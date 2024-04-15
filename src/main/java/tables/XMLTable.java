package tables;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import model.Row;
import model.FileTable;
import model.Table;

public class XMLTable implements FileTable {
	/*
	 * TODO: For Module 5, finish this stub.
	 */
	private static final Path BASE_DIR_PATH = Paths.get("db", "tables");
	private Path flatFile;
	private Document document;

	public XMLTable(String name, List<String> columns) {
		try {
			if (!Files.isDirectory(BASE_DIR_PATH)) {
				Files.createDirectories(BASE_DIR_PATH);
			}

			flatFile = BASE_DIR_PATH.resolve(name + ".xml");

			if (!Files.exists(flatFile)) {
				Files.createFile(flatFile);
			}
			this.document = DocumentHelper.createDocument();

			var root = document.addElement("table");
			var col = root.addElement("columns");
			root.addElement("rows");
			for (String column : columns) {
				col.addElement("column").addText(column);
			}

			flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public XMLTable(String name) {
		try {
			if (!Files.isDirectory(BASE_DIR_PATH)) {
				Files.createDirectories(BASE_DIR_PATH);
			}

			flatFile = BASE_DIR_PATH.resolve(name + ".xml");
			if (!Files.exists(flatFile)) {
				throw new IllegalArgumentException("Error, flat file doesnt exist at the path");
			}
			this.document = new SAXReader().read(flatFile.toFile());
		} catch (DocumentException e) {
			throw new IllegalStateException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear() {
		Element root = this.document.getRootElement();
		root.element("rows").clearContent();
		flush();

	}

	@Override
	public void flush() {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(flatFile.toFile()), format);
			writer.write(this.document);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Object> put(String key, List<Object> fields) {
		if (fields.size() + 1 != this.degree()) {
			throw new IllegalArgumentException("error, degree does not match");
		}
		Element rows = document.getRootElement().element("rows");
		for (Element rowElement : rows.elements()) {
			if (rowElement.attributeValue("key").equals(key)) {
				List<Object> oldFields = new ArrayList<>();
				for (Element fieldElement : rowElement.elements()) {
					oldFields.add(fieldtoObject(fieldElement));
				}
				rows.remove(rowElement);
				Element addElement = rows.addElement("row");
				addElement.addAttribute("key", key);
				for (Object field : fields) {
					Element fieldElem = addElement.addElement("field");
					if (field != null) {
						fieldElem.addAttribute("type", field.getClass().getName());
						fieldElem.addText(field.toString());
					} else {
						fieldElem.addAttribute("type", "null");
					}
				}
				flush();
				return oldFields;
			}
		}
		Element addElement = rows.addElement("row");
		addElement.addAttribute("key", key);
		for (Object field : fields) {
			Element fieldElem = addElement.addElement("field");
			if (field != null) {
				fieldElem.addAttribute("type", field.getClass().getName());
				fieldElem.addText(field.toString());
			} else {
				fieldElem.addAttribute("type", "null");
			}
		}
		flush();
		return null;
	}

	@Override
	public List<Object> get(String key) {
		Element rows = this.document.getRootElement().element("rows");
		for (Element rowElement : rows.elements()) {
			if (rowElement.attributeValue("key").equals(key)) {
				List<Object> fields = new ArrayList<>();
				for (Element fieldElement : rowElement.elements()) {
					fields.add(fieldtoObject(fieldElement));
				}
				return fields;
			}
		}
		return null;
	}

	@Override
	public List<Object> remove(String key) {
		Element rows = this.document.getRootElement().element("rows");
		for (Element rowElement : rows.elements()) {
			if (rowElement.attributeValue("key").equals(key)) {
				List<Object> oldFields = new ArrayList<>();
				for (Element fieldElement : rowElement.elements()) {
					oldFields.add(fieldtoObject(fieldElement));
				}
				rows.remove(rowElement);
				flush();
				return oldFields;
			}
		}
		return null;
	}

	@Override
	public int degree() {
		return this.columns().size();
	}

	@Override
	public int size() {
		int size = 0;
		Element root = this.document.getRootElement();
		if (root != null) {
			Element rowsElement = root.element("rows");
			for (Element row : rowsElement.elements()) {
				size++;
			}
		}
		return size;
	}

	@Override
	public int hashCode() {
		int hashSum = 0;
		Element rows = this.document.getRootElement().element("rows");
		for (Element rowElem : rows.elements()) {
			String key = rowElem.attributeValue("key");
			List<Object> fields = new ArrayList<>();
			for (Element fieldElem : rowElem.elements()) {
				fields.add(fieldtoObject(fieldElem));
			}
			Row newRow = new Row(key, fields);
			hashSum += newRow.hashCode();
		}
		return hashSum;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Table && this.hashCode() == obj.hashCode();
	}

	@Override
	public Iterator<Row> iterator() {
		List<Row> rows = new ArrayList<>();
		List<Element> elements = this.document.getRootElement().element("rows").elements();
		for (Element rowElement : elements) {
			String key = rowElement.attributeValue("key");
			List<Object> fields = new ArrayList<>();
			for (Element fieldElem : rowElement.elements()) {
				fields.add(fieldtoObject(fieldElem));
			}
			rows.add(new Row(key, fields));
		}
		return rows.iterator();
	}

	@Override
	public String name() {
		String filename = flatFile.getFileName().toString();
		int lastDotIndex = filename.lastIndexOf(".");
		if (lastDotIndex > 0) {
			return filename.substring(0, lastDotIndex);
		} else {
			return filename;
		}
	}

	@Override
	public List<String> columns() {
		List<String> colList = new ArrayList<>();
		Element root = this.document.getRootElement();
		if (root != null) {
			Element colElement = root.element("columns");
			if (colElement != null) {
				List<Element> colElements = colElement.elements("column");
				for (Element element : colElements) {
					colList.add(element.getText());
				}
			}
		}
		return colList;
	}

	@Override
	public String toString() {
		return toTabularView(false);
	}

	private static Object fieldtoObject(Element fieldElement) {
		switch (fieldElement.attributeValue("type")) {
		case "java.lang.Integer":
			return Integer.parseInt(fieldElement.getText());
		case "java.lang.Double":
			return Double.parseDouble(fieldElement.getText());
		case "java.lang.Boolean":
			return Boolean.parseBoolean(fieldElement.getText());
		case "java.lang.String":
			return fieldElement.getText().toString();
		case "null":
			return null;
		default:
			throw new IllegalArgumentException("field type is not recognized");
		}
	}
}
