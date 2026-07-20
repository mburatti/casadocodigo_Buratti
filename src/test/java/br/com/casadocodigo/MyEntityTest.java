package br.com.casadocodigo;

import br.com.casadocodigo.MyEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MyEntityTest {

    @BeforeEach
    @Transactional
    void clearTable() {
        MyEntity.deleteAll();
    }

    @Test
    @Transactional
    void persistAndRetrieveEntityByField() {
        // Arrange
        MyEntity entity = new MyEntity();
        entity.field = "test-value";

        // Act
        entity.persist();

        // Assert
        MyEntity found = MyEntity.findById(entity.id);
        assertNotNull(found);
        assertEquals("test-value", found.field);
    }

    @Test
    @Transactional
    void listAllReturnsAllPersistedEntities() {
        // Arrange
        MyEntity a = new MyEntity();
        a.field = "alpha";
        MyEntity b = new MyEntity();
        b.field = "beta";
        a.persist();
        b.persist();

        // Act
        List<MyEntity> all = MyEntity.listAll();

        // Assert
        assertEquals(2, all.size());
    }

    @Test
    @Transactional
    void countReflectsNumberOfStoredEntities() {
        // Arrange
        MyEntity entity = new MyEntity();
        entity.field = "counted";
        entity.persist();

        // Assert
        assertEquals(1, MyEntity.count());
    }

    @Test
    @Transactional
    void deleteRemovesEntityFromDatabase() {
        // Arrange
        MyEntity entity = new MyEntity();
        entity.field = "to-be-deleted";
        entity.persist();
        Long id = entity.id;

        // Act
        entity.delete();

        // Assert
        assertNull(MyEntity.findById(id));
    }

    @Test
    @Transactional
    void persistEntityWithNullFieldIsAllowed() {
        // Arrange — field column has no @NotNull, so null is a valid state
        MyEntity entity = new MyEntity();
        entity.field = null;

        // Act
        entity.persist();

        // Assert
        MyEntity found = MyEntity.findById(entity.id);
        assertNotNull(found);
        assertNull(found.field);
    }
}
