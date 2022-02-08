package testQuery;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
public class MariaDBtestQuery {
	private DBConnector dbc;
	private ORMLoader ol;
	private Criteria c;

	@BeforeAll
	public void setup() {
		this.dbc = new MariaDBConnector(3306, "localhost", "root", "", "test_orm", true);
		this.ol = new ORMLoader(dbc);
	}

	@Test
	public void testReadQuery() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.lt("Value", 9);
			String query = this.dbc.generateReadQuery(c);

			assertEquals(query, "SELECT * FROM Nota  WHERE Nota.Value < 9");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testReadQuery2Hierarchy() {
		this.c = this.ol.createCriteria(SUV.class);
		try {
			this.c.lt("HorsePower", 300);
			String query = this.dbc.generateReadQuery(c);

			assertEquals(query, "SELECT * FROM SUV  INNER JOIN Car ON SUV.Carcid = Car.cid WHERE SUV.HorsePower < 300");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testReadQuery3Hierarchy() {
		this.c = this.ol.createCriteria(WhiteSUV.class);
		try {
			this.c.lt("HorsePower", 300);
			String query = this.dbc.generateReadQuery(c);

			assertEquals(query,
					"SELECT * FROM WhiteSUV  INNER JOIN SUV ON WhiteSUV.SUVsid = SUV.sid INNER JOIN Car ON SUV.Carcid = Car.cid WHERE SUV.HorsePower < 300"
							+ "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateQuery() {
		try {
			List<String> query = this.dbc.generateCreateQuery(new Nota(4), Nota.class);

			assertEquals(query.get(0), "INSERT INTO Nota (Value) VALUES  (4.0)");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateQuery2Hierarchy() {
		try {
			List<String> query = this.dbc.generateCreateQuery(new SUV("BMW", "alb", "MH69KOL", 4, 120), SUV.class);

			assertEquals(query.get(0), "INSERT INTO SUV (HorsePower , Carcid) VALUES  (120 , LAST_INSERT_ID())");
			assertEquals(query.get(1),
					"INSERT INTO Car (Model,Color,RegistrationNumber,Age) VALUES  ('BMW','alb','MH69KOL',4)");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateQuery3Hierarchy() {
		try {
			List<String> query = this.dbc.generateCreateQuery(new WhiteSUV("BMW", "MH69KOL", 4, 120), WhiteSUV.class);

			assertEquals(query.get(0), "INSERT INTO WhiteSUV (SUVsid) VALUES  (LAST_INSERT_ID())");
			assertEquals(query.get(1), "INSERT INTO SUV (HorsePower , Carcid) VALUES  (120 , LAST_INSERT_ID())");
			assertEquals(query.get(2),
					"INSERT INTO Car (Model,Color,RegistrationNumber,Age) VALUES  ('BMW','white','MH69KOL',4)");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateQuery() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.like("Value", "ana%");
			String query = this.dbc.generateUpdateQuery(c, new Nota(4));
			assertEquals(query, "UPDATE Nota  SET Nota.Value=4.0 WHERE Nota.Value LIKE 'ana%'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateQuery2Hierarchy() {
		this.c = this.ol.createCriteria(SUV.class);
		try {
			this.c.gt("HorsePower", 10);
			Car car = new SUV("BMW", "red", "TM43GOG", 12, 1200);
			TableData tdsuv = ClassMapper.getInstance().getTableData(car.getClass());
			tdsuv.pk_field.set(car, 3);
			TableData tdcar = tdsuv.parentTable;
			tdcar.pk_field.set(car, 11);
			String query = this.dbc.generateUpdateQuery(c, car);
			assertEquals(query,
					"UPDATE SUV  INNER JOIN Car ON SUV.Carcid = Car.cid SET SUV.HorsePower=1200 , Car.Model='BMW',Car.Color='red',Car.RegistrationNumber='TM43GOG',Car.Age=12 WHERE SUV.HorsePower > 10");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateQuery3Hierarchy() {
		this.c = this.ol.createCriteria(SUV.class);
		try {
			this.c.gt("HorsePower", 10);
			Car car = new WhiteSUV("BMW", "TM43GOG", 12, 1200);
			TableData tdwsuv = ClassMapper.getInstance().getTableData(car.getClass());
			tdwsuv.pk_field.set(car, 3);
			TableData tdsuv = tdwsuv.parentTable;
			tdsuv.pk_field.set(car, 11);
			TableData tdcar = tdsuv.parentTable;
			tdcar.pk_field.set(car, 3);
			String query = this.dbc.generateUpdateQuery(c, car);
			assertEquals(query,
					"UPDATE WhiteSUV  INNER JOIN SUV ON WhiteSUV.SUVsid = SUV.sid INNER JOIN Car ON SUV.Carcid = Car.cid SET SUV.HorsePower=1200 , Car.Model='BMW',Car.Color='white',Car.RegistrationNumber='TM43GOG',Car.Age=12 WHERE SUV.HorsePower > 10");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDeleteQuery() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.like("Value", "ana%");
			String query = this.dbc.generateDeleteQuery(c);
			assertEquals(query, " DELETE  Nota  FROM Nota  WHERE Nota.Value LIKE 'ana%'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDeleteQuery2Hierarchy() {
		this.c = this.ol.createCriteria(SUV.class);
		try {
			this.c.lt("HorsePower", 300);
			String query = this.dbc.generateDeleteQuery(c);

			assertEquals(query,
					" DELETE  Car ,  SUV  FROM SUV  INNER JOIN Car ON SUV.Carcid = Car.cid WHERE SUV.HorsePower < 300");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDeleteQuery3Hierarchy() {
		this.c = this.ol.createCriteria(WhiteSUV.class);
		try {
			this.c.lt("HorsePower", 300);
			String query = this.dbc.generateDeleteQuery(c);

			assertEquals(query,
					" DELETE  Car ,  SUV ,  WhiteSUV  FROM WhiteSUV  INNER JOIN SUV ON WhiteSUV.SUVsid = SUV.sid INNER JOIN Car ON SUV.Carcid = Car.cid WHERE SUV.HorsePower < 300"
							+ "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDeleteTableQuery() {
		try {
			List<String> query = this.dbc.generateDeleteTableQuery(ClassMapper.getInstance().getTableData(Nota.class));
			assertEquals(query.get(0), "DROP TABLE Nota ; ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDeleteTableQuery2Hierarchy() {
		try {
			List<String> query = this.dbc.generateDeleteTableQuery(ClassMapper.getInstance().getTableData(SUV.class));

			assertEquals(query.get(0), "DROP TABLE SUV ; ");
			assertEquals(query.get(1), "DROP TABLE Car ; ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDeleteTableQuery3Hierarchy() {
		try {
			List<String> query = this.dbc
					.generateDeleteTableQuery(ClassMapper.getInstance().getTableData(WhiteSUV.class));
			assertEquals(query.get(0), "DROP TABLE WhiteSUV ; ");
			assertEquals(query.get(1), "DROP TABLE SUV ; ");
			assertEquals(query.get(2), "DROP TABLE Car ; ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateTableQuery() {
		try {
			List<String> query = this.dbc.generateCreateTableQuery(ClassMapper.getInstance().getTableData(Nota.class));
			assertEquals(query.get(0),
					"CREATE TABLE IF NOT EXISTS Nota(Value FLOAT , nid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( nid ) );");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateTableQuery2Hierarchy() {
		try {
			List<String> query = this.dbc.generateCreateTableQuery(ClassMapper.getInstance().getTableData(SUV.class));

			assertEquals(query.get(0),
					"CREATE TABLE IF NOT EXISTS SUV(HorsePower INTEGER , sid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( sid )  , Carcid INTEGER ,  CONSTRAINT SUVCar FOREIGN KEY (Carcid) REFERENCES Car (cid) ON DELETE CASCADE ON UPDATE RESTRICT);");
			assertEquals(query.get(1),
					"CREATE TABLE IF NOT EXISTS Car(Model VARCHAR(255) , Color VARCHAR(255) , RegistrationNumber VARCHAR(255) , Age INTEGER , cid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( cid ) );");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateTableQuery3Hierarchy() {
		try {
			List<String> query = this.dbc
					.generateCreateTableQuery(ClassMapper.getInstance().getTableData(WhiteSUV.class));
			assertEquals(query.get(0),
					"CREATE TABLE IF NOT EXISTS WhiteSUV(wsid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( wsid )  , SUVsid INTEGER ,  CONSTRAINT WhiteSUVSUV FOREIGN KEY (SUVsid) REFERENCES SUV (sid) ON DELETE CASCADE ON UPDATE RESTRICT);");
			assertEquals(query.get(1),
					"CREATE TABLE IF NOT EXISTS SUV(HorsePower INTEGER , sid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( sid )  , Carcid INTEGER ,  CONSTRAINT SUVCar FOREIGN KEY (Carcid) REFERENCES Car (cid) ON DELETE CASCADE ON UPDATE RESTRICT);");
			assertEquals(query.get(2),
					"CREATE TABLE IF NOT EXISTS Car(Model VARCHAR(255) , Color VARCHAR(255) , RegistrationNumber VARCHAR(255) , Age INTEGER , cid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( cid ) );");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
