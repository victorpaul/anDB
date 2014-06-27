package com.sukinsan.anDB.anDB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Table;
import com.sukinsan.anDB.anDB.schema.SchemaColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by viktor_2 on 6/27/14.
 */
public class TableCreator {

	Table tableInfo;
	List<Field> fields;
	SQLiteDatabase sqLite;
	List<SchemaColumn> schemaColumns;

	public TableCreator(Table tableInfo, List<Field> fields,SQLiteDatabase sqLite){
		this.tableInfo = tableInfo;
		this.fields = fields;
		this.sqLite = sqLite;
		this.schemaColumns = getSchemaColumns();

		createTable();
		updateTable();
	}

	private List<SchemaColumn> getSchemaColumns(){
		final List<SchemaColumn> schemaColumns_ = new ArrayList<SchemaColumn>();
		try {
			Log.i("E","row");
			new QueryResultReader("PRAGMA table_info("+tableInfo.name()+");",sqLite) {
				@Override
				public void loopThrougResults(Cursor cursor) {
					SchemaColumn schemaColumn = new SchemaColumn();
					Log.i("E","row");
					schemaColumn.setName(cursor.getString(cursor.getColumnIndex("name")));
					schemaColumn.setType(cursor.getString(cursor.getColumnIndex("type")));
					schemaColumn.setDfltValue(cursor.getString(cursor.getColumnIndex("")));
					schemaColumn.setNotNull(cursor.getInt(cursor.getColumnIndex("notnull"))==1);
					schemaColumn.setPrimaryKey(cursor.getInt(cursor.getColumnIndex("pk"))==1);

					schemaColumns_.add(schemaColumn);
				}
			};
		}catch(Exception e){
			Log.e("ERROR", e.getMessage());
		}
		return schemaColumns_;
	}

	private void updateTable(){
		Log.i("schemaColumns",schemaColumns.toString());
	}

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

		sqLite.execSQL(queryCreateTable.toString());
	}
}