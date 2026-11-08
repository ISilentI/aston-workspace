public class Main {
    public static void main(String[] args) {
        MyHashMap<Integer, String> phoneBook = new MyHashMap<>();

        phoneBook.put(1, "Анна");
        phoneBook.put(2, "Иван");
        phoneBook.put(3, "Мария");

        System.out.println("Ключ 2: " + phoneBook.get(2));

        String old = phoneBook.put(1, "Анна Петровна");
        System.out.println("\nСтарое значение: " + old);
        System.out.println("Новое значение: " + phoneBook.get(1));

        String removed = phoneBook.remove(3);
        System.out.println("\nУдалено: " + removed);
        System.out.println("После удаления: " + phoneBook.get(3));
    }
}