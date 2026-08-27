package br.com.casadocodigo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RootResourceTest {

    private final RootResource resource = new RootResource();

    @Test
    void rootEndpointReturnsApiMetadata() {
        String payload = resource.landingPage();

        assertNotNull(payload);
        assertTrue(payload.contains("\"service\": \"srv-produto\""));
        assertTrue(payload.contains("\"apiBase\": \"/api\""));
        assertTrue(payload.contains("\"health\": \"/api/health\""));
    }

    @Test
    void rootEndpointAdvertisesProductApi() {
        String payload = resource.landingPage();

        assertTrue(payload.contains("\"produtos\": \"/api/produtos\""));
    }
}
