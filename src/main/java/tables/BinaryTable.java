package tables;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;

import model.Row;
import model.FileTable;
import model.Table;

public class BinaryTable implements FileTable {
	private static final Path BASE_DIR_PATH = Paths.get("db", "tables");
	private Path root, data, metadata;
	private static final boolean CUSTOM_ENCODING = false;

	// root: db/tables/example_table_1
	// data: db/tables/example_table_1/data
	// metadata: db/tables/example_table_1/metadata

	public BinaryTable(String name, List<String> columns) {
		try {
			root = BASE_DIR_PATH.resolve(name);
			Files.createDirectories(root);

			data = root.resolve("data");
			Files.createDirectories(data);

			metadata = root.resolve("metadata");
			Files.createDirectories(metadata);

			// columns: db/tables/example_table_1/metadata/columns
			Files.write(metadata.resolve("columns"), columns);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public BinaryTable(String name) {
		root = BASE_DIR_PATH.resolve(name);
		if (!Files.exists(root)) {
			throw new IllegalArgumentException("Missing table: " + name);
		}
		data = root.resolve("data");
		metadata = root.resolve("metadata");
	}

	@Override
	public void clear() {
		try {
			Files.walk(data)
			.skip(1)
			.sorted(Comparator.reverseOrder())
			.forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			});
			writeInt(metadata.resolve("size"), 0);
			writeInt(metadata.resolve("fingerprint"), 0);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void writeInt(Path path, int i) {
		if (CUSTOM_ENCODING) {
			var bytes = ByteBuffer.allocate(4).putInt(i).array();
			try {
				Files.write(path, bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try (var out = new ObjectOutputStream(Files.newOutputStream(path))) {
				out.writeInt(i);
				out.flush();
			} 
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static int readInt(Path path) {
		if (CUSTOM_ENCODING) {
			try {
				var array = ByteBuffer.wrap(Files.readAllBytes(path));
				return array.getInt();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
		}
		else {
			try (var in = new ObjectInputStream(Files.newInputStream(path))) {
				var i = in.readInt();
				return i;
			} 
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void writeRow(Path path, Row row) {
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path.getParent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (CUSTOM_ENCODING) {
			var bytes = row.getBytes();
			try {
				Files.write(path, bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (var out = new ObjectOutputStream(Files.newOutputStream(path))) {
			out.writeObject(row);
			out.flush();
		} 
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Row readRow(Path path) {
		if (CUSTOM_ENCODING) {
			try {
				return Row.fromBytes(Files.readAllBytes(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (var in = new ObjectInputStream(Files.newInputStream(path))) {
			var i = (Row) in.readObject();
			return i;
		} 
		catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static void deleteRow(Path path) {
		try {
			Files.delete(path);
			Path parent = path.getParent();
			if (parent.toFile().listFiles().length == 0) {
				Files.delete(parent);
			}
			// if when you ask the files api for the count of the parent of the path and it's 0
			// then delete the parent of the path
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String digestFunction(String key) {
		try {
			var sha1 = MessageDigest.getInstance("SHA-1");
			sha1.update("salt-".getBytes());
			sha1.update(key.getBytes());
			
			var digest = sha1.digest();
			var hex = HexFormat.of().withLowerCase(); // ask the HexFormat class to produce a lowercase hex format object
			return hex.formatHex(digest);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private Path pathOf(String digest) {
		var prefix = digest.substring(0, 2);
		var suffix = digest.substring(2, digest.length());
		// get the first two characters of digest, call it the prefix
		//get the last two characters, call it the suffix
		return data.resolve(prefix).resolve(suffix);
		// resolve data sub prefix sub suffix
	}

	@Override
	public List<Object> put(String key, List<Object> fields) {
		if (fields.size() + 1 != this.degree()) {
			throw new IllegalArgumentException("error, degree does not match");
		}
		Path digestPath = pathOf(digestFunction(key));
		Row newRow = new Row(key, fields);
		if (Files.exists(digestPath)) {
			Row oldRow = readRow(digestPath);
			List<Object> oldFields = oldRow.fields();
			writeRow(digestPath, newRow);
			writeInt(metadata.resolve("fingerprint"), (this.hashCode() - oldRow.hashCode()) + newRow.hashCode());
			return oldFields;
		}
		else {
			writeRow(digestPath, newRow);
			writeInt(metadata.resolve("size"), readInt(metadata.resolve("size")) + 1);
			writeInt(metadata.resolve("fingerprint"), this.hashCode() + newRow.hashCode());
			return null;
		}
	}

	@Override
	public List<Object> get(String key) {
		Path digestPath = pathOf(digestFunction(key));
		if (Files.exists(digestPath)) {
			return readRow(digestPath).fields();
		}
		else {
			return null;
		}
	}

	@Override
	public List<Object> remove(String key) {
		Path digestPath = pathOf(digestFunction(key));
		if (Files.exists(digestPath)) {
			Row oldRow = readRow(digestPath);
			List<Object> oldFields = oldRow.fields();
			deleteRow(digestPath);
			writeInt(metadata.resolve("size"), readInt(metadata.resolve("size")) - 1);
			writeInt(metadata.resolve("fingerprint"), this.hashCode() - oldRow.hashCode());
			return oldFields;
		}
		return null;
	}

	@Override
	public int degree() {
		return columns().size();
	}

	@Override
	public int size() {
		// call your readint helper method at metadata/size and return the int
		return readInt(metadata.resolve("size"));
	}

	@Override
	public int hashCode() {
		//
		return readInt(metadata.resolve("fingerprint"));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Table)) {
			return false;
		} else {
			Table compTable = (Table) obj;
			return compTable.hashCode() == this.hashCode();
		}
	}

	@Override
	public Iterator<Row> iterator() {
		try {
			return Files.walk(data)
					.filter(file -> file.toFile().isFile()) // a condition that is only true for files, not directories
					.map(path -> readRow(path))
					.iterator();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String name() {
		return root.getFileName().toString();
	}

	@Override
	public List<String> columns() {
		List<String> colList = new ArrayList<>();
		// read the lines from the corresponding file and return them
		try {
			colList = Files.readAllLines(metadata.resolve("columns"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return colList;
	}

	@Override
	public String toString() {
		return toTabularView(false);
	}
}