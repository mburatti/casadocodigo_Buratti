package br.com.casadocodigo;

import br.com.casadocodigo.domain.HealthResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class HealthResourceTest {

    // ── HTTP endpoint tests ──────────────────────────────────────────────────

    @Test
    void healthEndpointReturnsStatusUp() {
        given()
            .when().get("/api/health")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }

    @Test
    void healthEndpointReturnsServiceName() {
        given()
            .when().get("/api/health")
            .then()
            .statusCode(200)
            .body("service", is("srv-produto"));
    }

    // ── HealthResponse record unit tests (no infrastructure required) ────────

    @Test
    void healthResponseRecordExposesStatusAccessor() {
        // Arrange & Act
        HealthResource.HealthResponse response = new HealthResource.HealthResponse("UP", "srv-produto");

        // Assert
        assertEquals("UP", response.status());
    }

    @Test
    void healthResponseRecordExposesServiceAccessor() {
        // Arrange & Act
        HealthResource.HealthResponse response = new HealthResource.HealthResponse("DOWN", "srv-produto");

        // Assert
        assertEquals("srv-produto", response.service());
    }

    @Test
    void healthResponseRecordEqualityIsValueBased() {
        // Arrange
        HealthResource.HealthResponse r1 = new HealthResource.HealthResponse("UP", "srv-produto");
        HealthResource.HealthResponse r2 = new HealthResource.HealthResponse("UP", "srv-produto");

        // Assert — records use structural equality
        assertEquals(r1, r2);
    }

    @Test
    void healthResponseRecordToStringContainsFields() {
        // Arrange
        HealthResource.HealthResponse response = new HealthResource.HealthResponse("UP", "srv-produto");

        // Assert
        String str = response.toString();
        assertTrue(str.contains("UP"));
        assertTrue(str.contains("srv-produto"));
    }
}
