package annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
  
@Retention(RUNTIME)
public @interface OneToOne {
	public String table() default "";
}
