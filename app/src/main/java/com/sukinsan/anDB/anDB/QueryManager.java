package com.sukinsan.anDB.anDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sukinsan.anDB.anDB.abstracts.BaseEntity;
import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Table;
import com.sukinsan.anDB.anDB.schema.SchemaColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victorPaul on 6/27/14.
 */
public class QueryManager {
    public final static String MAIN_ID = "id";
    public interface QueryReader{
        public void loop(Cursor cursor) throws Exception;
    }
	private final static String TAG = QueryManager.class.getSimpleName();
    private SQLiteDatabase sqLite;
    private SchemaManager schemaManager;

	public QueryManager(SQLiteDatabase sqLite){
		this.sqLite = sqLite;
        this.schemaManager = new SchemaManager();
	}

    /**
     * execute query
     * @param query mySql query
     * @return true/false
     */
    public boolean executeQuery(String query){
        Log.i(TAG, "Execute query: " + query);
        try {sqLite.execSQL(query);}catch(Exception e){Log.e(TAG,e.getMessage());return false;}
        return true;
    }

    public <BaseEntity> void create(Class<BaseEntity> baseEntity){
        Table tableInfo = schemaManager.getTable(baseEntity);
        List<Field> fields = schemaManager.getFields(baseEntity);
        List<SchemaColumn> schemaColumns = schemaManager.getSchemaColumns(tableInfo,this);
        if(schemaColumns.size() == 0){
            createTable(tableInfo,fields);
        }else{
            update(tableInfo, fields, schemaColumns);
        }
    }

    public <BaseEntity> boolean drop(Class<BaseEntity> baseEntity){
        Table tableInfo = schemaManager.getTable(baseEntity);
        return executeQuery("DROP TABLE IF EXISTS `"+tableInfo.name()+"`;");
    }

    /**
     * INSERT/REPLACE
     * @param baseEntity
     * @return
     */
    public long insert(BaseEntity baseEntity){
        ContentValues values = schemaManager.getInsertValues(baseEntity);
        if(values == null){
            return 0;
        }
        Table table = schemaManager.getTable(baseEntity.getClass());
        return sqLite.insertWithOnConflict(table.name(), null, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public <T> String generateSelect(String where, Class<? extends BaseEntity>... entities){
        StringBuilder SELECT = new StringBuilder("SELECT ");
        StringBuilder FROM = new StringBuilder("FROM ");
        for(Class<? extends BaseEntity> entity : entities){
            Table table = schemaManager.getTable(entity);
            SELECT.append(sw(table.name()) + ".*,");
            FROM.append(sw(table.name())+",");
        }

        SELECT.deleteCharAt(SELECT.length()-1);// remove last comma
        FROM.deleteCharAt(FROM.length()-1);// remove last comma

        return SELECT.toString() + " " + FROM.toString() + " " + where;
    }

    public <T> List<T> querySelectFrom(String query, final Class<? extends BaseEntity>... tables){
        final ArrayList<T> entities = new ArrayList<T>();
        resultReader(generateSelect(query,tables), new QueryManager.QueryReader() {
            @Override
            public void loop(Cursor cursor) throws Exception {
                T entity = (T)schemaManager.createEntityFromCursor(cursor,tables[0]);
                entities.add(entity);
            }
        });
        return entities;
    }

    /**
     * DELETE
     * @param userTable
     * @return
     */
    public boolean delete(BaseEntity userTable){
        Table tableInfo = schemaManager.getTable(userTable.getClass());
        userTable.beforeDelete(userTable);
        int deleteCode = sqLite.delete(tableInfo.name(),MAIN_ID+" = ?",new String[]{String.valueOf(userTable.getId())});
        return (deleteCode==1);
    }

	private void update(Table table, List<Field> fields, List<SchemaColumn> schemaColumns){
		if(fields.size() != schemaColumns.size() || !schemaManager.isSchemaUpToDate(schemaColumns,fields)){
			String columnsForMigration = schemaManager.getColumnsForMigration(schemaColumns,fields);

			String tempTableName = "temporaryTableName_"+table.name();
			executeQuery("ALTER TABLE " + sw(table.name()) + " RENAME TO " + sw(tempTableName) + ";");
			if(createTable(table,fields)) {
                executeQuery("INSERT INTO " + sw(table.name()) + " (" + columnsForMigration + ") SELECT " + columnsForMigration + " FROM " + sw(tempTableName) + ";");
                executeQuery("DROP TABLE " + sw(tempTableName) + ";");
            }else{
                executeQuery("ALTER TABLE " + sw(tempTableName) + " RENAME TO " + sw(table.name()) + ";");
            }
		}else{
			Log.i(TAG,"Table '" + table.name() + "' no needs to be updated");
		}
	}

    /**
     * To create a table in sqlite DB
     * @param table information about the table
     * @param fields information about fields
     * @return
     */
	private boolean createTable(Table table,List<Field> fields){
		StringBuilder queryCreateTable = new StringBuilder("CREATE TABLE " + sw(table.name()) + "(");
        StringBuilder queryCreateIndexes = new StringBuilder("");

		for(Field field : fields){
			Column column = field.getAnnotation(Column.class);
			queryCreateTable.append(sw(column.name()) + " " + column.type());
			if(column.PRIMARY_KEY()){queryCreateTable.append(" PRIMARY KEY ON CONFLICT REPLACE");}
			if(column.AUTOINCREMENT()){queryCreateTable.append(" AUTOINCREMENT");}
			queryCreateTable.append(",");
            String indexSql = getIndexSQLSyntax(table, column);
            if (indexSql != null){
                queryCreateIndexes.append(indexSql);
            }
        }
		queryCreateTable.deleteCharAt(queryCreateTable.length()-1);// remove last comma
		queryCreateTable.append(");");
        queryCreateTable.append(queryCreateIndexes.toString());

		if(executeQuery(queryCreateTable.toString())){
            return true;
		}
        executeQuery("DROP TABLE IF EXISTS " + sw(table.name()) + ";");
		return false;
	}

    /**
     * generate sql query to create index
     * @param table
     * @param column
     * @return
     */
    private String getIndexSQLSyntax(Table table, Column column){
        if(!column.index().name().isEmpty()) {
            return "CREATE " + (column.index().unique() ? "UNIQUE" : "") + " INDEX " + sw(column.index().name()) + " ON " + sw(table.name()) + " ( " + sw(column.name()) + " " + column.index().sortBy() + ");";
        }
        return null;
    }

    /**
     * wrap table/field name by ``
     * @param field table/field name
     * @return
     */
    public String sw(String field){
        return "`" + field + "`";
    }

    public void resultReader(String query,QueryReader qr){
        Log.i(TAG,query);
        Cursor cursor = sqLite.rawQuery(query, null);
        Log.i(TAG,"Found "+cursor.getCount()+" records");
        if (cursor.moveToFirst()){
            try {
                do {
                    qr.loop(cursor);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
    }
}