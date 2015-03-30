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

import com.sukinsan.anDB.anDB.abstracts.BaseTable;
import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Table;

/**
 * Created by victorPaul on 6/19/14.
 */
public class DBHandler extends SQLiteOpenHelper {
    private final static String TAG = DBHandler.class.getSimpleName();

	public final static String TABLE_ID = "id";
	private final static String DB_NAME = "ssAnDB";
	private SQLiteDatabase sqLite;
	private QueryManager qm;

	public DBHandler(Context context) {
		super(context, DB_NAME, null, 1);
		sqLite = getWritableDatabase();
		qm = new QueryManager(sqLite);

	}

	private void log(String errorMsg){
		Log.e("LOG",errorMsg);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * Get table info from class
	 */
	private final static <T> Table extractTableInfo(Class<T> userTable){
		if(userTable.isAnnotationPresent(Table.class)){
			return userTable.getAnnotation(Table.class);
		}
		Log.e(TAG,"Class '"+userTable.getSimpleName()+ "' do not contain Table annotation");
		return null;
	}

	/**
	 * Get table info from object
	 */
	private static Table extractTableInfo(BaseTable userTable){
		return extractTableInfo(userTable.getClass());
	}

	/**
	 * get fields that are table's columns from class
	 */
	private static <T> List<Field> extractAnnotatedFields(Class<T> userTable) {
		List<Field> annotatedFields = new ArrayList<Field>();
		// get parent fields
		for(Field field:userTable.getSuperclass().getDeclaredFields()){
			if(field.isAnnotationPresent(Column.class)) {
				annotatedFields.add(field);
			}
		}
		// then, let's get userTable's fields
		for(Field field: userTable.getDeclaredFields()){
			if(field.isAnnotationPresent(Column.class)) {
				annotatedFields.add(field);
			}
		}
		return annotatedFields;
	}

	/**
	 * get fields that are table's columns from object
	 */
	private static List<Field> extractAnnotatedFields(BaseTable userTable) {
		return extractAnnotatedFields(userTable.getClass());
	}

	/**
	 * will try to create/update a table in DB
	 */
	public <T> void create(Class<T> userTable){
		Table tableInfo = extractTableInfo(userTable);
		if(tableInfo != null){
			List<Field> fields = extractAnnotatedFields(userTable);
			new TableManager(tableInfo,fields,sqLite);
		}
	}

	/**
	 * insert or replace
	 */
	public long insert(BaseTable baseTable){
		Table tabelInfo = extractTableInfo(baseTable);
		if(tabelInfo == null){
            return 0;
        }

        ContentValues values = new ContentValues();
        try {
            for (Field field : extractAnnotatedFields(baseTable.getClass())){
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);

                // set int
                if(field.getType().isAssignableFrom(Integer.TYPE)) {
                    if(!column.AUTOINCREMENT()) {
                        values.put(column.name(), field.getInt(baseTable));
                    }
                    continue;
                }

                // set String
                if(field.getType().isAssignableFrom(String.class)) {
                    values.put(column.name(),(String)field.get(baseTable));
                    continue;
                }

                // set Double
                if(field.getType().isAssignableFrom(Double.class)) {
                    values.put(column.name(),field.getDouble(baseTable));
                    continue;
                }

                // set Float
                if(field.getType().isAssignableFrom(Float.class)) {
                    values.put(column.name(),field.getFloat(baseTable));
                    continue;
                }

                Log.e(TAG,"can't set column '"+ column.name() +"' for field '"+field.getName()+"' with field type type '"+field.getType()+"'");
            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
            return 0;
        }
        return sqLite.insert(tabelInfo.name(), null, values);

	}

	/**
	 * Select from query
	 */
	public <T> List<T> select(String query, final Class<T> userTable){
		final List<Field> fields = extractAnnotatedFields(userTable);
		final ArrayList<T> entities = new ArrayList<T>();

		try {
			new QueryResultReader(query, sqLite) {
				@Override
				public void loopThroughResults(Cursor cursor) {
					try{
						T entity = userTable.newInstance();
						for(Field field:fields){
							field.setAccessible(true);
							Column column = field.getAnnotation(Column.class);

							// get int
							if(field.getType().isAssignableFrom(Integer.TYPE)) {
								field.set(entity,cursor.getInt(cursor.getColumnIndex(column.name())));
								continue;
							}

							// get String
							if(field.getType().isAssignableFrom(String.class)) {
								field.set(entity,cursor.getString(cursor.getColumnIndex(column.name())));
								continue;
							}

							// get Double
							if(field.getType().isAssignableFrom(Double.class)) {
								field.set(entity,cursor.getDouble(cursor.getColumnIndex(column.name())));
								continue;
							}

							// get Float
							if(field.getType().isAssignableFrom(Float.class)) {
								field.set(entity,cursor.getFloat(cursor.getColumnIndex(column.name())));
								continue;
							}

                            Log.e(TAG,"can't get column '"+ column.name() +"' for field '"+field.getName()+"' with field type '"+field.getType()+"'");
						}
						entities.add(entity);

					}catch(Exception e){
                        Log.e(TAG,e.getMessage());
					}

				}
			};
		}catch(Exception e){
            Log.e(TAG,e.getMessage());
		}

		return entities;
	}

	/**
	 * Delete
	 */
	public boolean delete(BaseTable userTable){
		Table tableInfo = extractTableInfo(userTable.getClass());
		if(tableInfo != null) {
			userTable.beforeDelete(userTable);
			int deleteCode = sqLite.delete(tableInfo.name(),TABLE_ID+" = ?",new String[]{String.valueOf(userTable.getId())});
			return (deleteCode==1);
		}
		return false;
	}

	/**
	 * DROP TABLE
	 */
	public boolean drop(BaseTable userTable){
		Table tableInfo = extractTableInfo(userTable);
		if(tableInfo != null){
			return qm.executeQuery("DROP TABLE IF EXISTS "+tableInfo.name()+";");
		}
		return false;
	}
}