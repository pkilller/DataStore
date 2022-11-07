package com.pk.datastore.sample;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataStoreManager {
    private final Context context;

    private static final String BASE_URI = "content://com.pk.datastore.data";

    private static final String DATASTORE_NAME = "name";

    private static final String DATASTORE_KEY = "key";
    private static final String DATASTORE_VALUE = "value";

    public DataStoreManager(Context _context) {
        context = _context;
    }

    public void insertKV(String space, String key, String value) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(String.format("%s/%s/%s", BASE_URI, space, key));
        ContentValues cv = new ContentValues();
        cv.put(DATASTORE_VALUE, value);
        cr.insert(uri, cv);
    }

    public String getValue(String space, String key) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(String.format("%s/%s/%s", BASE_URI, space, key));
        Cursor cur = cr.query(uri, null, null, null, null);
        int indexOfvalue = cur.getColumnIndex(DATASTORE_VALUE);
        while (cur.moveToNext()) {
            return cur.getString(indexOfvalue);
        }
        return null;
    }

    public HashMap<String, String> getAllKV(String space) {
        HashMap<String, String> kvs = new HashMap<>();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(String.format("%s/%s", BASE_URI, space));
        Cursor cur = cr.query(uri, null, null, null, null);
        int indexOfKey = cur.getColumnIndex(DATASTORE_KEY);
        int indexOfvalue = cur.getColumnIndex(DATASTORE_VALUE);
        while (cur.moveToNext()) {
            String key = cur.getString(indexOfKey);
            String value = cur.getString(indexOfvalue);
            kvs.put(key, value);
        }
        return kvs;
    }

    public void deleteKV(String space, String key) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(String.format("%s/%s/%s", BASE_URI, space, key));
        cr.delete(uri,null, null);
    }

    public void insertSpace(String space) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(String.format("%s/%s", BASE_URI, space));
        ContentValues cv = new ContentValues();
        cr.insert(uri, cv);
    }

    public List<String> getAllSpace() {
        List<String> spaces = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(String.format(BASE_URI));
        Cursor cur = cr.query(uri, null, null, null, null);
        int nameOfKey = cur.getColumnIndex(DATASTORE_NAME);
        while (cur.moveToNext()) {
            String name = cur.getString(nameOfKey);
            spaces.add(name);
        }
        return spaces;
    }

    public void deleteSpace(String space) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(String.format("%s/%s", BASE_URI, space));
        cr.delete(uri,null, null);
    }

}
