package tables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

import model.FileTable;
import model.Row;
import model.Table;

public class CSVTable implements FileTable {
	private static final Path BASE_DIR = Paths.get("db", "tables");
	private Path filePath;

	public CSVTable(String name, List<String> columns) {
		try {
			if (!Files.isDirectory(BASE_DIR)) {
				Files.createDirectories(BASE_DIR);
			}
			filePath = BASE_DIR.resolve(name + ".csv");
			if (!Files.exists(filePath)) {
				Files.createFile(filePath);
			}
			String headerFile = String.join(",", columns);
			List<String> lines = new ArrayList<String>();
			lines.add(headerFile);
			Files.write(filePath, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CSVTable(String name) {
		this.filePath = BASE_DIR.resolve(name + ".csv");
		if (!Files.exists(filePath)) {
			throw new IllegalArgumentException("File does not exist: " + filePath);
		}
	}

	@Override
	public void clear() {
		try {
			List<String> lines = Files.readAllLines(filePath);
			String header = lines.get(0);
			lines.clear();
			lines.add(header);
			Files.write(filePath, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String name() {
		return filePath.getFileName().toString().replace(".csv", "");
	}

	public List<String> columns() {
		List<String> columns = new ArrayList<String>();
		try {
			List<String> lines = Files.readAllLines(filePath);
			String header = lines.get(0);
			columns = Arrays.asList(header.split(","));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return columns;
	}

	public int degree() {
		return this.columns().size();
	}

	public int size() {
		List<String> lines = new ArrayList<String>();
		try {
			lines = Files.readAllLines(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines.size() - 1;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof CSVTable)) {
			return false;
		} else {
			Table thatTable = (Table) obj;
			return this.hashCode() == thatTable.hashCode();
		}
	}

	private static String encodeRow(Row row) {
		StringJoiner joiner = new StringJoiner(",");
		joiner.add(encodeField(row.key()));
		for (Object field : row.fields()) {
			joiner.add(encodeField(field));
		}
		return joiner.toString();
	}

	private static Row decodeRow(String record) {
		List<String> pieces = Arrays.asList(record.split(","));
		String key = pieces.get(0).substring(1, pieces.get(0).length() - 1);
		List<Object> fields = new ArrayList<>();

		for (int i = 1; i < pieces.size(); i++) {
			fields.add(decodeField(pieces.get(i)));
		}
		return new Row(key, fields);
	}

	private static String encodeField(Object obj) {
		if (obj == null) {
			return "null";
		} else if (obj instanceof String) {
			return "\"" + obj.toString() + "\"";
		} else if ((obj instanceof Boolean) || (obj instanceof Integer) || (obj instanceof Double)) {
			return obj.toString();
		} else {
			throw new IllegalArgumentException("Unsupported object: " + obj.toString());
		}
	}

	private static Object decodeField(String field) {
		if (field.equalsIgnoreCase("null")) {
			return null;
		} else if (field.startsWith("\"") && field.endsWith("\"")) {
			return field.substring(1, field.length() - 1);
		} else if (field.equalsIgnoreCase("true") || field.equalsIgnoreCase("false")) {
			boolean value = false;
			if (field.equalsIgnoreCase("true")) {
				value = true;
			}
			return value;
		} else if (isInteger(field)) {
			return Integer.parseInt(field);
		} else if (isDouble(field)) {
			return Double.parseDouble(field);
		} else {
			throw new IllegalArgumentException("Unsupported field: ");
		}
	}

	private static boolean isInteger(String field) {
		try {
			Integer.parseInt(field);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isDouble(String field) {
		try {
			Double.parseDouble(field);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		try {
			List<String> lines = Files.readAllLines(filePath);
			int hashSum = 0;

			for (int i = 1; i < lines.size(); i++) {
				String line = lines.get(i);
				Row row = decodeRow(line);
				hashSum += row.hashCode();
			}
			return hashSum;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<Object> put(String key, List<Object> fields) {
		try {
			List<String> lines = Files.readAllLines(filePath);
			String header = lines.get(0);
			List<String> headerList = Arrays.asList(header.split(","));

			if (fields.size() + 1 != headerList.size()) {
				throw new IllegalArgumentException("error, fields are not the same size");
			}

			Row newRow = new Row(key, fields);
			String newRecord = encodeRow(newRow);

			int index = -1;

			for (int i = 1; i < lines.size(); i++) {
				String line = lines.get(i);
				Row oldRow = decodeRow(line);
				if (oldRow.key().equals(key)) {
					index = i;
					break;
				}
			}

			if (index != -1) {
				Row oldRow = decodeRow(lines.get(index));
				lines.set(index, newRecord);
				Files.write(filePath, lines);
				return oldRow.fields();
			} else {
				lines.add(newRecord);
				Files.write(filePath, lines);
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Object> get(String key) {
		try {
			List<String> lines = Files.readAllLines(filePath);

			int index = -1;

			for (int i = 1; i < lines.size(); i++) {
				String line = lines.get(i);
				Row oldRow = decodeRow(line);
				if (oldRow.key().equals(key)) {
					index = i;
					break;
				}
			}

			if (index != -1) {
				Row oldRow = decodeRow(lines.get(index));
				lines.remove(index);
				lines.add(1, encodeRow(oldRow));
				Files.write(filePath, lines);
				return oldRow.fields();
			}
			return null;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Object> remove(String key) {
		try {
			List<String> lines = Files.readAllLines(filePath);

			int index = -1;

			for (int i = 1; i < lines.size(); i++) {
				String line = lines.get(i);
				Row oldRow = decodeRow(line);
				if (oldRow.key().equals(key)) {
					index = i;
					break;
				}
			}

			if (index != -1) {
				Row oldRow = decodeRow(lines.get(index));
				lines.remove(index);
				Files.write(filePath, lines);
				return oldRow.fields();
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Iterator<Row> iterator() {
		List<String> lines;
		List<Row> row = new ArrayList<Row>();
		try {
			lines = Files.readAllLines(filePath);
			for (int i = 1; i < lines.size(); i++) {
				row.add(decodeRow(lines.get(i)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return row.iterator();
	}

	@Override
	public String toString() {
		return toTabularView(true);
	}

	public static CSVTable fromText(String name, String text) throws IOException {
		try {
			if (!Files.isDirectory(BASE_DIR)) {
				Files.createDirectory(BASE_DIR);
			}
			Path newFilePath = BASE_DIR.resolve(name + ".csv");
			if (!Files.exists(newFilePath)) {
				Files.createFile(newFilePath);
			}
			Files.write(newFilePath, text.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new CSVTable(name);
	}

}
