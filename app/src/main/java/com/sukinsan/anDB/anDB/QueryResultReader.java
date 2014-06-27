package com.sukinsan.anDB.anDB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by victorPaul on 6/27/14.
 */
public abstract class QueryResultReader {

	public QueryResultReader(String query,SQLiteDatabase sqLite) throws Exception{
		Cursor cursor = sqLite.rawQuery(query, null);

		for(String cname:cursor.getColumnNames()){
			Log.i("QueryResultReader","Column name ="+cname);
		}

		Log.i("QueryResultReader","Found "+cursor.getCount()+" records");
		if (cursor.moveToFirst()){
			do {
				loopThrougResults(cursor);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	public abstract void loopThrougResults(Cursor cursor);

}
