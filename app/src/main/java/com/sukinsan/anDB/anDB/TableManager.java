package com.sukinsan.anDB.anDB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Index;
import com.sukinsan.anDB.anDB.annotations.Table;
import com.sukinsan.anDB.anDB.schema.SchemaColumn;
import com.sukinsan.anDB.anDB.schema.SchemaIndex;

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
	QueryManager qm;
	List<SchemaColumn> schemaColumns;
	List<SchemaIndex> schemaindexes;

	public TableManager(Table tableInfo, List<Field> fields,SQLiteDatabase sqLite){

		qm = new QueryManager(sqLite);
		this.tableInfo = tableInfo;
		this.fields = fields;
		this.sqLite = sqLite;
		this.schemaColumns = getSchemaColumns();
		this.schemaindexes = getSchemaIndexes();

		if(this.schemaColumns.size() == 0){
			createTable();
		}else{
			updateTable();
		}

	}

	private List<SchemaIndex> getSchemaIndexes(){
		final List<SchemaIndex> schemaIndexes_ = new ArrayList<SchemaIndex>();
		try {
			new QueryResultReader("SHOW INDEX FROM `"+tableInfo.name()+"`;",sqLite) {
				@Override
				public void loopThroughResults(Cursor cursor) {
					SchemaIndex schemaIndex = new SchemaIndex();
					schemaIndexes_.add(schemaIndex);
				}
			};
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
		return schemaIndexes_;
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

			qm.executeQuery("ALTER TABLE `" + tableInfo.name() + "` RENAME TO `" + tempTableName + "`;");
			createTable();
			qm.executeQuery("INSERT INTO `" + tableInfo.name() + "` (" + columnsForMigration + ") SELECT " + columnsForMigration + " FROM `" + tempTableName + "`;");
			qm.executeQuery("DROP TABLE `" + tempTableName + "`;");

		}else{
			Log.i(TAG,"Table '" + tableInfo.name() + "' no needs to be updated");
		}
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
	private boolean createTable(){
		StringBuilder queryCreateTable = new StringBuilder("CREATE TABLE "+tableInfo.name()+"(");

		for(Field field : fields){
			Column column = field.getAnnotation(Column.class);
			queryCreateTable.append(column.name()+" "+column.type());

			if(column.PRIMARY_KEY()){queryCreateTable.append(" PRIMARY KEY ON CONFLICT REPLACE");}
			if(column.AUTOINCREMENT()){queryCreateTable.append(" AUTOINCREMENT");}

			queryCreateTable.append(",");
		}
		queryCreateTable.deleteCharAt(queryCreateTable.length()-1);// remove last comma
		queryCreateTable.append(");");

		if(!qm.executeQuery(queryCreateTable.toString())){
			return false;
		}

		for(Index index:tableInfo.indexes()){
			if(!qm.executeQuery("CREATE " + (index.unique() ? "UNIQUE" : "") + " INDEX `" + index.name() + "` ON `" + tableInfo.name() + "` ( " + index.column() + " " + index.sortBy() + ");")){
				return false;
			}
		}

		return true;
	}
}