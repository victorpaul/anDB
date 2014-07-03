package com.sukinsan.anDB.anDB.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by victorPaul on 7/2/14.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
	String name();
	String column();
	boolean unique() default false;
	String sortBy() default "ASC";
}
