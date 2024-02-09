package tables;
//Nicholas Price

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
		this.columns = List.copyOf(columns); // creates an immutable list
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
		if (fields.size() + 1 > degree) {
			throw new IllegalArgumentException("Row is too wide");
		}
		if(1+ fields.size() < degree) {
			throw new IllegalArgumentException("Row is too narrow");
		}
		Row newRow = new Row(key, fields);
		for (int i = 0; i < size; i++) {
			if (rows[i].key().equals(key)) {
				List<Object> oldFields = rows[i].fields();
				fingerprint -= rows[i].hashCode();
				rows[i] = newRow;
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
		size++;
		return null;
	}

	@Override
	public List<Object> get(String key) {
		for(int i = 0; i < size; i++) {
			if(rows[i].key().equals(key)) {
				return rows[i].fields();
			}
		}
		return null;
	}

	@Override
	public List<Object> remove(String key) {
		for(int i = 0; i < size; i++) {
			if(rows[i].key().equals(key)) {
				List<Object> oldFields = rows[i].fields();
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
	
	@Override
	public String toTabularView(boolean sorted) {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("\n");
		sb.append(String.join(" | ", columns)).append("\n");
		Row[] rowsToPrint = Arrays.copyOf(rows, size);
		if (sorted) {
			Arrays.sort(rowsToPrint);
		}
		for (Row row : rowsToPrint) {
			sb.append(row.key()).append(": ").append(row.fields().toString()).append("\n");
		}
		return sb.toString();
	}
}
