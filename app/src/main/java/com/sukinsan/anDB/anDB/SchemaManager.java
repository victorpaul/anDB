package com.sukinsan.anDB.anDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.sukinsan.anDB.anDB.abstracts.BaseEntity;
import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Table;
import com.sukinsan.anDB.anDB.schema.SchemaColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by victor on 31.03.15.
 */
public class SchemaManager {

    private static final String TAG = SchemaManager.class.getSimpleName();

    public <T> Table getTable(Class<T> entity){
        if(entity.isAnnotationPresent(Table.class)){
            return entity.getAnnotation(Table.class);
        }
        Log.e(TAG,"Class '"+entity.getSimpleName()+ "' do not contain Table annotation");
        return null;
    }

    public <T> List<Field> getFields(Class<T> entity) {
        List<Field> annotatedFields = new ArrayList<Field>();
        for(Field field:entity.getSuperclass().getDeclaredFields()){
            if(field.isAnnotationPresent(Column.class)) {
                annotatedFields.add(field);
            }
        }
        for(Field field: entity.getDeclaredFields()){
            if(field.isAnnotationPresent(Column.class)) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields;
    }

    public String getColumnsForMigration(List<SchemaColumn> schemaColumns,List<Field> fields){
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
        Log.i(TAG, "List of fields for migration: " + matchedColumns);

        Iterator<String> iter = matchedColumns.iterator();
        StringBuilder builder = new StringBuilder(iter.next());
        while( iter.hasNext() ) {
            builder.append(",").append(iter.next());
        }
        return builder.toString();
    }

    public boolean isSchemaUpToDate(List<SchemaColumn> schemaColumns,List<Field> fields){
        Log.i(TAG,"isSchemaUpToDate()");
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

    public List<SchemaColumn> getSchemaColumns(Table table, QueryManager qm){
        Log.i(TAG,"getSchemaColumns()");
        final List<SchemaColumn> schemaColumns = new ArrayList<SchemaColumn>();
        qm.resultReader("PRAGMA table_info(" + table.name() + ");", new QueryManager.QueryReader() {
            @Override
            public void loop(Cursor cursor) {
            SchemaColumn schemaColumn = new SchemaColumn();
            schemaColumn.setPrimaryKey(cursor.getInt(cursor.getColumnIndex("pk")) == 1);
            schemaColumn.setName(cursor.getString(cursor.getColumnIndex("name")));
            schemaColumn.setType(cursor.getString(cursor.getColumnIndex("type")));
            schemaColumn.setDfltValue(cursor.getString(cursor.getColumnIndex("dflt_value")));
            schemaColumn.setNotNull(cursor.getInt(cursor.getColumnIndex("notnull")) == 1);
            schemaColumns.add(schemaColumn);
            }
        });
        return schemaColumns;
    }

    public ContentValues getInsertValues(BaseEntity baseEntity){
        ContentValues values = new ContentValues();
        try {
            for (Field field : getFields(baseEntity.getClass())){
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);

                if(field.getType().isAssignableFrom(Integer.TYPE)){
                    int value = field.getInt(baseEntity);
                    if(value > 0) {
                        values.put(column.name(), value);
                    }
                    continue;
                }
                if(field.getType().isAssignableFrom(String.class)) {
                    values.put(column.name(),(String)field.get(baseEntity));
                    continue;
                }
                if(field.getType().isAssignableFrom(Double.TYPE)) {
                    values.put(column.name(),field.getDouble(baseEntity));
                    continue;
                }
                if(field.getType().isAssignableFrom(Float.TYPE)) {
                    values.put(column.name(),field.getFloat(baseEntity));
                    continue;
                }

                Log.e(TAG,"can't set column '"+ column.name() +"' for field '"+field.getName()+"' with field type '"+field.getType()+"'");
            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
            return null;
        }
        return values;
    }

    public <T> T createEntityFromCursor(Cursor cursor,Class<T> table) throws Exception{
        T entity = table.newInstance();
        final List<Field> fields = getFields(table);
        for (Field field : fields) {

            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);

            if (field.getType().isAssignableFrom(Integer.TYPE)) {
                field.set(entity, cursor.getInt(cursor.getColumnIndex(column.name())));
                continue;
            }
            if (field.getType().isAssignableFrom(String.class)) {
                field.set(entity, cursor.getString(cursor.getColumnIndex(column.name())));
                continue;
            }
            if (field.getType().isAssignableFrom(Double.TYPE)) {
                field.set(entity, cursor.getDouble(cursor.getColumnIndex(column.name())));
                continue;
            }
            if (field.getType().isAssignableFrom(Float.TYPE)) {
                field.set(entity, cursor.getFloat(cursor.getColumnIndex(column.name())));
                continue;
            }
            Log.e(TAG, "can't get column '" + column.name() + "' for field '" + field.getName() + "' with field type '" + field.getType() + "'");
        }
        return entity;
    }

}
