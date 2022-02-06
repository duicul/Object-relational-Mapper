package criteria;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import criteria.Criteria;
import database.DBConnector;
import database.MariaDBConnector;
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
		this.c.lt("Value", 9);
		assertEquals(this.c.getCriteriaText(), "Nota.Value < 9");
	}
	
	@Test
	public void testLike() {
		this.c = this.ol.createCriteria(Nota.class);
		this.c.like("Value", "ana%");
		assertEquals(this.c.getCriteriaText(), "Nota.Value LIKE 'ana%'");
	}
	
	@Test
	public void testMultiple() {
		this.c = this.ol.createCriteria(Nota.class);
		this.c.like("Value", "ana%");
		this.c.gt("Value", 3);
		assertEquals(this.c.getCriteriaText(), "Nota.Value LIKE 'ana%' AND Nota.Value > 3");
	}

}
