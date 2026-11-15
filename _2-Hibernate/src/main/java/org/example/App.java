package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.entity.User;
import org.example.exception.DaoException;
import org.example.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        log.info("Начало user-service...");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> findUserById();
                    case "3" -> findAllUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    case "0" -> running = false;
                    default -> System.out.println("Ошибка выбора.");
                }
            } catch (DaoException e) {
                System.err.println(e.getMessage());
                log.error("DaoException", e);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                log.error("Непредвиденная ошибка", e);
            }
        }

        HibernateUtil.shutdown();
        scanner.close();
        log.info("Завершение user-service.");
    }

    private static void printMenu() {
        System.out.println("\n--- USER SERVICE ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.println("Выберите действие: ");
    }

    private static void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        User user = new User(name, email, age);
        User created = userDao.create(user);
        System.out.println("Пользователь создан: " + created);
    }

    private static void findUserById() {
        System.out.print("Введите ID: ");
        long id = Long.parseLong(scanner.nextLine().trim());
        Optional<User> user = userDao.findById(id);
        user.ifPresentOrElse(
                u -> System.out.println("Найден: " + u),
                () -> System.out.println("Пользователь с ID " + id + " не найден.")
        );
    }

    private static void findAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
        } else {
            System.out.println("Всего пользователей: " + users.size());
            users.forEach(System.out::println);
        }
    }

    private static void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        long id = Long.parseLong(scanner.nextLine().trim());

        Optional<User> existing = userDao.findById(id);
        if (existing.isEmpty()) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }

        User user = existing.get();
        System.out.print("Новое имя (" + user.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) user.setName(name);

        System.out.print("Новый email (" + user.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) user.setEmail(email);

        System.out.print("Новый возраст (" + user.getAge() + "): ");
        String ageStr = scanner.nextLine().trim();
        if (!ageStr.isEmpty()) user.setAge(Integer.parseInt(ageStr));

        User updated = userDao.update(user);
        System.out.println("Пользователь обновлён: " + updated);
    }

    private static void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        long id = Long.parseLong(scanner.nextLine().trim());
        boolean deleted = userDao.deleteById(id);
        if (deleted) {
            System.out.println("Пользователь с ID " + id + " удалён.");
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }
}
