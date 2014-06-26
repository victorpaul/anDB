package com.sukinsan.anDB.anDB.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by victorPaul on 6/24/14.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	String name();
	String type();
	boolean PRIMARY_KEY() default false;
	boolean AUTOINCREMENT() default false;

}
