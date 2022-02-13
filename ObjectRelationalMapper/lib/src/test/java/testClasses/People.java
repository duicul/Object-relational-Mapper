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
	public List<WhiteSUV> c;
	
	@PK(name="pid",autoincrement=true)
	public int pid;
	
	public People() {
		
	}
	
	public People(List<WhiteSUV> c,String name) {
		this.name = name;
		this.c=c;		
	}
	
}
