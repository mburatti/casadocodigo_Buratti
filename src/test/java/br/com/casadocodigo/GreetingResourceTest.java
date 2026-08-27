package br.com.casadocodigo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class GreetingResourceTest {
    @Test
    void healthEndpointReportsServiceStatus() {
        given()
            .when().get("/api/health")
            .then()
            .statusCode(200)
            .body(containsString("UP"))
            .body(containsString("srv-produto"));
    }

    @Test
    void rootEndpointRespondsWithApiInfo() {
        given()
            .when().get("/api")
            .then()
            .statusCode(200)
            .body(containsString("srv-produto"))
            .body(containsString("/api"));
    }

    @Test
    void helloEndpointRespondsWithIdentity() {
        given()
            .when().get("/api/hello")
            .then()
            .statusCode(200)
            .body(is("Hello from Quarkus REST"));
    }
}