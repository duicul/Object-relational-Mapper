package testClasses;

import annotation.Column;
import annotation.PK;
import annotation.Table;

@Table(name="Nota")
public class Nota {
      @Column(name="Value")
      public float val;
      @PK(name="nid")
      public int nid;
      
      public Nota() {
	    
      }
      
      public Nota(float val) {
	    this.val=val;
      }

}
