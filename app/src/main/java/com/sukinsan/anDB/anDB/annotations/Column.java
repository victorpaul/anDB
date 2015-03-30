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
    Index index() default @Index(name="");
	String name();
	String type(); // TEXT,INTEGER,REAL,BLOB,INT,CHAR(50)
	boolean PRIMARY_KEY() default false; //true,false
	boolean AUTOINCREMENT() default false; //true,false
	boolean NOT_NULL() default false; //true,false

}
