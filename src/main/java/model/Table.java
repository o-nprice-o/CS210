package model;

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

	public default String toTabularView(boolean sorted) {
        StringBuilder sb = new StringBuilder();
        sb.append(name()).append("\n");

        List<String> columns = columns();
        int numColumns = columns.size();
        int[] columnWidths = new int[numColumns];

        // Calculate column widths
        for (Row row : this) {
            for (int i = 0; i < numColumns - 1; i++) {
                Object field = row.fields().get(i);
                columnWidths[i] = Math.max(columnWidths[i], field.toString().length());
            }
        }

        // Print table header
        for (int i = 0; i < numColumns; i++) {
            sb.append("+").append("-".repeat(columnWidths[i] + 2));
            if (i == numColumns - 1)
                sb.append("+\n");
        }

        // Print column names
        sb.append("|");
        for (int i = 0; i < numColumns; i++) {
            String columnLabel = columns.get(i);
            sb.append(" ").append(((columnLabel)));
            sb.append(" |");
        }
        sb.append("\n");

        // Print rows
        List<Row> rows = new ArrayList<>();
        for (Row row : this) {
            rows.add(row);
        }
        if (sorted) {
            Collections.sort(rows);
        }
        for (Row row : rows) {
            sb.append("|");
            for (int i = 0; i < numColumns - 1; i++) {
                Object field = row.fields().get(i);
                String fieldValue = field == null ? " " : field.toString();
                if (fieldValue.length() > columnWidths[i]) {
                    fieldValue = fieldValue.substring(0, columnWidths[i] - 3) + "...";
                }
                sb.append(" ").append((fieldValue).toString());
                sb.append(" |");
            }
            sb.append("\n");
        }

        // Print table footer
        for (int i = 0; i < numColumns; i++) {
            sb.append("+").append("-".repeat(columnWidths[i] + 2));
            if (i == numColumns - 1)
                sb.append("+\n");
        }

        return sb.toString();
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
