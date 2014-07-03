package com.sukinsan.anDB.anDB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Index;
import com.sukinsan.anDB.anDB.annotations.Table;
import com.sukinsan.anDB.anDB.schema.SchemaColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by victorPaul on 6/27/14.
 */
public class TableManager {

	private final static String TAG = "TableManager";

	Table tableInfo;
	List<Field> fields;
	SQLiteDatabase sqLite;
	List<SchemaColumn> schemaColumns;

	public TableManager(Table tableInfo, List<Field> fields,SQLiteDatabase sqLite){
		this.tableInfo = tableInfo;
		this.fields = fields;
		this.sqLite = sqLite;
		this.schemaColumns = getSchemaColumns();

		if(this.schemaColumns.size() == 0){
			createTable();
		}else{
			updateTable();
		}

	}

	private List<SchemaColumn> getSchemaColumns(){
		final List<SchemaColumn> schemaColumns_ = new ArrayList<SchemaColumn>();
		try {
			new QueryResultReader("PRAGMA table_info("+tableInfo.name()+");",sqLite) {
				@Override
				public void loopThroughResults(Cursor cursor) {
					SchemaColumn schemaColumn = new SchemaColumn();
					schemaColumn.setPrimaryKey(cursor.getInt(cursor.getColumnIndex("pk"))==1);
					schemaColumn.setName(cursor.getString(cursor.getColumnIndex("name")));
					schemaColumn.setType(cursor.getString(cursor.getColumnIndex("type")));
					schemaColumn.setDfltValue(cursor.getString(cursor.getColumnIndex("dflt_value")));
					schemaColumn.setNotNull(cursor.getInt(cursor.getColumnIndex("notnull"))==1);
					schemaColumns_.add(schemaColumn);
				}
			};
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
		return schemaColumns_;
	}

	/**
	 * update table if it needs to be updated
	 */
	private void updateTable(){
		if(fields.size() != schemaColumns.size() || !isSchemaUpToDate()){
			String columnsForMigration = getColumnsForMigration();

			String tempTableName = "temporaryTableName"+tableInfo.name();
			execute("ALTER TABLE " + tableInfo.name() + " RENAME TO " + tempTableName + ";");
			createTable();
			execute("INSERT INTO " + tableInfo.name() + " (" + columnsForMigration + ") SELECT " + columnsForMigration + " FROM " + tempTableName + ";");
			execute("DROP TABLE " + tempTableName + ";");

		}else{
			Log.i(TAG,"Table '" + tableInfo.name() + "' no needs to be updated");
		}
	}

	private void execute(String query){
		Log.i(TAG,"Execute query: "+query);
		sqLite.execSQL(query);
	}

	private boolean isSchemaUpToDate(){
		boolean schemaIsUpToDate = true;
		int totalFieldsMatched = 0;

		for(Field field: fields){
			for(SchemaColumn schemaColumn:schemaColumns){
				Column column = field.getAnnotation(Column.class);
				if(schemaColumn.getName().equals(column.name())){

					if(schemaColumn.isPrimaryKey() != column.PRIMARY_KEY()){
						Log.e(TAG,"Column "+schemaColumn.getName()+ " has wrong PRIMARY KEY");
						return false;
					}
					if(column.NOT_NULL() != schemaColumn.isNotNull()){
						Log.e(TAG,"Column "+schemaColumn.getName()+ " has wrong NOT NULL");
						return false;
					}
					if(!column.type().equals(schemaColumn.getType())){
						Log.e(TAG,"Column "+schemaColumn.getName()+ " has wrong TYPE");
						return false;
					}
					if(column.PRIMARY_KEY() != schemaColumn.isPrimaryKey()){
						Log.e(TAG,"Column "+schemaColumn.getName()+ " has wrong PRIMARY KEY");
						return false;
					}
					totalFieldsMatched++;
					break;
				}
			}
		}

		if(fields.size() != totalFieldsMatched){
			schemaIsUpToDate = false;
			Log.i(TAG,"Only "+totalFieldsMatched +" fields out of "+ fields.size() + " are matched with table");
		}

		return schemaIsUpToDate;
	}

	/**
	 * get list of columns that we can move to the table with new structure
	 */
	private String getColumnsForMigration(){
		List<String> matchedColumns = new ArrayList<String>();
		for(Field field: fields){
			for(SchemaColumn schemaColumn:schemaColumns){
				Column column = field.getAnnotation(Column.class);
				if(schemaColumn.getName().equals(column.name()) && schemaColumn.getType().equals(column.type())){
					matchedColumns.add(column.name());
					break;
				}
			}
		}
		Log.i(TAG,"List of fields for migration: " + matchedColumns);

		Iterator<String> iter = matchedColumns.iterator();
		StringBuilder builder = new StringBuilder(iter.next());
		while( iter.hasNext() ) {
			builder.append(",").append(iter.next());
		}
		return builder.toString();
	}


	/**
	 * create a table
	 */
	private void createTable(){
		boolean appendCommaNextTime = false;
		StringBuilder queryCreateTable = new StringBuilder("CREATE TABLE "+tableInfo.name()+"(");

		for(Field field : fields){
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
		queryCreateTable.append(");");//close query

		execute(queryCreateTable.toString());

		/*
		for(Index index:tableInfo.indexes()){
			execute("CREATE UNIQUE INDEX " + index.name() + " ON " + tableInfo.name() + " ( " + index.column() + " ASC);");
		}//*/

	}
}