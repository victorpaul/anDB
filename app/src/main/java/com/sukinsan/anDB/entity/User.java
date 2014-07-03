package com.sukinsan.anDB.entity;

import android.util.Log;

import com.sukinsan.anDB.anDB.abstracts.BaseTable;
import com.sukinsan.anDB.anDB.annotations.*;

import java.lang.reflect.Field;

/**
 * Created by victorPaul on 6/19/14.
 */

@Table(name="user",indexes = {@Index(name="index_id",column = "id",sortBy = "DESC"),@Index(name="index_text1",column = "text1",unique = false)})
public class User extends BaseTable{

	@Column(name="name", type="TEXT")
    private String name;

	@Column(name="text1", type="TEXT")
	private String email;

	@Column(name="text2", type="TEXT")
	private String password;

	@Column(name="integer", type="INT")
	private int fieldInt;

	@Column(name="real", type="REAL")
	private int fieldReal2;

	public User() {
		super();
		Log.i("user","created");
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", fieldInt=" + fieldInt +
				", fieldReal2=" + fieldReal2 +
				'}';
	}

	@Override
	public void beforeDelete(BaseTable baseTable) {

	}
}