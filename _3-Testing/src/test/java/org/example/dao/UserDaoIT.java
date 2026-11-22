package org.example.dao;

import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void setUp() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
        userDao = new UserDaoImpl(sessionFactory);
    }

    @Test
    void testCreateAndFindById() {
        User user = new User("Анжелика", "angeli@bk.ru", 36);
        User created = userDao.create(user);

        assertNotNull(created.getId());
        assertEquals("Анжелика", created.getName());
        assertNotNull(created.getCreatedAt());

        Optional<User> found = userDao.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("angeli@bk.ru", found.get().getEmail());
    }

    @Test
    void testFindAll() {
        userDao.create(new User("Александр", "alex@gmail.com", 24));
        userDao.create(new User("Мари", "mari@gmail.com", 31));

        List<User> users = userDao.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void testUpdate() {
        User user = userDao.create(new User("Валдимир", "vladmir@mail.ru", 47));
        user.setName("Владимир");
        user.setAge(48);

        User updated = userDao.update(user);
        assertEquals("Владимир", updated.getName());
        assertEquals(48, updated.getAge());
    }

    @Test
    void testDelete() {
        User user = userDao.create(new User("Всеволод", "vsevo@bk.", 28));
        boolean deleted = userDao.deleteById(user.getId());
        assertTrue(deleted);
        assertTrue(userDao.findById(user.getId()).isEmpty());
    }

    @Test
    void testDeleteNonExisting() {
        boolean deleted = userDao.deleteById(999L);
        assertFalse(deleted);
    }
}