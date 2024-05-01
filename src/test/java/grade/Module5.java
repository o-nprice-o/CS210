package grade;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@DisplayName("M5 JSON & XML Tables")
@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
@TestClassOrder(ClassOrderer.ClassName.class)
final class Module5 extends AbstractModule {
	@BeforeAll
	void defineModule() {
		containers = 4;
		operations = 250;
		elements = 250;
	}

	@Nested
	@DisplayName("m5_table1 [degree 4]")
	class JSONTable1 extends JSONTableContainer {
		@BeforeAll
		void defineTable() {
			name = "m5_table1";
			columns = List.of("k2", "f2a", "f2b", "f2c");
		}
	}

	@Nested
	@DisplayName("m5_table2 [degree 4]")
	class XMLTable2 extends XMLTableContainer {
		@BeforeAll
		void defineTable() {
			name = "m5_table2";
			columns = List.of("k2", "f2a", "f2b", "f2c");
		}
	}

	@TestMethodOrder(MethodOrderer.MethodName.class)
	abstract class JSONTableContainer extends AbstractTableContainer {
		static final List<String> exempt = List.of(
    		"model",
			"tables",
			"java.nio.file.Path",
			"com.fasterxml.jackson.core.TreeNode",
			"com.fasterxml.jackson.core.TreeCodec"
		);

		@TestFactory
		@DisplayName("New JSON Table")
		@Execution(ExecutionMode.SAME_THREAD)
		Stream<DynamicTest> testNewTable() {
			logStart("new");

			subject = testConstructor(
				"tables.JSONTable",
				List.of(String.class, List.class),
				List.of(name, columns),
				exempt
			);

			control = new ControlTable();

			return IntStream.range(0, operations).mapToObj(i -> {
				if (i == 0)
					return testName();
				else if (i == 1)
					return testColumns();
				else if (i == 2)
					return testClear();
				else if (i % 20 == 0 || i == operations-1)
					return testIterator();
				else {
					if (control.size() < elements * .99)
						return testPut(false, null);
					else if (control.size() > elements * 1.01)
						return testRemove(true, null);
					else if (RNG.nextBoolean())
						return testPut(RNG.nextBoolean(), null);
					else
						return testRemove(RNG.nextBoolean(), null);
				}
			});
		}

		@TestFactory
		@DisplayName("Existing JSON Table")
		@Execution(ExecutionMode.SAME_THREAD)
		Stream<DynamicTest> thenTestExistingTable() {
			logStart("existing");

			subject = testConstructor(
				"tables.JSONTable",
				List.of(String.class),
				List.of(name),
				exempt
			);

			return IntStream.range(0, operations).mapToObj(i -> {
				if (i == 0)
					return testName();
				else if (i == 1)
					return testColumns();
				else if (i == 2 || i % 20 == 0 || i == operations-1)
					return testIterator();
				else {
					if (control.size() < elements * .99)
						return testPut(false, null);
					else if (control.size() > elements * 1.01)
						return testRemove(true, null);
					else
						if (RNG.nextBoolean())
						return testGet(RNG.nextBoolean());
					else if (RNG.nextBoolean())
						return testPut(RNG.nextBoolean(), null);
					else
						return testRemove(RNG.nextBoolean(), null);
				}
			});
		}
	}

	@TestMethodOrder(MethodOrderer.MethodName.class)
	abstract class XMLTableContainer extends AbstractTableContainer {
		static final List<String> exempt = List.of(
    		"model",
			"tables",
			"java.nio.file.Path",
			"org.dom4j.Node"
		);

		@TestFactory
		@DisplayName("New XML Table")
		@Execution(ExecutionMode.SAME_THREAD)
		Stream<DynamicTest> testNewTable() {
			logStart("new");

			subject = testConstructor(
				"tables.XMLTable",
				List.of(String.class, List.class),
				List.of(name, columns),
				exempt
			);

			control = new ControlTable();

			return IntStream.range(0, operations).mapToObj(i -> {
				if (i == 0)
					return testName();
				else if (i == 1)
					return testColumns();
				else if (i == 2)
					return testClear();
				else if (i % 20 == 0 || i == operations-1)
					return testIterator();
				else {
					if (control.size() < elements * .99)
						return testPut(false, null);
					else if (control.size() > elements * 1.01)
						return testRemove(true, null);
					else if (RNG.nextBoolean())
						return testPut(RNG.nextBoolean(), null);
					else
						return testRemove(RNG.nextBoolean(), null);
				}
			});
		}

		@TestFactory
		@DisplayName("Existing XML Table")
		@Execution(ExecutionMode.SAME_THREAD)
		Stream<DynamicTest> thenTestExistingTable() {
			logStart("existing");

			subject = testConstructor(
				"tables.XMLTable",
				List.of(String.class),
				List.of(name),
				exempt
			);

			return IntStream.range(0, operations).mapToObj(i -> {
				if (i == 0)
					return testName();
				else if (i == 1)
					return testColumns();
				else if (i == 2 || i % 20 == 0 || i == operations-1)
					return testIterator();
				else {
					if (control.size() < elements * .99)
						return testPut(false, null);
					else if (control.size() > elements * 1.01)
						return testRemove(true, null);
					else
						if (RNG.nextBoolean())
						return testGet(RNG.nextBoolean());
					else if (RNG.nextBoolean())
						return testPut(RNG.nextBoolean(), null);
					else
						return testRemove(RNG.nextBoolean(), null);
				}
			});
		}
	}
}