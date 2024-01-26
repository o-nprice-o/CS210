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

	public LookupTable(String name, List<String> columns) {
		this.name = name;
		this.columns = columns;
		this.degree = columns.size();
		clear();
	}

	@Override
	public void clear() {
		rows = new Row[52];
	}

	private int indexOf(String key) {
		if (key.length() < 1)
			throw new IllegalArgumentException("Key must be at least 1 character");

		char c = key.charAt(0);
		if (c >= 'a' && c <= 'z')
			return c - 'a';
		else if (c >= 'A' && c <= 'Z')
			return c - 'A' +26;
		else
			throw new IllegalArgumentException("Key must start with a lowercase or uppercase letter");
	}

	@Override
	public List<Object> put(String key, List<Object> fields) {
		if (1 + fields.size() < degree)
			throw new IllegalArgumentException("Row is too narrow");
		if (1 + fields.size() > degree)
			throw new IllegalArgumentException("Row is too wide");

		int i = indexOf(key);

		Row make = new Row(key, fields);

		if (rows[i] != null) {
			Row old = rows[i];
			rows[i] = make;
			return old.fields();
		}
		rows[i] = make;
		return null;
	}

	@Override
	public List<Object> get(String key) {
		int i = indexOf(key);
		if(rows[i] != null)
			return rows[i].fields();
		else
			return null;
	}

	@Override
	public List<Object> remove(String key) {
		int i = indexOf(key);

		if (rows[i] != null) {
			Row old = rows[i];
			rows[i] = null;
			return old.fields();
		}

		return null;
	}

	@Override
	public int degree() {
		return degree;
	}

	@Override
	public int size() {
		int size = 0;
		for (Row row: rows)
			if(row != null)
				size++;
		return size;
	}

	@Override
	public int hashCode() {
		int fingerprint = 0;
		for (Row row: rows)
			if (row != null)
				fingerprint += row.key().hashCode() ^ row.fields().hashCode();
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
		var sj = new StringJoiner(", ", name() + "<" + columns().get(0) + "=" + columns().subList(1, degree) + ">{", "}");
		for (var row: rows)
			if (row != null)
				sj.add(row.key() + "=" + row.fields());
		return sj.toString();
	}
}