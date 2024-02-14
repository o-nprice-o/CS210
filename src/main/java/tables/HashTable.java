package tables;

import java.util.Iterator;
import java.util.List;

import model.DataTable;
import model.Row;

public class HashTable implements DataTable {
	/*
	 * TODO: For Modules 2 and 3, finish this stub.
	 */

	public HashTable(String name, List<String> columns) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	private int hashFunction(String key) {
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
	public int capacity() {
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
		return new Iterator<>() {


			@Override
			public boolean hasNext() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Row next() {
				throw new UnsupportedOperationException();
			}
		};
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
