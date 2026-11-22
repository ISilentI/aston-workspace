package org.example.dao;

import org.example.entity.User;
import org.example.exception.DaoException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User create(User user) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            log.info("Создан пользователь: {}", user);
            return user;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            log.error("Ошибка при создании пользователя", e);
            throw new DaoException("Не удалось создать пользователя", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя по id={}", id, e);
            throw new DaoException("Не удалось найти пользователя", e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<User> users = session.createQuery("FROM User ORDER BY id", User.class).list();
            log.debug("Найдено {} пользователей", users.size());
            return users;
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            throw new DaoException("Не удалось получить список пользователей", e);
        }
    }

    @Override
    public User update(User user) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            User merged = session.merge(user);
            tx.commit();
            log.info("Обновлён пользователь: {}", merged);
            return merged;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            log.error("Ошибка при обновлении пользователя id={}", user.getId(), e);
            throw new DaoException("Не удалось обновить пользователя", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user == null) {
                tx.commit();
                log.warn("Пользователь не найден для удаления id={}", id);
                return false;
            }
            session.remove(user);
            tx.commit();
            log.info("Удалён пользователь с id={}", id);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            log.error("Ошибка при удалении пользователя id={}", id, e);
            throw new DaoException("Не удалось удалить пользователя", e);
        }
    }
}
