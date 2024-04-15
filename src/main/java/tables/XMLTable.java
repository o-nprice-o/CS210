package tables;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class XMLTable {
    private static final String BASE_DIRECTORIES = "db"; // Base directories for sub-tables
    private static final String FILE_EXTENSION = ".xml"; // File extension for table files

    private final String name;
    private final String path;
    private Document document;

    // 2-ary constructor
    public XMLTable(String name, List<String> columns) {
        this.name = name;
        this.path = BASE_DIRECTORIES + File.separator + name + FILE_EXTENSION;

        createBaseDirectoriesIfNeeded();
        initializeDocument(columns);
    }

    // 1-ary constructor
    public XMLTable(String name) {
        this.name = name;
        this.path = BASE_DIRECTORIES + File.separator + name + FILE_EXTENSION;

        if (!fileExistsAtPath()) {
            throw new IllegalArgumentException("File does not exist: " + path);
        }

        initializeDocumentFromExistingFile();
    }
    
    private boolean fileExistsAtPath() {
        return Files.exists(Paths.get(this.path));
    }

    // Clear method
    public void clear() {
        Element rowsElement = document.getRootElement().element("rows");
        rowsElement.clearContent();
        flushToFile();
    }

    // Public methods for statistics and predicates
    public String name() {
        return name;
    }

    public List<String> columns() {
        List<String> columnList = new ArrayList<>();
        Element columnsElement = document.getRootElement().element("columns");
        for (Iterator<Element> it = columnsElement.elementIterator("column"); it.hasNext(); ) {
            columnList.add(it.next().getText());
        }
        return columnList;
    }

    public int degree() {
        return columns().size();
    }

    public int size() {
        Element rowsElement = document.getRootElement().element("rows");
        return rowsElement.elements("row").size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        XMLTable otherTable = (XMLTable) obj;
        return columns().equals(otherTable.columns());
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Table: ").append(name()).append("\n");
        sb.append("Columns: ").append(columns()).append("\n");
        sb.append("Number of Rows: ").append(size()).append("\n");
        return sb.toString();
    }

    // Helper method to create base directories if needed
    private void createBaseDirectoriesIfNeeded() {
        File baseDir = new File(BASE_DIRECTORIES);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    // Helper method to initialize the document with columns and rows
    private void initializeDocument(List<String> columns) {
        document = DocumentHelper.createDocument();
        Element tableElement = document.addElement("table");
        Element columnsElement = tableElement.addElement("columns");
        columns.forEach(column -> columnsElement.addElement("column").setText(column));
        tableElement.addElement("rows");

        flushToFile();
    }

    // Helper method to initialize the document from an existing file
    private void initializeDocumentFromExistingFile() {
        try {
            String xmlContent = Files.readString(Paths.get(path));
            document = DocumentHelper.parseText(xmlContent);
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Error reading XML file: " + e.getMessage());
        }
    }

    // Helper method to flush the document to the file
    private void flushToFile() {
        try (FileWriter writer = new FileWriter(path)) {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing XML file: " + e.getMessage());
        }
    }

    // Inner class representing a row (customize as needed)
    public static class Row {
        private final Element rowElement;

        public Row(Element rowElement) {
            this.rowElement = rowElement;
        }

        // Implement getters for specific columns (customize as needed)
        public String getColumnValue(String columnName) {
            Element columnElement = rowElement.element(columnName);
            return columnElement != null ? columnElement.getText() : null;
        }
    }
}
