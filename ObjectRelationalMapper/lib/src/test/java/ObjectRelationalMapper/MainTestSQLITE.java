package ObjectRelationalMapper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import criteria.Criteria;
import database.DBConnector;
import database.MariaDBConnector;
import database.SQLiteDBConenctor;
import exception.WrongColumnName;
import loader.ClassMapper;
import loader.ORMLoader;
import loader.TableData;
import testClasses.Car;
import testClasses.Door;
import testClasses.Nota;
import testClasses.StudentLiterature;
import testClasses.Traction;
import testClasses.WhiteSUV;

public class MainTestSQLITE {
	public static void main(String argv[]) {
		String currentPath = System.getProperty("user.dir");
		Path p = Paths.get(currentPath).getParent();
		System.out.println(currentPath);
		System.out.println(p);
		
		Traction trac = new Traction(7);
		List<Door> doors = new LinkedList<Door>();
		doors.add(new Door(3,4));
		doors.add(new Door(5,6));
		DBConnector dbc = new SQLiteDBConenctor(p + "\\test_db.db", true);
		ORMLoader ol = new ORMLoader(dbc);
		/*ol.createTable(Nota.class);
		ol.insert(new Nota(7));
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
		ol.insert(new Car("BMW", "roz", "MH89GOG", 12, doors));
		objCar = ol.get(ccar);
		for (Object car : objCar) {
			Car cr = (Car) car;
			System.out.println(ol.getJSON(cr));
			cr.age = 34;
			cr.color = "Green";
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
			cr.age = 34;
			cr.color = "Yellow";
			ol.update(liceC, cr);
		}

		ol.createTable(WhiteSUV.class);
		ol.insert(new WhiteSUV("BMW", "MH69KOL", 4, 120, doors, trac));

		Criteria wS = ol.createCriteria(WhiteSUV.class);
		try {
			wS.lt("HorsePower", 300);
		} catch (WrongColumnName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Object whiteSuv : ol.get(wS)) {
			WhiteSUV ws = (WhiteSUV) whiteSuv;
			System.out.println(ol.getJSON(ws));
			ws.age = 34;
			ws.hp = 13;
			ol.update(ws);
		}
		// ol.dropTable(WhiteSUV.class);*/
		//ol.createTable(WhiteSUV.class);
		ol.createTable(StudentLiterature.class);
		List<WhiteSUV> ws = new LinkedList<WhiteSUV>();
		ws.add(new WhiteSUV("Audi", "TM78IOP", 12, 90, doors,trac));
		ws.add(new WhiteSUV("Audi1", "TM78IOP2", 12, 90, doors,trac));
		StudentLiterature st = new StudentLiterature(ws, 7, "Meth", "Gogu", new Nota(8));
		ol.insert(st);
		//TableData td = ClassMapper.getInstance().getTableData(StudentLiterature.class);
		//System.out.println(td);
	}
}
