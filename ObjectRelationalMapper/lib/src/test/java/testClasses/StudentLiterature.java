package testClasses;

import java.util.List;

import annotation.Column;
import annotation.PK;
import annotation.Table;

@Table(name="StudentLiterature")
public class StudentLiterature extends Student {
	
	@Column(name="specialization")
	public String spec;
	
	@PK(name="slid")
	public int slid;
	
	public StudentLiterature() {
		
	}
	
	public StudentLiterature(List<Car> c, int grade,String spec,String name,Nota n) {
		super(c, grade,name, n);
		this.spec = spec;
	}


	

}
