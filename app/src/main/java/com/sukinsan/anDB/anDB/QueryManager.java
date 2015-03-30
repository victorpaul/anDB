package com.sukinsan.anDB.anDB;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by viktor_2 on 7/3/14.
 */
public class QueryManager {
	private final static String TAG = QueryManager.class.getSimpleName();

	SQLiteDatabase sqLite;

	public QueryManager(SQLiteDatabase sqLite) {
		this.sqLite = sqLite;
	}

	public boolean executeQuery(String query){
		return executeQuery(query,sqLite);
	}

	public static boolean executeQuery(String query, SQLiteDatabase sqLite){
		Log.i(TAG, "Execute query: " + query);
		try {
			sqLite.execSQL(query);
		}catch(Exception e){
			Log.e(TAG,e.getMessage());
			return false;
		}
		return true;
	}

}
