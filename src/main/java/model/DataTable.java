package model;

public interface DataTable extends Table {
	public int capacity();

	public default boolean isFull() {
		throw new UnsupportedOperationException();
	}

	public default double loadFactor() {
		throw new UnsupportedOperationException();
	}
}
