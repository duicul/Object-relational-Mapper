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

			assertEquals(query, "SELECT * FROM Nota WHERE Nota.Value < 9");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateQuery() {
		try {
			List<String> query = this.dbc.generateCreateQuery(new Nota(4),Nota.class);

			assertEquals(query.get(0), "INSERT INTO Nota (Value) VALUES  (4.0)");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateQuery2Hierarchy() {
		try {
			List<String> query = this.dbc.generateCreateQuery(new SUV("BMW","alb" , "MH69KOL", 4, 120),SUV.class);
			
			assertEquals(query.get(0),"INSERT INTO SUV (HorsePower,Carcid) VALUES  (120,LAST_INSERT_ID())");
			assertEquals(query.get(1),"INSERT INTO Car (Model,Color,RegistrationNumber,Age) VALUES  ('BMW','alb','MH69KOL',4)");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateQuery3Hierarchy() {
		try {
			List<String> query = this.dbc.generateCreateQuery(new WhiteSUV("BMW", "MH69KOL", 4, 120),WhiteSUV.class);
			
			assertEquals(query.get(0),"INSERT INTO WhiteSUV (HorsePower,SUVsid) VALUES  (0,LAST_INSERT_ID())");
			assertEquals(query.get(1),"INSERT INTO SUV (HorsePower,Carcid) VALUES  (120,LAST_INSERT_ID())");
			assertEquals(query.get(2),"INSERT INTO Car (Model,Color,RegistrationNumber,Age) VALUES  ('BMW','white','MH69KOL',4)");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateQury() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.like("Value", "ana%");
			String query = this.dbc.generateUpdateQuery(c,new Nota(4));
			assertEquals(query, "UPDATE Nota SET Value=4.0 WHERE Nota.Value LIKE 'ana%'");
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
			assertEquals(query, "DELETE FROM Nota WHERE Nota.Value LIKE 'ana%'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testDeleteTableQuery() {
		try {
			String query = this.dbc.generateDeleteTableQuery(ClassMapper.getInstance().getTableData(Nota.class));
			assertEquals(query, "DROP TABLE Nota");
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
			assertEquals(query.get(0), "CREATE TABLE IF NOT EXISTS Nota(Value FLOAT , nid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( nid ) );");
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
			
			assertEquals(query.get(0),"CREATE TABLE IF NOT EXISTS SUV(HorsePower INTEGER , sid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( sid )  , Carcid INTEGER ,  CONSTRAINT SUVCar FOREIGN KEY (Carcid) REFERENCES Car (cid) ON DELETE CASCADE ON UPDATE RESTRICT);");
			assertEquals(query.get(1),"CREATE TABLE IF NOT EXISTS Car(Model VARCHAR(255) , Color VARCHAR(255) , RegistrationNumber VARCHAR(255) , Age INTEGER , cid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( cid ) );");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateTableQuery3Hierarchy() {
		try {
			List<String> query = this.dbc.generateCreateTableQuery(ClassMapper.getInstance().getTableData(WhiteSUV.class));
			assertEquals(query.get(0),"CREATE TABLE IF NOT EXISTS WhiteSUV(HorsePower INTEGER , wsid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( wsid )  , SUVsid INTEGER ,  CONSTRAINT WhiteSUVSUV FOREIGN KEY (SUVsid) REFERENCES SUV (sid) ON DELETE CASCADE ON UPDATE RESTRICT);");
			assertEquals(query.get(1),"CREATE TABLE IF NOT EXISTS SUV(HorsePower INTEGER , sid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( sid )  , Carcid INTEGER ,  CONSTRAINT SUVCar FOREIGN KEY (Carcid) REFERENCES Car (cid) ON DELETE CASCADE ON UPDATE RESTRICT);");
			assertEquals(query.get(2),"CREATE TABLE IF NOT EXISTS Car(Model VARCHAR(255) , Color VARCHAR(255) , RegistrationNumber VARCHAR(255) , Age INTEGER , cid  INTEGER  AUTO_INCREMENT ,  PRIMARY KEY ( cid ) );");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
