package com.sukinsan.anDB.entity;

import android.util.Log;

import com.sukinsan.anDB.anDB.BaseTable;
import com.sukinsan.anDB.anDB.annotations.*;

/**
 * Created by victorPaul on 6/19/14.
 */

@Table(name="user")
public class User extends BaseTable{

    @Column(name="id",type="integer", AUTOINCREMENT = true, PRIMARY_KEY = true)
	private int id;

	@Column(name="name", type="text")
    private String name;

	@Column(name="email", type="text")
	private String email;

	@Column(name="password", type="text")
	private String password;

	public User() {
		super();
		Log.i("user","created");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
