package com.sukinsan.anDB.entity;

import android.util.Log;

import com.sukinsan.anDB.anDB.abstracts.BaseTable;
import com.sukinsan.anDB.anDB.annotations.*;

/**
 * Created by victorPaul on 6/19/14.
 */

@Table(name="user")
public class User extends BaseTable{

	@Column(name="name", type="TEXT")
    private String name;

	@Column(name="email", type="CHAR(254)")
	private String email;

	@Column(name="password", type="CHAR(20)")
	private String password;

	@Column(name="field_int", type="INT")
	private int fieldInt;

	@Column(name="field_real", type="REAL")
	private int fieldReal;

	public User() {
		super();
		Log.i("user","created");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getFieldInt() {
		return fieldInt;
	}

	public void setFieldInt(int fieldInt) {
		this.fieldInt = fieldInt;
	}

	public int getFieldReal() {
		return fieldReal;
	}

	public void setFieldReal(int fieldReal) {
		this.fieldReal = fieldReal;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", fieldInt=" + fieldInt +
				", fieldReal=" + fieldReal +
				'}';
	}

	@Override
	public void beforeDelete(BaseTable baseTable) {

	}
}