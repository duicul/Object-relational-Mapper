package testClasses;

import annotation.Column;
import annotation.PK;
import annotation.Table;

@Table(name = "Door")
public class Door {
	@Column(name="Length")
	public int length;
	@Column(name="Width")
	public int width;
	@PK(name="did")
	public int did;
	public Door() {
	}
	
	public Door(int length,int width) {
		this.length=length;
		this.width=width;
	}

}
