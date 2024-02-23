package apps;

//Nicholas Price

import java.util.Arrays;
import java.util.List;

import model.Table;
import tables.HashTable;

public class Sandbox {
	public static void main(String[] args) {
		Table rc_table = new HashTable("my_classes", Arrays.asList("title", "subject"));
		rc_table.put("CS210", Arrays.asList("CS"));
		rc_table.put("Math 156", Arrays.asList("Math"));
		rc_table.put("History 101", Arrays.asList("History"));
		rc_table.put("CS350", Arrays.asList("CS"));
		rc_table.put("Sociology 101", Arrays.asList("Sociology"));
		System.out.println(rc_table);
	}
	
}