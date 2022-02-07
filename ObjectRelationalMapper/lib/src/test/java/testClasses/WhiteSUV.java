package testClasses;

import annotation.Column;
import annotation.PK;
import annotation.Table;

@Table(name = "WhiteSUV")
public class WhiteSUV extends SUV {
	@Column(name="HorsePower")
    public int hp;
	
	@PK(name="wsid")
	public int wsid;
	
	public WhiteSUV() {
		
	}
	
	public WhiteSUV(String model,String reg_no, int age, int hp) {
		super(model, "white", reg_no,  age,hp);
	}
}
