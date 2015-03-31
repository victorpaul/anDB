package com.sukinsan.anDB.anDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.sukinsan.anDB.anDB.abstracts.BaseEntity;
import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Table;

/**
 * Created by victorPaul on 6/19/14.
 */
public class DBHandler extends SQLiteOpenHelper {
    private final static String TAG = DBHandler.class.getSimpleName();

	public final static String DB_NAME = "ssAnDB";
	private SQLiteDatabase sqLite;
	private QueryManager qm;

	public DBHandler(Context context) {
		super(context, DB_NAME, null, 1);
		sqLite = getWritableDatabase();
		qm = new QueryManager(sqLite);
	}

    /**
     * get query manager
     * @return
     */
    public QueryManager getQM(){
        return qm;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}