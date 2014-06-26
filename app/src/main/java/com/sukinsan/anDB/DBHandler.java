package com.sukinsan.anDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.sukinsan.anDB.anDB.BaseTable;
import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Table;
import com.sukinsan.anDB.entity.User;

/**
 * Created by victorPaul on 6/19/14.
 */
public class DBHandler extends SQLiteOpenHelper {
	private final static String DB_NAME = "qw";
	private SQLiteDatabase sqLite;

	public DBHandler(Context context) {
		super(context, DB_NAME, null, 1);
		sqLite = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void createTable(BaseTable baseTable){

		if(baseTable.getClass().isAnnotationPresent(Table.class)){
			Table table = User.class.getAnnotation(Table.class);

			boolean appendCommaNextTime = false;
			StringBuilder queryCreateTable = new StringBuilder("CREATE TABLE "+table.name()+"(");

			Field[] fields = baseTable.getClass().getDeclaredFields();
			for(Field field : fields){
				if(field.isAnnotationPresent(Column.class)){
					if(appendCommaNextTime){
						queryCreateTable.append(",");
					}
					Column coluumn = field.getAnnotation(Column.class);
					queryCreateTable.append(coluumn.name()+" "+coluumn.type());
					if(coluumn.PRIMARY_KEY()){
						queryCreateTable.append(" PRIMARY KEY");
					}
					if(coluumn.AUTOINCREMENT()){
						queryCreateTable.append(" AUTOINCREMENT");
					}

					appendCommaNextTime = true;
				}
			}

			queryCreateTable.append(");");//close query

			Log.i("QUERY", queryCreateTable.toString());

			sqLite.execSQL(queryCreateTable.toString());
		}
	}

	public long insertInto(BaseTable baseTable){
		if(baseTable.isTable()){
			ContentValues values = new ContentValues();
			try {
				for (Field field : baseTable.getAnnotatedFields()) {
					field.setAccessible(true);
					Column column = field.getAnnotation(Column.class);

					if(field.getType().isAssignableFrom(String.class)) {
						values.put(column.name(),(String)field.get(baseTable));
						continue;
					}
					if(field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(Integer.TYPE)) {
						if(!column.AUTOINCREMENT()) {
							values.put(column.name(), field.getInt(baseTable));
						}
						continue;
					}
					Log.e("ERROR","can't find field type "+field.getType());
				}
			}catch (Exception e){
				Log.e("ERROR",e.getMessage());
				return 0;
			}
			return sqLite.insert(baseTable.getTable().name(), null, values);
		}
		return 0;
	}


	private <T> List<T> readFromQuery(String query,Class<T> userTable){
		ArrayList<T> entities = new ArrayList<T>();
		Cursor cursor = sqLite.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			Field[] fields = userTable.getDeclaredFields();
			do {
				try{
					T entity = userTable.newInstance();
					for(Field field:fields){
						field.setAccessible(true);
						Column column = field.getAnnotation(Column.class);
						if(field.getType().isAssignableFrom(String.class)) {
							field.set(entity,cursor.getString(cursor.getColumnIndex(column.name())));
							continue;
						}
						if(field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(Integer.TYPE)) {
							field.set(entity,cursor.getInt(cursor.getColumnIndex(column.name())));
							continue;
						}
						Log.e("ERROR","can't read field's type "+field.getType());
					}
					entities.add(entity);
				}catch (Exception e){
					Log.e("ERROR",e.getMessage());
				}
			} while (cursor.moveToNext());
		}
		cursor.close();

		return entities;
	}


	public <T> List<T> readAllFrom(Class <T> userTable){
		if(!userTable.isAnnotationPresent(Table.class)){
			return null;
		}
		Table table = userTable.getAnnotation(Table.class);

		return readFromQuery("SELECT * FROM "+table.name(),userTable);
	}

	public void dropTable(BaseTable baseTable){
		if(baseTable.isTable()){
			sqLite.execSQL("DROP TABLE IF EXISTS "+baseTable.getTable().name()+";");
		}
	}
}