package tables;

import java.util.Iterator;
import java.util.List;

import model.FileTable;
import model.Row;

public class CSVTable implements FileTable {
	/*
	 * TODO: For Module 4, finish this stub.
	 */

	public CSVTable(String name, List<String> columns) {
		throw new UnsupportedOperationException();
	}

	public CSVTable(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	private static String encodeRow(String key, List<Object> fields) {
		throw new UnsupportedOperationException();
	}

	private static Row decodeRow(String record) {
		throw new UnsupportedOperationException();
	}

	private static String encodeField(Object obj) {
		throw new UnsupportedOperationException();
	}

	private static Object decodeField(String field) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Object> put(String key, List<Object> fields) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Object> get(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Object> remove(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int degree() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> columns() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}

	public static CSVTable fromText(String name, String text) {
		throw new UnsupportedOperationException();
	}
}
