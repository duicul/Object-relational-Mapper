package ObjectRelationalMapper;

import java.util.List;

import criteria.Criteria;
import database.DBConnector;
import database.MariaDBConnector;
import loader.ORMLoader;
import testClasses.Nota;

public class MainTest {

	public static void main(String[] args) {
		DBConnector dbc=new MariaDBConnector(3306,"localhost","root","", "test_orm",true);
		ORMLoader ol = new ORMLoader(dbc);
		ol.createTable(Nota.class);
		Criteria c = ol.createCriteria(Nota.class);
		c.gt("Value", 2);
		List<Object> objs = ol.get(c);
		System.out.println(objs.toString());
	}

}
