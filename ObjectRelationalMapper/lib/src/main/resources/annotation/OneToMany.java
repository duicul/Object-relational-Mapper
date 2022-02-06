package annotation;


import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
  
@Retention(RUNTIME)
public @interface OneToMany {
	public String table() default "";
}
