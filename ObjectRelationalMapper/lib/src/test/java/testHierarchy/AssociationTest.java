package testHierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import criteria.Criteria;
import database.DBConnector;
import database.MariaDBConnector;
import loader.ClassMapper;
import loader.ORMLoader;
import loader.TableData;
import testClasses.Car;
import testClasses.Door;
import testClasses.SUV;
import testClasses.Traction;

@TestInstance(Lifecycle.PER_CLASS)
public class AssociationTest {
	private DBConnector dbc, mocked_dbc;
	private ORMLoader ol, mocked_ol;
	private Criteria c;
	private Connection mocked_con;

	@BeforeAll
	public void setup() {
		this.dbc = new MariaDBConnector(3306, "localhost", "root", "", "test_orm", true);
		this.ol = new ORMLoader(dbc);
		this.mocked_dbc = Mockito.mock(MariaDBConnector.class);
		this.mocked_con = Mockito.mock(Connection.class);
		this.mocked_ol = new ORMLoader(mocked_dbc);
		try {
			Mockito.when(mocked_dbc.getConnection()).thenReturn(this.mocked_con);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSimpleAssociation() {
		List<Door> doors = new LinkedList<Door>();
		doors.add(new Door(12, 10));
		doors.add(new Door(5, 5));
		Car c = new Car("Audi", "Pink", "BV45GOG", 9, doors);
		TableData car = ClassMapper.getInstance().getTableData(Car.class);
		assertEquals(car.class_name, Car.class);
		for (TableData foreignTables : car.associatedTables.keySet())
			assertEquals(foreignTables.class_name, Door.class);
	}
	
	@Test
	public void test2Association() {
		List<Door> doors = new LinkedList<Door>();
		doors.add(new Door(12, 10));
		doors.add(new Door(5, 5));
		Traction t = new Traction(3);
		SUV c = new SUV("Audi", "Pink", "BV45GOG", 9,120, doors,t);
		TableData suv = ClassMapper.getInstance().getTableData(SUV.class);
		assertEquals(suv.class_name, SUV.class);
		for (TableData foreignTables : suv.associatedTables.keySet())
			assertEquals(foreignTables.class_name, Traction.class);
		assertEquals(suv.parentTable.class_name, Car.class);
		for (TableData foreignTables : suv.parentTable.associatedTables.keySet())
			assertEquals(foreignTables.class_name, Door.class);
	}

}
