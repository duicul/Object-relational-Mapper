package testClasses;

import java.util.List;

import annotation.Column;
import annotation.OneToOne;
import annotation.PK;
import annotation.Table;

@Table(name = "SUV")
public class SUV extends Car {
	@Column(name="HorsePower")
    public int hp;
	@PK(name="sid")
	public int sid;
	@OneToOne()
	public Traction traction;
	
	public SUV() {
		
	}
	
	public SUV(String model,String color,String reg_no, int age, int hp,List<Door> doors,Traction traction) {
		super(model, color, reg_no,  age,doors);
		this.hp=hp;
		this.traction=traction;
	}
}
