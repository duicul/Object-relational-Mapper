package ObjectRelationalMapper;

import java.util.List;

import criteria.Criteria;
import database.DBConnector;
import database.MariaDBConnector;
import loader.ORMLoader;
import testClasses.Nota;
import testClasses.Car;

public class MainTest {

	public static void main(String[] args) {
		DBConnector dbc=new MariaDBConnector(3306,"localhost","root","", "test_orm",true);
		ORMLoader ol = new ORMLoader(dbc);
		ol.createTable(Nota.class);
		Criteria c = ol.createCriteria(Nota.class);
		c.gt("Value", 2);
		List<Object> objs = ol.get(c);
		for(Object o:objs) {
			Nota n = (Nota) o;
			System.out.println(ol.getJSON(n));
		}
		System.out.println(objs.toString());
		ol.insert(new Nota(6));
		ol.createTable(Car.class);
		ol.insert(new Car("BMW","roz","MH89GOG",12));
		Criteria ccar = ol.createCriteria(Car.class);
		ccar.gt("Age", 4);
		List<Object> objCar = ol.get(ccar);
		for(Object car:objCar) {
			Car cr = (Car) car;
			System.out.println(ol.getJSON(cr));
		}
	}

}
