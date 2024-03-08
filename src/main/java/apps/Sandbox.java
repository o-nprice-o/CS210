package apps;

//Nicholas Price

import java.util.Arrays;
import java.util.List;

import model.Table;
import tables.HashTable;

public class Sandbox {
	public static void main(String[] args) {
		Table rc_table = new HashTable("my_classes", Arrays.asList("| title", "subject", "meetings", ""));
		rc_table.put("CS210", Arrays.asList("    CS    ", "  210  ", true));
		rc_table.put("Math 156", Arrays.asList("  Math  ", "  156  ", true));
		rc_table.put("History 101", Arrays.asList("History", "  101   ", true));
		rc_table.put("CS350", Arrays.asList("    CS    ", "  350  ", true));
		rc_table.put("Sociology 101", Arrays.asList("  SOC   ", "  101   ", false));
		System.out.println(rc_table);
		
		Table rc_table2 = new HashTable("Cars", Arrays.asList("| Make", "Model", "Produced", ""));
		rc_table2.put("Ford", Arrays.asList("  Ford  ", "      GT     ", false));
		rc_table2.put("Chevy", Arrays.asList("Chevy ", "   Camaro  ", false));
		rc_table2.put("Toyota", Arrays.asList("Toyota ", "   Supra   ",  true));
		rc_table2.put("Subaru", Arrays.asList("Subaru", "WRX STI",  false));
		rc_table2.put("BMW", Arrays.asList("BMW  ", "      I8      ",  false));
		System.out.println(rc_table2);
	}
	
}