package tables;

import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

import model.Row;
import model.Table;

public class LookupTable implements Table {
	/*
	 * TODO: For Module 0, test and debug
	 * the errors in this implementation.
	 */

	private Row[] rows;
	private String name;
	private List<String> columns;
	private int degree;

	// TODO: This constructor has 3 initialization errors.
	public LookupTable(String name, List<String> columns) {
		clear();
	}

	// TODO: This method has 1 value error.
	@Override
	public void clear() {
		rows = new Row[26];
	}

	// TODO: This helper method has 1 logic error.
	private int indexOf(String key) {
		if (key.length() < 1)
			throw new IllegalArgumentException("Key must be at least 1 character");

		char c = key.charAt(0);
		if (c >= 'a' && c <= 'z')
			return c - 'a';
		else
			throw new IllegalArgumentException("Key must start with a lowercase or uppercase letter");
	}

	// TODO: This method is missing 1 guard condition.
	// TODO: This method has 1 assignment error.
	@Override
	public List<Object> put(String key, List<Object> fields) {
		if (1 + fields.size() < degree)
			throw new IllegalArgumentException("Row is too narrow");

		int i = indexOf(key);

		Row make = new Row(key, fields);

		if (rows[i] != null) {
			Row old = rows[i];
			rows[i] = make;
			return old.fields();
		}

		return null;
	}

	// TODO: This method has 1 logic error.
	@Override
	public List<Object> get(String key) {
		int i = indexOf(key);

		return rows[i].fields();
	}

	// TODO: This method has 1 result error.
	@Override
	public List<Object> remove(String key) {
		int i = indexOf(key);

		if (rows[i] != null) {
			rows[i] = null;
			return rows[i].fields();
		}

		return null;
	}

	// TODO: This method has 1 result error.
	@Override
	public int degree() {
		return 0;
	}

	// TODO: This method has 1 logic error.
	@Override
	public int size() {
		int size = 0;
		for (Row row: rows)
			size++;
		return size;
	}

	// TODO: This method has 1 assignment error.
	@Override
	public int hashCode() {
		int fingerprint = 0;
		for (Row row: rows)
			if (row != null)
				fingerprint = row.key().hashCode() ^ row.fields().hashCode();
		return fingerprint;
	}

	@Override
	public boolean equals(Object obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Row> iterator() {
		throw new UnsupportedOperationException();
	}

	// TODO: This method has 1 result error.
	@Override
	public String name() {
		return null;
	}

	// TODO: This method has 1 result error.
	@Override
	public List<String> columns() {
		return null;
	}

	@Override
	public String toString() {
		var sj = new StringJoiner(", ", name() + "<" + columns().get(0) + "=" + columns().subList(1, degree) + ">{", "}");
		for (var row: rows)
			if (row != null)
				sj.add(row.key() + "=" + row.fields());
		return sj.toString();
	}
}