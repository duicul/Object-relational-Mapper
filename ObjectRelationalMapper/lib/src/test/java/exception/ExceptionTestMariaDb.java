package exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import criteria.Criteria;
import database.DBConnector;
import database.MariaDBConnector;
import exception.WrongColumnName;
import loader.ORMLoader;
import testClasses.Nota;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;

@TestInstance(Lifecycle.PER_CLASS)
public class ExceptionTestMariaDb {
	private DBConnector dbc;
	private ORMLoader ol;
	private Criteria c;

	@BeforeAll
	public void setup() {
		this.dbc = new MariaDBConnector(3306, "localhost", "root", "", "test_orm", true);
		this.ol = new ORMLoader(dbc);
	}

	@Test
	public void testLTWrongColumn() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.lt("Valuenone", 9);
			fail();
		} catch (WrongColumnName e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		assertEquals(this.c.getCriteriaText(), "");
	}

	@Test
	public void testLikeWrongColumn() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.like("Valuenone", "ana%");
			fail();
		} catch (WrongColumnName e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		assertEquals(this.c.getCriteriaText(), "");
	}

	@Test
	public void testMultipleWrongColumn() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.like("Value", "ana%");

			this.c.gt("Valuenone", 3);
			fail();
		} catch (WrongColumnName e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		assertEquals(this.c.getCriteriaText(), " WHERE Nota.Value LIKE 'ana%'");
	}

}
