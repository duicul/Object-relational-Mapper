package ObjectRelationalMapper;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import criteria.Criteria;
import database.DBConnector;
import database.MariaDBConnector;
import exception.WrongColumnName;
import loader.ORMLoader;
import testClasses.Nota;
import testClasses.Car;

public class MainTest {
	private static final Logger logger = LogManager.getLogger(MainTest.class);
	public static void main(String[] args) {
		logger.error("test");
		DBConnector dbc = new MariaDBConnector(3306, "localhost", "root", "", "test_orm", true);
		ORMLoader ol = new ORMLoader(dbc);
		ol.createTable(Nota.class);
		Criteria c = ol.createCriteria(Nota.class);
		try {
			c.gt("Value", 2);
		} catch (WrongColumnName e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		List<Object> objs = ol.get(c);
		for (Object o : objs) {
			Nota n = (Nota) o;
			System.out.println(ol.getJSON(n));
		}
		ol.delete(c);
		ol.insert(new Nota(6));
		ol.createTable(Car.class);
		Criteria ccar = ol.createCriteria(Car.class);
		try {
			ccar.gt("Age", 4);
		} catch (WrongColumnName e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<Object> objCar = ol.get(ccar);
		for (Object car : objCar) {
			Car cr = (Car) car;
			System.out.println(ol.getJSON(cr));
		}
		ol.delete(ccar);
		ol.insert(new Car("BMW", "roz", "MH89GOG", 12));
		objCar = ol.get(ccar);
		for (Object car : objCar) {
			Car cr = (Car) car;
			System.out.println(ol.getJSON(cr));
			cr.age=34;
			cr.color="Green";
			ol.update(cr);
		}
		Criteria liceC = ol.createCriteria(Car.class);
		try {
			liceC.like("RegistrationNumber", "MH89%");
		} catch (WrongColumnName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		objCar = ol.get(liceC);
		for (Object car : objCar) {
			Car cr = (Car) car;
			System.out.println(ol.getJSON(cr));
			cr.age=34;
			cr.color="Yellow";
			ol.update(liceC, cr);
		}
	}

}
