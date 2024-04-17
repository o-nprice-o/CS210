package tables;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import model.FileTable;
import model.Row;

public class BinaryTable implements FileTable {
	/*
	 * TODO: For Module 6, finish this stub.
	 */

	private static final boolean BYTES_MODE = false;
	private static final boolean ZIP_MODE = false;

	public BinaryTable(String name, List<String> columns) {
		throw new UnsupportedOperationException();
	}

	public BinaryTable(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}

	private static void writeInt(Path path, int i) {
		throw new UnsupportedOperationException();
	}

	private static int readInt(Path path) {
		throw new UnsupportedOperationException();
	}

	private static void writeRow(Path path, Row row) {
		throw new UnsupportedOperationException();
	}

	private static Row readRow(Path path) {
		throw new UnsupportedOperationException();
	}

	private static void deleteRow(Path path) {
		throw new UnsupportedOperationException();
	}

	private String digestFunction(Object key) {
		throw new UnsupportedOperationException();
	}

	private Path pathOf(String digest) {
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
}
