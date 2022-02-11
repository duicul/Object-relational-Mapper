package testClasses;

import java.util.List;

import annotation.Column;
import annotation.PK;
import annotation.Table;

@Table(name = "WhiteSUV")
public class WhiteSUV extends SUV {
	
	@PK(name="wsid")
	public int wsid;
	
	public WhiteSUV() {
		
	}
	
	public WhiteSUV(String model,String reg_no, int age, int hp,List<Door> doors,Traction traction) {
		super(model, "white", reg_no,  age,hp,doors,traction);
	}
}
