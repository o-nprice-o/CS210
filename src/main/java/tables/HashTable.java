package tables;

import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
	private static final int INITIAL_CAPACITY = 503; // An odd prime number

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
		throw new UnsupportedOperationException();
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

		private static final String SALT = "Nicholas Price";

		private int hashFunction(String key) {
		    String saltedKey = SALT + key;
		    int p = 31; 
		    int m = capacity; 
		    long hashValue = 0;
		    long pPow = 1;

		    for (char c : saltedKey.toCharArray()) {
		        hashValue = (hashValue + (c - 'a' + 1) * pPow) % m;
		        pPow = (pPow * p) % m;
		    }

		    return Math.floorMod(hashValue, m);
		}

		public List<Object> put(String key, List<Object> fields) {
		    if (fields == null) {
		        throw new IllegalArgumentException("Fields cannot be null");
		    }
		    if (fields.size() != degree - 1) {
		        throw new IllegalArgumentException("Fields must have " + (degree - 1) + " elements");
		    }
		    Row newRow = new Row(key, Collections.unmodifiableList(fields));
		    int index = hashFunction(key);
		    while (rows[index] != null && !rows[index].key().equals(key)) {
		        index = (index + 1) % capacity;
		        if (index == hashFunction(key)) {
		            throw new IllegalStateException("Array is full");
		        }
		    }
		    List<Object> oldFields = null;
		    if (rows[index] != null) {
		        oldFields = rows[index].fields();
		        fingerprint -= rows[index].hashCode();
		    } else {
		        size++; 
		    }
		    rows[index] = newRow;
		    fingerprint += newRow.hashCode(); 
		    return oldFields;
		}


	public List<Object> get(String key) {
		int index = hashFunction(key);
		while (rows[index] != null && !rows[index].key().equals(key)) {
			index = (index + 1) % capacity;
			if (index == hashFunction(key)) {
				throw new IllegalStateException("Array is full");
			}
		}
		return rows[index] != null ? rows[index].fields() : null;
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
}
