package com.sukinsan.anDB.entity;

import android.util.Log;

import com.sukinsan.anDB.anDB.abstracts.BaseEntity;
import com.sukinsan.anDB.anDB.annotations.*;

/**
 * Created by victorPaul on 6/19/14.
 */

@Table(name="user")
public class User extends BaseEntity {

	@Column(name="name", type="TEXT")
    public String name;

	@Column(name="text1", type="TEXT", index=@Index(name="index_text1",unique = false))
    public String email;

	@Column(name="text2", type="TEXT")
    public String password;

	@Column(name="integer", type="INTEGER")
    public int fieldInt;

	@Column(name="real", type="REAL")
    public double fieldReal2;

	public User() {
		super();
		Log.i("user","created");
	}

	@Override
	public String toString() {
		return "User{" +
                "  id='" + id + '\'' +
				"  name='" + name + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", fieldInt=" + fieldInt +
				", fieldReal2=" + fieldReal2 +
				'}';
	}

	@Override
	public void beforeDelete(BaseEntity baseEntity) {

	}
}