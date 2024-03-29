package apps;

import java.io.IOException;
import java.util.List;

import tables.CSVTable;

//Nicholas Price

public class Sandbox {

    public static void main(String[] args) throws IOException {
        CSVTable table1 = new CSVTable("table1", List.of("ID", "Name", "Age"));

        String table2Text = "ID,Name,Age\n1,John,30\n2,Alice,25\n3,Bob,35";
        CSVTable table2 = CSVTable.fromText("table2", table2Text);

        printTableInfo(table1);
        System.out.println();
        printTableInfo(table2);
    }

    private static void printTableInfo(CSVTable table) {
        System.out.println("Table Name: " + table.name());
        System.out.println("Columns: " + table.columns());
        System.out.println("Degree: " + table.degree());
        System.out.println("Size: " + table.size());
    }
}