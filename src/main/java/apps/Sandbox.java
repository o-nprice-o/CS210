package apps;

import java.util.Arrays;
import java.util.List;

import model.Table;
import tables.LookupTable;

public class Sandbox {
	public static void main(String[] args) {
		/*
		 * TODO: Modify as needed to debug
		 * or demonstrate arbitrary code.
		 */

		Table rc_table = new LookupTable("reaser_courses", List.of("title", "subject", "number", "credits", "meetings"));
		rc_table.put("File and Data Structures", List.of("CS", "210", 4, true));
		rc_table.put("HNRS: CS 210 Add-On", List.of("CS", "298a", 0, false));
		rc_table.put("Principles of Programming Languages", List.of("CS", "310", 3, true));
		rc_table.put("Database Design and Theory", List.of("CS", "440", 3, true));
		rc_table.put("SPTP: STEM Teaching Practices", Arrays.asList("ENGR", "493a", 3, null));
		rc_table.put("Teaching Practicum", List.of("CSEE", "490", "1 to 3", false));
		System.out.println(rc_table);
	}
}
