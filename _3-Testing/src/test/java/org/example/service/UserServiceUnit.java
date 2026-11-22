package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.example.exception.DaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnit {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ReturnCreatedUser() {
        User expectedUser = new User("Анжелика", "angeli@bk.ru", 36);
        expectedUser.setId(1L);
        expectedUser.setCreatedAt(LocalDateTime.now());
        when(userDao.create(any(User.class))).thenReturn(expectedUser);

        User result = userService.createUser("Анжелика", "angeli@bk.ru", 36);

        assertNotNull(result);
        assertEquals("Анжелика", result.getName());
        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    void createUser_EmptyName_ThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("", "angeli@bk.ru", 36);
        });
        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void createUser_InvalidEmail_ThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("Анжелика", "vladi", 36);
        });
        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void createUser_NegativeAge_ThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("Анжелика", "angeli@bk.ru", -36);
        });
        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void getUserById_Exists_ReturnUser() {
        User user = new User("Александр", "alex@gmail.com", 24);
        user.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("Александр", result.get().getName());
    }

    @Test
    void getUserById_NotExists_ReturnEmpty() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_ReturnList() {
        User user1 = new User("A", "a@a.com", 1);
        User user2 = new User("B", "b@b.com", 2);
        when(userDao.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void updateUser_Exists_UpdateAndReturn() {
        User existing = new User("Old", "old@example.com", 30);
        existing.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(existing));
        when(userDao.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, "New", "new@example.com", 31);

        assertEquals("New", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(31, result.getAge());
        verify(userDao).update(existing);
    }

    @Test
    void updateUser_NotExists_ThrowException() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DaoException.class, () -> {
            userService.updateUser(99L, "Name", "email@test.com", 20);
        });
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    void deleteUser_ReturnTrue() {
        when(userDao.deleteById(1L)).thenReturn(true);

        assertTrue(userService.deleteUser(1L));
        verify(userDao).deleteById(1L);
    }
}