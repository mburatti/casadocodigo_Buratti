package br.com.casadocodigo;

import br.com.casadocodigo.domain.Produto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;

@QuarkusTest
class ProdutoResourceTest {

    @BeforeEach
    @Transactional
    void resetDatabase() {
        Produto.deleteAll();
    }

    @Test
    void listProductsReturnsBookstoreCatalog() {
        given()
            .accept(ContentType.JSON)
            .when().get("/produtos")
            .then()
            .statusCode(200)
            .body("size()", is(0));
    }

    @Test
    void createProductPersistsAndReturnsStoredValues() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"titulo\":\"Clean Architecture\",\"descricao\":\"A practical guide\",\"paginas\":240}")
            .when().post("/produtos")
            .then()
            .statusCode(201)
            .body("titulo", is("Clean Architecture"))
            .body("descricao", is("A practical guide"))
            .body("paginas", is(240))
            .body("$", hasKey("id"));
    }

    @Test
    void rootPageContainsProductCreationForm() {
        given()
            .accept("text/html")
            .when().get("/")
            .then()
            .statusCode(200)
            .body(containsString("id=\"product-form\""))
            .body(containsString("name=\"titulo\""))
            .body(containsString("Create via POST"))
            .body(containsString("List via GET"));
    }
}
