package br.com.casadocodigo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class RootResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String landingPage() {
        return """
            {
              "service": "srv-produto",
              "status": "UP",
              "apiBase": "/api",
              "endpoints": {
                "health": "/api/health",
                "hello": "/api/hello",
                "produtos": "/api/produtos"
              }
            }
            """;
    }
}