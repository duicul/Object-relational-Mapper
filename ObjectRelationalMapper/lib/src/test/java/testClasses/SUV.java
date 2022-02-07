package testClasses;

import annotation.Column;
import annotation.PK;
import annotation.Table;

@Table(name = "SUV")
public class SUV extends Car {
	@Column(name="HorsePower")
    public int hp;
	@PK(name="sid")
	public int sid;
	
	public SUV() {
		
	}
	
	public SUV(String model,String color,String reg_no, int age, int hp) {
		super(model, color, reg_no,  age);
		this.hp=hp;
	}
}
