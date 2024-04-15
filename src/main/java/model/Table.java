package model;

//This is a change so I can submit these together
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tables.HashTable;

public interface Table extends Iterable<Row> {
	public void clear();

	public List<Object> put(String key, List<Object> fields);

	public List<Object> get(String key);

	public List<Object> remove(String key);

	public default boolean contains(String key) {
		throw new UnsupportedOperationException();
	}

	public int degree();

	public int size();

	public default boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object obj);

	@Override
	public Iterator<Row> iterator();

	public String name();

	public List<String> columns();

	@Override
	public String toString();
	
	private void appendTableLine(StringBuilder builder, int repeat) {
        builder.append("+");
        for (int i = 0; i < repeat; i++) {
            for (int j = 0; j < 10; j++) {
                builder.append("--");
            }
        }
        builder.append("+");
        builder.append("\n");
    }

	public default String toTabularView(boolean sorted) {
		StringBuilder tabularView = new StringBuilder();
        List<Row> sortedRows = new ArrayList<>();
        Iterator<Row> iterator = iterator();
        String cellFormat = "| %-18s";
        String numberFormat = "|%18s ";
        int repeat = 10;

        while (iterator.hasNext()) {
            sortedRows.add(iterator.next());
        }
        if (sortedRows.size() > 0) {
            repeat = this.columns().size();
        }
        tabularView.append("/ " +this.name() + " \\" + System.lineSeparator());

        appendTableLine(tabularView, repeat);

        for (int i = 0; i < repeat; i++) {
            tabularView.append(String.format(cellFormat, this.columns().get(i)));
        }
        tabularView.append("|\n");

        appendTableLine(tabularView, repeat);

        for (Row row : sortedRows) {
            tabularView.append(String.format(cellFormat, row.key()));
            for (Object field : row.fields()) {
                tabularView.append(String.format(numberFormat, field));
            }
            tabularView.append("|\n");
        }

        appendTableLine(tabularView, repeat);

        return tabularView.toString();
    }

	
	public default Table filter(String column, Object target) {
	    if (!columns().contains(column)) {
	        throw new IllegalArgumentException("Column " + column + " does not exist in the table.");
	    }
	    if (target == null) {
	        throw new IllegalArgumentException("Target cannot be null.");
	    }
	    Table partition = new HashTable(name() + "_partition", columns());
	    for (Row row : this) {
	        int columnIndex = columns().indexOf(column);
	        Object value = row.fields().get(columnIndex);
	        if (value != null && value.toString().equals(target.toString())) {
	            partition.put(row.key(), row.fields());
	        }
	    }
	    return partition;
	}

}