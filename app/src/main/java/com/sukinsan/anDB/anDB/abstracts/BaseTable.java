package com.sukinsan.anDB.anDB.abstracts;

import com.sukinsan.anDB.anDB.annotations.Column;


/**
 * Created by victorPAul on 6/25/14.
 */
public abstract class BaseTable{

	@Column(name="id",type="INTEGER", AUTOINCREMENT = true, PRIMARY_KEY = true)
	protected int id; // shouldn't be changed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	abstract public void beforeDelete(BaseTable baseTable);
}