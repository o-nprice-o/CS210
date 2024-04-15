package apps;

import tables.JSONTable;
import tables.XMLTable;

import java.util.Arrays;
import java.util.List;

import model.FileTable;

public class Sandbox {
	public static void main(String[] args) throws Exception {
		JSONTable cars = new JSONTable("Cars", Arrays.asList("Make", "Model", "Year"));
		cars.put("Ford", List.of("Falcon", "1977"));
		cars.put("Chevy", List.of("Camaro", "1969"));
		cars.put("Toyota,", List.of("Camry", "2022"));
		cars.put("Nissan", List.of("GTR", "2005"));
		System.out.println(cars.toString());
		
		JSONTable cars2 = new JSONTable("Cars 2");
		System.out.println(cars2.toString());
		
		XMLTable classes = new XMLTable("Classes", Arrays.asList("Dept", "Number", "Teacher", "Credits"));
		classes.put("Math", List.of("156", "Mrs. Wojciechowska", 4));
		classes.put("Sociology", List.of("101", "Professor Rachael Green Ph.D", 4));
		classes.put("Comp Sci", List.of("350", "Mr. Powell", 3));
		System.out.println(classes.toString());
		
		XMLTable classes2 = new XMLTable("Classes 2");
		System.out.println(classes2.toString());
	}
}
