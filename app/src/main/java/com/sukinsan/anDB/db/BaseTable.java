package com.sukinsan.anDB.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.sukinsan.anDB.db.annotations.Column;
import com.sukinsan.anDB.db.annotations.Table;

/**
 * Created by victorPAul on 6/25/14.
 */
public class BaseTable{

	public boolean isTable(){
		return this.getClass().isAnnotationPresent(Table.class);
	}

	public Table getTable() {
		return this.getClass().getAnnotation(Table.class);
	}

	public List<Field> getAnnotatedFields() {
		List<Field> annotatedFields = new ArrayList<Field>();
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field field:fields){
			if(field.isAnnotationPresent(Column.class)) {
				annotatedFields.add(field);
			}
		}

		return annotatedFields;
	}
}
