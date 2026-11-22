package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.example.exception.DaoException;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Пустое поле имени");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        if (age == null || age < 0) {
            throw new IllegalArgumentException("Возраст должен быть > 0");
        }

        User user = new User(name.trim(), email.trim(), age);
        return userDao.create(user);
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        User existing = userDao.findById(id)
                .orElseThrow(() -> new DaoException("Пользователь с ID " + id + " не найден", null));

        if (name != null && !name.trim().isEmpty()) existing.setName(name.trim());
        if (email != null && email.contains("@")) existing.setEmail(email.trim());
        if (age != null && age >= 0) existing.setAge(age);

        return userDao.update(existing);
    }

    public boolean deleteUser(Long id) {
        return userDao.deleteById(id);
    }
}