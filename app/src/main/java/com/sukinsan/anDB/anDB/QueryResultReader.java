package com.sukinsan.anDB.anDB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by victorPaul on 6/27/14.
 */
public abstract class QueryResultReader {
	private final static String TAG = "QueryResultReader";

	public QueryResultReader(String query,SQLiteDatabase sqLite) throws Exception{
		Log.i(TAG,query);
		Cursor cursor = sqLite.rawQuery(query, null);
		Log.i(TAG,"Found "+cursor.getCount()+" records");
		if (cursor.moveToFirst()){
			do {
				loopThroughResults(cursor);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	public abstract void loopThroughResults(Cursor cursor);

}
