package tables;

import java.util.Iterator;
import java.util.List;
import java.util.*;
import model.DataTable;
import model.Row;
import model.Table;

public class SearchTable implements DataTable {
	private Row[] rows;
	private String name;
	private List<String> columns;
	private int degree;
	private int size;
	private int capacity;
	private static final int INITIAL_CAPACITY = 16;
	private int fingerprint;

	public SearchTable(String name, List<String> columns) {
		this.name = name;
		this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
		this.degree = columns.size();
		clear();
	}

	@Override
	public void clear() {
		this.capacity = INITIAL_CAPACITY;
		this.rows = new Row[this.capacity];
		this.size = 0;
		this.fingerprint = 0;
	}

	@Override
	public List<Object> put(String key, List<Object> fields) {
		if (fields.size() != degree) {
			throw new IllegalArgumentException("Degree of given row does not match the degree field of the table.");
		}
		Row newRow = new Row(key, fields);
		for (int i = 0; i < size; i++) {
			if (rows[i].getKey().equals(key)) {
				List<Object> oldFields = rows[i].getFields();
				rows[i] = newRow;
				fingerprint -= rows[i].hashCode();
				fingerprint += newRow.hashCode();
				return oldFields;
			}
		}
		if (size == capacity) {
			capacity *= 2;
			rows = Arrays.copyOf(rows, capacity);
		}
		rows[size] = newRow;
		fingerprint += newRow.hashCode();
		return null;
	}

	@Override
	public List<Object> get(String key) {
		for(int i = 0; i < size; i++) {
			if(rows[i].getKey().equals(key)) {
				return rows[i].getFields();
			}
		}
		return null;
	}

	@Override
	public List<Object> remove(String key) {
		for(int i = 0; i < size; i++) {
			if(rows[i].getKey().equals(key)) {
				List<Object> oldFields = rows[i].getFields();
				fingerprint -= rows[i].hashCode();
				rows[i] = rows[--size];
				rows[size] = null;
				return oldFields;
			}
		}
		return null;
	}

	@Override
	public int degree() {
		return degree;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public double loadFactor() {
		return (double) size / capacity;
	}

	@Override
	public int hashCode() {
		return fingerprint;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return obj.hashCode() == this.hashCode();
	}

	@Override
	public Iterator<Row> iterator() {
		return new Iterator<Row>() {
			int currentIndex = 0;

			@Override
			public boolean hasNext() {
				return currentIndex < size;
			}

			@Override
			public Row next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				int old = currentIndex;
				currentIndex++;
				return rows[old];
			}
		};
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<String> columns() {
		return columns;
	}

	@Override
	public String toString() {
		return toTabularView(true);
	}
	
	public class Row{
		private String key;
		private List<Object> fields;
		
		public Row(String key, List<Object> fields) {
			this.key = key;
			this.fields = new ArrayList<>(fields);
		}
		
		public String getKey() {
			return key;
		}
		
		public List<Object> getFields() {
			return fields;
		}
	}
}
