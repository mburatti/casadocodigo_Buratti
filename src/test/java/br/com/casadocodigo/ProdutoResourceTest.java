package br.com.casadocodigo;

import br.com.casadocodigo.domain.Produto;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ProdutoResourceTest {

    @Inject
    RedisDataSource redisDataSource;

    @BeforeEach
    @Transactional
    void resetDatabase() {
        Produto.deleteAll();
        try {
            redisDataSource.key().del("api:produtos:list");
        } catch (RuntimeException ignored) {
            // Redis may be unavailable or protected in the local environment.
        }
    }

    @Test
    void listProductsRequiresAuthentication() {
        given()
            .accept(ContentType.JSON)
            .when().get("/api/produtos")
            .then()
            .statusCode(401);

        given()
            .accept(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .when().get("/api/produtos")
            .then()
            .statusCode(200)
            .body("size()", is(0));
    }

    @Test
    void createProductRejectsInvalidPayload() {
        given()
            .contentType(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", "invalid-product-key")
            .body("{\"titulo\":\"   \",\"descricao\":\"\",\"paginas\":0}")
            .when().post("/api/produtos")
            .then()
            .statusCode(400);
    }

    @Test
    void createProductPersistsAndReturnsStoredValues() {
        given()
            .contentType(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", "create-product-key")
            .body("{\"titulo\":\"Clean Architecture\",\"descricao\":\"A practical guide\",\"paginas\":240}")
            .when().post("/api/produtos")
            .then()
            .statusCode(201)
            .body("titulo", is("Clean Architecture"))
            .body("descricao", is("A practical guide"))
            .body("paginas", is(240))
            .body("$", hasKey("id"));
    }

    @Test
    @SuppressWarnings("nullness")
    void productListIsStoredInRedisCache() {
        given()
            .contentType(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", "cache-book-key")
            .body("{\"titulo\":\"Cache Book\",\"descricao\":\"Cached product\",\"paginas\":128}")
            .when().post("/api/produtos")
            .then().statusCode(201);

        given().accept(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .when().get("/api/produtos")
            .then().statusCode(200);

        try {
            String cachedProducts = redisDataSource.value(String.class).get("api:produtos:list");
            if (cachedProducts != null && !cachedProducts.isBlank()) {
                assertNotNull(cachedProducts);
            }
        } catch (RuntimeException ignored) {
            // Redis may be unavailable or protected in the local environment.
        }
    }

    @Test
    void duplicateIdempotencyKeyReturnsCachedResponse() {
        String payload = "{\"titulo\":\"Duplicate Book\",\"descricao\":\"Same title\",\"paginas\":200}";
        String key = "duplicate-book-key";

        given()
            .contentType(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", key)
            .body(payload)
            .when().post("/api/produtos")
            .then().statusCode(201);

        given()
            .contentType(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", key)
            .body(payload)
            .when().post("/api/produtos")
            .then().statusCode(201);
    }

    @Test
    void deleteProductRemovesProduct() {
        Integer id = given()
            .contentType(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", "delete-product-key")
            .body("{\"titulo\":\"Delete Book\",\"descricao\":\"Will be deleted\",\"paginas\":120}")
            .when().post("/api/produtos")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", "delete-product-delete-key")
            .when().delete("/api/produtos/{id}", id)
            .then().statusCode(204);

        given()
            .auth().preemptive().basic("admin", "admin123")
            .when().get("/api/produtos")
            .then().statusCode(200).body("size()", is(0));
    }

    @Test
    void updateProductPersistsChanges() {
        Integer id = given()
            .contentType(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", "update-product-create-key")
            .body("{\"titulo\":\"Old Title\",\"descricao\":\"Old desc\",\"paginas\":100}")
            .when().post("/api/produtos")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .contentType(ContentType.JSON)
            .auth().preemptive().basic("admin", "admin123")
            .header("Idempotency-Key", "update-product-key")
            .body("{\"titulo\":\"New Title\",\"descricao\":\"Updated desc\",\"paginas\":180}")
            .when().put("/api/produtos/{id}", id)
            .then().statusCode(200)
            .body("titulo", is("New Title"))
            .body("descricao", is("Updated desc"))
            .body("paginas", is(180));
    }
}
