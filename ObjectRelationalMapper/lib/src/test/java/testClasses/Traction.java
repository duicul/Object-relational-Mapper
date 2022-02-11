package testClasses;

import annotation.Column;
import annotation.PK;
import annotation.Table;

@Table(name = "Traction")
public class Traction {
	@Column(name="Ratio")
	public float ratio;
	@PK(name="tid")
	public int tid;
	public Traction() {
	}
	
	public Traction(float ratio) {
		this.ratio=ratio;
	}

}
