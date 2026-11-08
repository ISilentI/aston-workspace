import java.util.Objects;

public class MyHashMap<K, V> {

    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;

    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = (Node<K, V>[]) new Node[INITIAL_CAPACITY];
        size = 0;
    }

    public V get(K key) {
        Objects.requireNonNull(key, "K = null");
        int index = indexFor(hash(key), table.length);
        Node<K, V> current = table[index];

        while (current != null) {
            if (key.equals(current.key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public V put(K key, V value) {
        Objects.requireNonNull(key, "K = null");
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        Node<K, V> current = table[index];
        while (current != null) {
            if (key.equals(current.key)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.next;
        }

        Node<K, V> newNode = new Node<>(key, value, table[index]);
        table[index] = newNode;
        size++;

        if (size > table.length * LOAD_FACTOR) {
            resize();
        }
        return null;
    }

    public V remove(K key) {
        Objects.requireNonNull(key, "K = null");
        int index = indexFor(hash(key), table.length);
        Node<K, V> current = table[index];
        Node<K, V> prev = null;

        while (current != null) {
            if (key.equals(current.key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    private int hash(K key) {
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = table.length * 2;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];

        for (Node<K, V> head : table) {
            Node<K, V> current = head;
            while (current != null) {
                Node<K, V> next = current.next;
                int newIndex = indexFor(hash(current.key), newCapacity);
                current.next = newTable[newIndex];
                newTable[newIndex] = current;
                current = next;
            }
        }
        table = newTable;
    }

    public int size() {
        return size;
    }
}