package relations;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import criteria.Criteria;
import database.DBConnector;
import database.MariaDBConnector;
import loader.ClassMapper;
import loader.ORMLoader;
import loader.TableData;
import testClasses.Car;
import testClasses.Nota;
import testClasses.SUV;
import testClasses.WhiteSUV;

@TestInstance(Lifecycle.PER_CLASS)
public class TestInheritance {
	private DBConnector dbc;
	private ORMLoader ol;
	private Criteria c;

	@BeforeAll
	public void setup() {
		this.dbc = new MariaDBConnector(3306, "localhost", "root", "", "test_orm", true);
		this.ol = new ORMLoader(dbc);
	}

	@Test
	public void test2LevelHierarchy() {
		TableData td = ClassMapper.getInstance().getTableData(SUV.class);
		assertEquals(td.parentTable.class_name, Car.class);
	}
	
	@Test
	public void test3LevelHierarchy() {
		TableData td = ClassMapper.getInstance().getTableData(WhiteSUV.class);
		assertEquals(td.parentTable.class_name, SUV.class);
		assertEquals(td.parentTable.parentTable.class_name, Car.class);
	}
}
