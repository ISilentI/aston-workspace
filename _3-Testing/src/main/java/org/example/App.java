package org.example;

import org.example.entity.User;
import org.example.exception.DaoException;
import org.example.service.UserService;
import org.example.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private final UserService userService;
    private final Scanner scanner;

    public App(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        var sessionFactory = HibernateUtil.getSessionFactory();
        var userDao = new org.example.dao.UserDaoImpl(sessionFactory);
        var userService = new UserService(userDao);
        var scanner = new Scanner(System.in);

        App app = new App(userService, scanner);
        app.run();

        HibernateUtil.shutdown();
        scanner.close();
    }

    public void run() {
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
            } catch (IllegalArgumentException e) {
                System.err.println("Ошибка данных: " + e.getMessage());
                log.warn("Ошибка валидации: {}", e.getMessage());
            } catch (DaoException e) {
                System.err.println("Ошибка базы данных: " + e.getMessage());
                log.error("DaoException", e);
            } catch (Exception e) {
                System.err.println("Непредвиденная ошибка: " + e.getMessage());
                log.error("Непредвиденная ошибка", e);
            }
        }

        log.info("Завершение user-service.");
    }

    private void printMenu() {
        System.out.println("\n--- USER SERVICE ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        User created = userService.createUser(name, email, age);
        System.out.println("Пользователь создан: " + created);
    }

    private void findUserById() {
        System.out.print("Введите ID: ");
        long id = Long.parseLong(scanner.nextLine().trim());
        Optional<User> user = userService.getUserById(id);
        user.ifPresentOrElse(
                u -> System.out.println("Найден: " + u),
                () -> System.out.println("Пользователь с ID " + id + " не найден.")
        );
    }

    private void findAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
        } else {
            System.out.println("Всего пользователей: " + users.size());
            users.forEach(System.out::println);
        }
    }

    private void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        long id = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Новое имя (Enter - пропустить): ");
        String name = scanner.nextLine().trim();

        System.out.print("Новый email (Enter - пропустить): ");
        String email = scanner.nextLine().trim();

        System.out.print("Новый возраст (Enter - пропустить): ");
        String ageStr = scanner.nextLine().trim();
        Integer age = ageStr.isEmpty() ? null : Integer.parseInt(ageStr);

        User updated = userService.updateUser(id, name, email, age);
        System.out.println("Пользователь обновлён: " + updated);
    }

    private void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        long id = Long.parseLong(scanner.nextLine().trim());
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            System.out.println("Пользователь с ID " + id + " удалён.");
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }
}