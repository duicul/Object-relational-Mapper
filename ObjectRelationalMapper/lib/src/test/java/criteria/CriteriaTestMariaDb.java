package criteria;

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
public class CriteriaTestMariaDb {
	private DBConnector dbc;
	private ORMLoader ol;
	private Criteria c;

	@BeforeAll
	public void setup() {
		this.dbc = new MariaDBConnector(3306, "localhost", "root", "", "test_orm", true);
		this.ol = new ORMLoader(dbc);
	}

	@Test
	public void testLT() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.lt("Value", 9);
		} catch (WrongColumnName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(this.c.getCriteriaText(), " WHERE Nota.Value < 9");
	}

	@Test
	public void testLike() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.like("Value", "ana%");
		} catch (WrongColumnName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(this.c.getCriteriaText(), " WHERE Nota.Value LIKE 'ana%'");
	}

	@Test
	public void testMultiple() {
		this.c = this.ol.createCriteria(Nota.class);
		try {
			this.c.like("Value", "ana%");

			this.c.gt("Value", 3);
		} catch (WrongColumnName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(this.c.getCriteriaText(), " WHERE Nota.Value LIKE 'ana%' AND Nota.Value > 3");
	}
	
	@Test
	public void testNoCriteria() {
		this.c = this.ol.createCriteria(Nota.class);
		assertEquals(this.c.getCriteriaText(), "");
	}

}
