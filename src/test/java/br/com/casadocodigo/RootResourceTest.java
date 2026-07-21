package br.com.casadocodigo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RootResourceTest {

    private final RootResource resource = new RootResource();

    @Test
    void landingPageReturnsValidHtmlDocument() {
        // Arrange & Act
        String html = resource.landingPage();

        // Assert
        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("</html>"));
    }

    @Test
    void landingPageContainsExpectedTitle() {
        // Arrange & Act
        String html = resource.landingPage();

        // Assert
        assertTrue(html.contains("SRV Produto API Explorer"));
    }

    @Test
    void landingPageAdvertisesProdutosEndpoint() {
        // Arrange & Act
        String html = resource.landingPage();

        // Assert — products endpoint must be referenced in the page
        assertTrue(html.contains("/produtos"));
    }

    @Test
    void landingPageContainsProductCreationForm() {
        // Arrange & Act
        String html = resource.landingPage();

        // Assert — form elements must be present for product creation
        assertTrue(html.contains("id=\"product-form\""));
        assertTrue(html.contains("name=\"titulo\""));
        assertTrue(html.contains("name=\"descricao\""));
        assertTrue(html.contains("name=\"paginas\""));
    }

    @Test
    void landingPageContainsLinksToOtherEndpoints() {
        // Arrange & Act
        String html = resource.landingPage();

        // Assert — navigation links to peer endpoints must be present
        assertTrue(html.contains("/health"));
        assertTrue(html.contains("/hello"));
    }
}
