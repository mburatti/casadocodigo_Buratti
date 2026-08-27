package br.com.casadocodigo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.SetArgs;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/api")
@RolesAllowed("admin")
public class ProdutoResource {

    private static final String PRODUTOS_CACHE_KEY = "api:produtos:list";
    private static final String IDEMPOTENCY_PREFIX = "idempotency:produtos:";
    private static final long CACHE_TTL_SECONDS = 300;
    private static final long IDEMPOTENCY_TTL_SECONDS = 86400;
    private static final Map<String, Response> IDEMPOTENCY_MEMORY = new ConcurrentHashMap<>();

    @FunctionalInterface
    private interface ResponseAction {
        Response execute();
    }

    private static class IdempotencyResponsePayload {
        public int statusCode;
        public Object payload;

        public IdempotencyResponsePayload(int statusCode, Object payload) {
            this.statusCode = statusCode;
            this.payload = payload;
        }
    }

    @Inject
    RedisDataSource redisDataSource;

    @Inject
    ObjectMapper objectMapper;

    @GET
    @Path("/produtos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Produto> list() {
        String cached = readRedisString(PRODUTOS_CACHE_KEY);
        if (cached != null && !cached.isBlank()) {
            try {
                return objectMapper.readValue(
                    cached,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Produto.class)
                );
            } catch (JsonProcessingException ignored) {
                // Redis is optional; fall back to the database result.
            }
        }

        List<Produto> produtos = Produto.listAll();
        try {
            String json = objectMapper.writeValueAsString(produtos);
            writeRedisString(PRODUTOS_CACHE_KEY, json, CACHE_TTL_SECONDS);
        } catch (JsonProcessingException ignored) {
            // Redis is optional; continue without caching if unavailable.
        }
        return produtos;
    }

    @POST
    @Path("/produtos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Produto payload, @HeaderParam("Idempotency-Key") String idempotencyKey) {
        return executeIdempotent(idempotencyKey, () -> {
            validateProductPayload(payload);
            if (isDuplicate(payload)) {
                return Response.status(Response.Status.CONFLICT)
                    .entity("Produto duplicado.")
                    .build();
            }

            payload.persist();
            evictCache();
            return Response.status(Response.Status.CREATED).entity(payload).build();
        });
    }

    @DELETE
    @Path("/produtos/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id, @HeaderParam("Idempotency-Key") String idempotencyKey) {
        return executeIdempotent(idempotencyKey, () -> {
            Produto produto = Produto.findById(id);
            if (produto == null) {
                throw new NotFoundException("Produto não encontrado.");
            }

            produto.delete();
            evictCache();
            return Response.noContent().build();
        });
    }

    @PUT
    @Path("/produtos/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(@PathParam("id") Long id, Produto payload,
                          @HeaderParam("Idempotency-Key") String idempotencyKey) {
        return executeIdempotent(idempotencyKey, () -> {
            Produto existing = Produto.findById(id);
            if (existing == null) {
                throw new NotFoundException("Produto não encontrado.");
            }

            validateProductPayload(payload);

            existing.titulo = payload.titulo.trim();
            existing.descricao = payload.descricao.trim();
            existing.paginas = payload.paginas;

            existing.persist();
            evictCache();
            return Response.ok(existing).build();
        });
    }

    @SuppressWarnings("nullness")
    private Response executeIdempotent(String idempotencyKey, ResponseAction action) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Idempotency-Key header is required.")
                .build();
        }

        final String cacheKey = IDEMPOTENCY_PREFIX + idempotencyKey.trim();
        Response memoryResponse = IDEMPOTENCY_MEMORY.get(cacheKey);
        if (memoryResponse != null) {
            return memoryResponse;
        }

        String cachedStatus = readRedisString(cacheKey);
        if (cachedStatus != null && !cachedStatus.isBlank()) {
            if ("IN_PROGRESS".equals(cachedStatus)) {
                return Response.status(Response.Status.CONFLICT)
                    .entity("Request already in progress.")
                    .build();
            }

            try {
                IdempotencyResponsePayload cachedResponse = objectMapper.readValue(
                    cachedStatus,
                    IdempotencyResponsePayload.class
                );
                Response replayResponse = Response.status(cachedResponse.statusCode).entity(cachedResponse.payload).build();
                IDEMPOTENCY_MEMORY.put(cacheKey, replayResponse);
                return replayResponse;
            } catch (JsonProcessingException ignored) {
                // Redis is optional; continue without replaying the cached response.
            }
        }

        try {
            writeRedisString(cacheKey, "IN_PROGRESS", IDEMPOTENCY_TTL_SECONDS);
        } catch (RuntimeException ignored) {
            // Redis is optional; continue without idempotency protection when unavailable.
        }

        Response response = action.execute();
        try {
            String json = objectMapper.writeValueAsString(
                new IdempotencyResponsePayload(response.getStatus(), response.getEntity())
            );
            writeRedisString(cacheKey, json, IDEMPOTENCY_TTL_SECONDS);
        } catch (JsonProcessingException ignored) {
            // Redis is optional; continue without storing the cached response.
        }

        IDEMPOTENCY_MEMORY.put(cacheKey, response);
        return response;
    }

    @SuppressWarnings("nullness")
    private String readRedisString(String key) {
        try {
            return redisDataSource.value(String.class).get(key);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private void writeRedisString(String key, String value, long ttlSeconds) {
        try {
            if (value == null || value.isBlank()) {
                return;
            }
            redisDataSource.value(String.class).set(key, value, new SetArgs().ex(ttlSeconds));
        } catch (RuntimeException ignored) {
            // Redis is optional; continue without caching if unavailable.
        }
    }

    private void evictCache() {
        try {
            redisDataSource.key().del(PRODUTOS_CACHE_KEY);
        } catch (RuntimeException ignored) {
            // Redis is optional; DB write still succeeds.
        }
    }

    private void validateProductPayload(Produto payload) {
        if (payload == null) {
            throw new BadRequestException("Produto inválido.");
        }
        if (payload.titulo == null || payload.titulo.isBlank()) {
            throw new BadRequestException("Titulo é obrigatório.");
        }
        if (payload.descricao == null || payload.descricao.isBlank()) {
            throw new BadRequestException("Descricao é obrigatória.");
        }
        if (payload.paginas <= 0) {
            throw new BadRequestException("Paginas deve ser maior que zero.");
        }
    }

    private boolean isDuplicate(Produto payload) {
        if (payload == null) {
            return true;
        }

        String titulo = payload.titulo == null ? "" : payload.titulo.trim();
        String descricao = payload.descricao == null ? "" : payload.descricao.trim();
        int paginas = payload.paginas;

        return Produto.find("lower(titulo) = ?1 and lower(descricao) = ?2 and paginas = ?3",
                titulo.toLowerCase(), descricao.toLowerCase(), paginas)
            .firstResult() != null;
    }
}
