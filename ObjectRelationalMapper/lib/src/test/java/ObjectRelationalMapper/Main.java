package ObjectRelationalMapper;

import java.util.ArrayList;
import java.util.List;

import annotation.Table;

public class Main {
 public static void main(String args[]) {
        /* for(Table t:ORMConverter.getRelatingTables(ORMConverter.extractHierarchicalData(StudentLiterature.class)))
               System.out.println(t.name());
         try {
               DatabaseConnector dbc=new MariaDBConnector(3306,"127.0.0.1","root","", "demo_orm",true);
               //SELECT LAST_INSERT_ID();
               ORMLoader ol=new ORMLoader(dbc);
               ol.dropTable(StudentLiterature.class);
               CriteriaSet c=ol.setCriteria(StudentLiterature.class);
               c.gt("Value", 7);
               //c.like("Model", "d%");
               //c.like("specialization","Drama");
               //c.orderAsc("grade");
               List<Car> lc=new ArrayList<Car>();
               lc.add(new Car("a","b","c"));
               lc.add(new Car("d","e","f"));
               List<Car> lc1=new ArrayList<Car>();
               lc1.add(new Car("q","r","t"));
               //ol.insert(new People(null,"Fane"));
               ol.insert(new StudentLiterature(lc,5, "Drama", "Gica",new Nota((float) 7.8)));
               ol.insert(new StudentLiterature(lc1,9, "Mate", "Boss",new Nota((float) 3)));
		List<Object> ls=c.extract();
		c.remove();
		System.out.println(ls);
		for(Object o:ls) {
		      StudentLiterature sl=(StudentLiterature) o;
		      	System.out.println();
		      	String nota=sl.n!=null?sl.n.nid+" "+sl.n.val:"";
			System.out.println(sl.name+" "+sl.grade+" "+sl.spec+" "+sl.pid+" "+sl.sid+" "+sl.slid+" "+nota);
			if(sl.c!=null)
			for(Car cr:sl.c)
			      System.out.println(cr.cid+" "+cr.color+" "+cr.model+" "+cr.reg_no);
			System.out.println();
		}
	} catch ( DbDriverNotFound | CommunicationException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (ConstructorException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
      } catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
      } catch (DeleteComposition e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
      }
	 
	 //ol.insert(new Student(null,5,"Popa"));
	 
	 /*try {
		System.out.println(dbc.checkTable("peoples"));
		ol.get(StudentLiterature.class,"name", "Popa");
	} catch (DbDriverNotFound | CommunicationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	Integer t=3;*/
 }
}
