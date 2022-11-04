package com.pk.datastore.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class KVDataAdapter {
    public static final String TABLE_DEFAULT = "default";

    public final String FN_KEY = "key";
    public final String FN_VALUE = "value";

    public final String FN_NAME = "name";
    public final String TB_SQLITE_MASTER = "sqlite_master";

    private final KVDataBaseHelper dbHelper;
    private final SQLiteDatabase db;

    private List<String> tables;

    public KVDataAdapter(Context context) {
        dbHelper = new KVDataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        tables = getAllTables();
    }

    private void addToCV(ContentValues values, String fieldName, Object value) {
        if (value instanceof String) {
            values.put(fieldName, (String) value);
        } else if (value instanceof Integer) {
            values.put(fieldName, (Integer) value);
        } else if (value instanceof Long) {
            values.put(fieldName, (Long) value);
        } else if (value instanceof Float) {
            values.put(fieldName, (Float) value);
        } else if (value instanceof Double) {
            values.put(fieldName, (Double) value);
        }
    }

    private ContentValues toCV(String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(FN_KEY, key);
        cv.put(FN_VALUE, value);
        return cv;
    }

    public List<String> getTables() {
        return tables;
    }

    public void createTable(String table) {
        db.execSQL(String.format(KVDataBaseHelper.SQL_CREATE_TABLE, table));
        tables = getAllTables();
    }

    public void dropTable(String table) {
        db.execSQL(String.format(KVDataBaseHelper.SQL_DROP_TABLE, table));
        tables = getAllTables();
    }

    public void insert(String table, String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(FN_KEY, key);
        cv.put(FN_VALUE, value);
        db.insert(table, null, cv);
        // update value.
        update(table, key, value);
    }

    public void delete(String table, String key) {
        db.delete(table, FN_KEY + "=?", new String[]{String.valueOf(key)});
    }

    public void update(String table, String key, String value) {
        ContentValues cv =  new ContentValues();
        cv.put(FN_KEY, key);
        cv.put(FN_VALUE, value);
        db.update(table, cv, FN_KEY + "=?", new String[]{String.valueOf(key)});
    }

    public String getValue(String table, String key) {
        String value = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, new String[]{FN_VALUE}, FN_KEY + "=?", new String[]{key}, null, null, null);
        int indexOfValue = cursor.getColumnIndex(FN_VALUE);
        if (cursor.moveToNext()) {
            value = cursor.getString(indexOfValue);
        }
        cursor.close();
        return value;
    }

    public List<KVBean> queryAllKV(String table) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, null, null, null, null, null, null);
        List<KVBean> kvs = new ArrayList<>();
        int indexOfKey = cursor.getColumnIndex(FN_KEY);
        int indexOfValue = cursor.getColumnIndex(FN_VALUE);
        while (cursor.moveToNext()) {
            KVBean kvbean = new KVBean();
            kvbean.setKey(cursor.getString(indexOfKey));
            kvbean.setValue(cursor.getString(indexOfValue));
            kvs.add(kvbean);
        }
        cursor.close();
        return kvs;
    }

    public List<String> getAllTables() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TB_SQLITE_MASTER, new String[]{FN_NAME}, "type=?",  new String[]{"table"}, null, null, null);
        List<String> tables = new ArrayList<>();
        int indexOfName = cursor.getColumnIndex(FN_NAME);
        while (cursor.moveToNext()) {
            String tableName = cursor.getString(indexOfName);
            if (tableName.equals("android_metadata") || tableName.equals(TABLE_DEFAULT)) {
                continue;
            }
            tables.add(tableName);
        }
        cursor.close();
        return tables;
    }

    public static class KVDataBaseHelper extends SQLiteOpenHelper {
        public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS \"%s\" " +
                "(\"key\" char(500) NOT NULL, \"value\" char(65535), PRIMARY KEY(\"key\"))";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS \"%s\"";

        private static final String DB_NAME = "data_store.db";
        private static final int VERSION = 1;

        public KVDataBaseHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format(SQL_CREATE_TABLE, TABLE_DEFAULT));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(String.format(SQL_DROP_TABLE, TABLE_DEFAULT));
            db.execSQL(String.format(SQL_CREATE_TABLE, TABLE_DEFAULT));
        }
    }
}