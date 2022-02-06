package testClasses;

import annotation.Column;
import annotation.PK;
import annotation.Table;

@Table(name = "Car")
public class Car {
	@Column(name="Model")
	public String model;
	@Column(name="Color")
	public String color;
	@Column(name="RegistrationNumber")
	public String reg_no;
	@Column(name="Age")
	public int age;
	@PK(name="cid")
	public int cid;
	
	public Car() {
		
	}
	
	public Car(String model,String color,String reg_no, int age) {
		this.color=color;
		this.model=model;
		this.reg_no=reg_no;
		this.age=age;
	}
	
}
