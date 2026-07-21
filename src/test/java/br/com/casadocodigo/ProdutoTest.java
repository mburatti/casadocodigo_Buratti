package br.com.casadocodigo;

import br.com.casadocodigo.domain.Produto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ProdutoTest {

    @BeforeEach
    @Transactional
    void clearTable() {
        Produto.deleteAll();
    }

    @Test
    @Transactional
    void persistAndRetrieveProdutoFields() {
        // Arrange
        Produto produto = new Produto();
        produto.titulo = "Clean Architecture";
        produto.descricao = "A practical guide to software design";
        produto.paginas = 432;

        // Act
        produto.persist();

        // Assert
        Produto found = Produto.findById(produto.id);
        assertNotNull(found);
        assertEquals("Clean Architecture", found.titulo);
        assertEquals("A practical guide to software design", found.descricao);
        assertEquals(432, found.paginas);
    }

    @Test
    @Transactional
    void listAllReturnsAllPersistedProdutos() {
        // Arrange
        Produto p1 = new Produto();
        p1.titulo = "Domain-Driven Design";
        p1.descricao = "Tackling complexity in the heart of software";
        p1.paginas = 560;

        Produto p2 = new Produto();
        p2.titulo = "The Pragmatic Programmer";
        p2.descricao = "Your journey to mastery";
        p2.paginas = 352;

        p1.persist();
        p2.persist();

        // Act
        List<Produto> all = Produto.listAll();

        // Assert
        assertEquals(2, all.size());
    }

    @Test
    @Transactional
    void countReflectsNumberOfStoredProdutos() {
        // Arrange
        Produto produto = new Produto();
        produto.titulo = "Refactoring";
        produto.descricao = "Improving the design of existing code";
        produto.paginas = 448;
        produto.persist();

        // Assert
        assertEquals(1, Produto.count());
    }

    @Test
    @Transactional
    void deleteProdutoRemovesItFromDatabase() {
        // Arrange
        Produto produto = new Produto();
        produto.titulo = "To Be Deleted";
        produto.descricao = "Temporary entry";
        produto.paginas = 1;
        produto.persist();
        Long id = produto.id;

        // Act
        produto.delete();

        // Assert
        assertNull(Produto.findById(id));
    }

    @Test
    @Transactional
    void persistProdutoWithZeroPaginasIsAllowed() {
        // Arrange — paginas is a primitive int; zero is its default value
        Produto produto = new Produto();
        produto.titulo = "Untitled Draft";
        produto.paginas = 0;

        // Act
        produto.persist();

        // Assert
        Produto found = Produto.findById(produto.id);
        assertNotNull(found);
        assertEquals(0, found.paginas);
    }
}
