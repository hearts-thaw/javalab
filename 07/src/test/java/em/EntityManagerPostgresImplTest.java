package em;

import models.User;
import org.junit.jupiter.api.*;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EntityManagerPostgresImplTest {

    private static PGSimpleDataSource dataSource;

    private static EntityManagerPostgresImpl entityManagerPostgres = null;

    @BeforeAll
    static void setUp() {
         dataSource = new PGSimpleDataSource();
         dataSource.setDatabaseName("postgres");
         dataSource.setUser("postgres");
         dataSource.setPassword("zyrf14aa");
         entityManagerPostgres = new EntityManagerPostgresImpl(dataSource);
    }

    @Order(1)
    @Test
    void testCreation() {
        assertDoesNotThrow(() -> entityManagerPostgres.createTable("person", User.class));
    }

    @Order(2)
    @Test
    void testInsertion() {
        assertDoesNotThrow(() -> entityManagerPostgres.save("person", new User(1L, "vasya", "vasya", false)));
    }

    @Order(3)
    @Test
    void testSelection() {
        assertEquals(new User(1L, "vasya", "vasya", false), entityManagerPostgres.findById("person", User.class, Long.class, 1L));
    }

    @AfterAll
    static void tearDown() {
        try (Connection conn = dataSource.getConnection("postgres", "zyrf14aa")) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE person;");
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
