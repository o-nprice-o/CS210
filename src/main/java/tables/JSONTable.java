package tables;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import model.FileTable;
import model.Row;
import model.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONTable implements FileTable {
	private static final Path BASE_DIR = Paths.get("db", "tables");
	private Path filePath;
	private final ObjectMapper mapper = new ObjectMapper();
	private ObjectNode tree;

	// Constructors
	public JSONTable(String name, List<String> columns) {
		try {
			if (!Files.isDirectory(BASE_DIR)) {
				Files.createDirectories(BASE_DIR);
			}
			filePath = BASE_DIR.resolve(name + ".json");
			if (!Files.exists(filePath)) {
				Files.createFile(filePath);
			}
			this.tree = mapper.createObjectNode();
			ObjectNode metanode = mapper.createObjectNode();
			metanode.putPOJO("columns", columns);
			tree.set("metadata", metanode);
			tree.set("data", mapper.createObjectNode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public JSONTable(String name) {
		try {
			if (!Files.isDirectory(BASE_DIR)) {
				Files.createDirectories(BASE_DIR);
			}
			filePath = BASE_DIR.resolve(name + ".json");
			if (!Files.exists(filePath)) {
				throw new IllegalArgumentException();
			}
			this.tree = (ObjectNode) mapper.readTree(filePath.toFile());
		} catch (JsonParseException e) {
			throw new IllegalStateException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initializeTree() {
		if (tree == null) {
			throw new IllegalStateException("JSONTable tree is not initialized.");
		}
	}

	// Public methods for statistics and predicates
	public String name() {
		return filePath.getFileName().toString().replace(".json", "");
	}

	public List<String> columns() {
		return mapper.convertValue(tree.get("metadata").get("columns"), List.class);
	}

	public int degree() {
		return columns().size();
	}

	public int size() {
		return tree.get("data").size();
	}

	public int hashCode() {
		int hashSum = 0;

		ObjectNode data = (ObjectNode) this.tree.get("data");
		Row helper;
		Iterator<Entry<String, JsonNode>> properties = data.fields();
		while (properties.hasNext()) {
			Entry<String, JsonNode> entry = properties.next();
			helper = new Row(entry.getKey(), mapper.convertValue(entry.getValue(), List.class));
			hashSum += helper.hashCode();
		}

		return hashSum;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Table)) {
			return false;
		} else {
			JSONTable thatTable = (JSONTable) obj;
			return this.hashCode() == thatTable.hashCode();
		}
	}

	public Iterator<Row> iterator() {
		ObjectNode data = (ObjectNode) this.tree.get("data");
		Row helper;
		List<Row> rows = new ArrayList<>();
		Iterator<Entry<String, JsonNode>> properties = data.fields();
		while (properties.hasNext()) {
			Entry<String, JsonNode> entry = properties.next();
			helper = new Row(entry.getKey(), mapper.convertValue(entry.getValue(), List.class));
			rows.add(helper);
		}
		return rows.iterator();
	}

	public void clear() {
		if (this.tree != null && this.tree.has("data")) {
			ObjectNode dataN = (ObjectNode) this.tree.get("data");
			dataN.removeAll();
			flush();
		}
	}

	public void flush() {
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(this.filePath.toFile(), this.tree);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return toTabularView(false);
	}

	public List<Object> put(String key, List<Object> fields) {
		if (fields.size() + 1 != this.degree()) {
			throw new IllegalArgumentException("Degree of the given row doesn't match the degree of the table.");
		}
		if (this.tree != null && this.tree.has("data")) {
			ObjectNode dataN = (ObjectNode) this.tree.get("data");
			if (dataN.has(key)) {
				List<Object> oldfields = mapper.convertValue(dataN.get(key), List.class);
				dataN.remove(key);
				dataN.putPOJO(key, fields);
				flush();
				return oldfields;
			} else {
				dataN.putPOJO(key, fields);
				flush();
				return null;
			}
		} else {
			return null;
		}
	}

	public List<Object> get(String key) {
		if (this.tree != null && this.tree.has("data")) {
			ObjectNode dataN = (ObjectNode) this.tree.get("data");
			return mapper.convertValue(dataN.get(key), List.class);
		} else {
			return null;
		}
	}

	public List<Object> remove(String key) {
		if (this.tree != null && this.tree.has("data")) {
			ObjectNode dataN = (ObjectNode) this.tree.get("data");
			if (dataN.has(key)) {
				List<Object> oldfields = mapper.convertValue(dataN.get(key), List.class);
				dataN.remove(key);
				flush();
				return oldfields;
			}
		}
		return null;
	}
}
