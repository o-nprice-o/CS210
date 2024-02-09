package apps;

import java.util.Arrays;
import java.util.List;

import model.Table;
import tables.LookupTable;

public class Sandbox {
	public static void main(String[] args) {
		Table table = new LookupTable("test", List.of("a", "b", "c"));
		table.put("a", Arrays.asList(1, 2));
		table.put("b", Arrays.asList(3, 4));
		table.put("c", Arrays.asList(5, 6));
		System.out.println(table);
	}
}