package tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import model.DataTable;
import model.Row;

public class HashTable implements DataTable {
    private Row[] rows;
    private String name;
    private List<String> columns;
    private int degree;
    private int size;
    private int capacity;
    private int fingerprint;
    private int contamination; // New field for tombstones
    private static final int INITIAL_CAPACITY = 661; // An odd prime number between 10 and 50
    private static final double LOAD_FACTOR_BOUND = 0.75; // Load factor bound set to 75%
    private static final Row TOMBSTONE = new Row(null, null); // Static tombstone row
    private static final String SALT = "Nicholas Price";

    public HashTable(String name, List<String> columns) {
        this.name = name;
        this.columns = List.copyOf(columns);
        this.degree = columns.size();
        clear();
    }

    public void clear() {
        this.capacity = INITIAL_CAPACITY;
        this.rows = new Row[capacity];
        this.size = 0;
        this.fingerprint = 0;
        this.contamination = 0; // Initialize contamination to 0
    }

    public int degree() {
        return degree;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public String name() {
        return name;
    }

    public List<String> columns() {
        return columns;
    }

    public List<Object> remove(String key) {
        int index = hashFunction1(key);
        int step = hashFunction2(key);
        int i = index;
        int origHash = 0;

        while (rows[i] != null) {
            if (!rows[i].equals(TOMBSTONE) && key.equals(rows[i].key())) {
                List<Object> oldFields = rows[i].fields();
                origHash = rows[i].hashCode();
                rows[i] = TOMBSTONE; // Replace with tombstone
                size--;
                contamination++;
                fingerprint -= origHash; // Subtract the hash of the removed key
                return oldFields;
            }
            i = (i + step) % capacity; // Update the index
            if (index == i) {
                break;
            }
        }

        return null; // Return null when key not found
    }


    public int hashCode() {
        return fingerprint;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DataTable)) {
            return false;
        }
        DataTable other = (DataTable) obj;
        return fingerprint == other.hashCode();
    }

    private int hashFunction2(Object key) {
        int hashCode = key.hashCode();
        return 1 + Math.floorMod(hashCode, capacity - 1);
    }

    private int hashFunction1(Object key) {
        String saltedKey = SALT + key.toString();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(saltedKey.getBytes());
            BigInteger hash = new BigInteger( hashBytes);
            return Math.floorMod(hash.intValue(), capacity);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error computing hash: " + e.getMessage());
        }
    }

    public List<Object> put(String key, List<Object> fields) {
        if (key == null || fields == null) {
            throw new IllegalArgumentException("Key and fields cannot be null");
        }
        if (fields.size() != degree - 1) {
            throw new IllegalArgumentException("Fields must have " + (degree - 1) + " elements");
        }
        if (size >= capacity) {
            throw new IllegalStateException("Table is full");
        }

        Row newRow = new Row(key, Collections.unmodifiableList(fields));
        int index = hashFunction1(key);
        int step = hashFunction2(key);
        int i = index;
        int tombstoneIndex = -1; // Index to track tombstone for recycling

        // Probe until an empty slot or a tombstone is found
        while (rows[i] != null) {
            if (key.equals(rows[i].key())) {
                Row oldRows = rows[i];
                rows[i] = newRow; // Update existing row
				fingerprint-= oldRows.hashCode();
                fingerprint += newRow.hashCode();
                return oldRows.fields();
            } else if (rows[i] == TOMBSTONE && tombstoneIndex == -1) {
                tombstoneIndex = i; // Track the first tombstone for recycling
            }
            i = (i + step) % capacity;
            if (index == i) {
                throw new IllegalStateException("Table is full");
            }
        }

        // Insert the new row
        if (tombstoneIndex != -1) {
            rows[tombstoneIndex] = newRow; // Recycle tombstone
            size++;
            contamination--;
            fingerprint += newRow.hashCode(); // Add the hash of the new key
            return null; // No old fields
        } else {
            rows[i] = newRow;
            size++;
            fingerprint += newRow.hashCode(); // Add the hash of the new key
            return null; // No old fields
        }
    }

    public List<Object> get(String key) {
        int index = hashFunction1(key);
        int step = hashFunction2(key);
        int i = index;

        while (rows[i] != null) {
            if (!rows[i].equals(TOMBSTONE) && key.equals(rows[i].key())) {
                return rows[i].fields(); // Key found
            }
            i = (i + step) % capacity; // Update the index
            if (index == i) {
                // If we have probed all possible indices and haven't found the key,
                // break the loop to avoid infinite loop.
                break;
            }
        }

        return null; // Key not found
    }

    public Iterator<Row> iterator() {
        return new Iterator<Row>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                while (currentIndex < rows.length && (rows[currentIndex] == null || rows[currentIndex].key() == null)) {
                    currentIndex++;
                }
                return currentIndex < rows.length;
            }

            @Override
            public Row next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Row nextRow = rows[currentIndex++];
                while (nextRow == null || nextRow.key() == null) {
                    nextRow = rows[currentIndex++];
                }
                return nextRow;
            }
        };
    }

    public String toString() {
        return toTabularView(false);
    }

    private double loadFactor() {
        return ((double) (size + contamination)) / capacity;
    }
}

