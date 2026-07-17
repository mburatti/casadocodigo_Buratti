package br.com.casadocodigo.domain;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/produtos")
public class ProdutoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Produto> list() {
        return Produto.listAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Produto payload) {
        payload.persist();
        return Response.status(Response.Status.CREATED).entity(payload).build();
    }
}
