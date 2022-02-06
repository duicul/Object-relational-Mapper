package testClasses;

import java.util.List;

import annotation.Column;
import annotation.OneToMany;
import annotation.PK;
import annotation.Table;

@Table(name = "People")
public class People {	
	@Column(name="Name")
	public String name;
	
	@OneToMany()
	public List<Car> c;
	
	@PK(name="pid",autoincrement=true)
	public int pid;
	
	public People() {
		
	}
	
	public People(List<Car> c,String name) {
		this.name = name;
		this.c=c;		
	}
	
}
