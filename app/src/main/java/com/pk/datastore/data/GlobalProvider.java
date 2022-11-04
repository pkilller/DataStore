package com.pk.datastore.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.List;


public class GlobalProvider extends ContentProvider {
    private static final String AUTHORITY = "com.pk.datastore.data";

    private static final int CODE_ROOT = 1;     // content://com.pk.datastore.data/
    private static final int CODE_SPACE = 2;    // content://com.pk.datastore.data/{SPACE}
    private static final int CODE_ITEM = 3;     // content://com.pk.datastore.data/{SPACE}/{ITEM_KEY}

    //text/plain
    private static final String ITEM_TYPE_PREFIX = "vnd.android.cursor.item/";
    private static final String DIR_TYPE_PREFIX = "vnd.android.cursor.dir/";

    private static final String ROOT = "ROOT";

    public final String FN_KEY = "key";
    public final String FN_VALUE = "value";
    public final String FN_NAME = "name";

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "/", CODE_ROOT);
        uriMatcher.addURI(AUTHORITY, "/*", CODE_SPACE);
        uriMatcher.addURI(AUTHORITY, "/*/*", CODE_ITEM);
    }

    private KVDataAdapter dbAdapter;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbAdapter = new KVDataAdapter(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CODE_ROOT:
                return DIR_TYPE_PREFIX + ROOT;
            case CODE_SPACE:
            case CODE_ITEM:
                String space = uri.getPathSegments().get(0);
                return ITEM_TYPE_PREFIX + space;
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String space;
        String key;
        MatrixCursor cursor;
        switch (uriMatcher.match(uri)) {
            case CODE_ROOT:
                cursor = new MatrixCursor(new String[]{FN_NAME});
                List<String> tables = dbAdapter.getTables();
                for (String table: tables) {
                    cursor.addRow(new String[]{table});
                }
                return cursor;
            case CODE_SPACE:
                cursor = new MatrixCursor(new String[]{FN_KEY, FN_VALUE});
                space = uri.getPathSegments().get(0);
                List<KVBean> kvBeans = dbAdapter.queryAllKV(space);
                for (KVBean kvBean : kvBeans) {
                    cursor.addRow(new String[]{kvBean.getKey(), kvBean.getValue()});
                }
                return cursor;
            case CODE_ITEM:
                cursor = new MatrixCursor(new String[]{FN_KEY, FN_VALUE});
                space = uri.getPathSegments().get(0);
                key = uri.getPathSegments().get(1);
                String value = dbAdapter.getValue(space, key);
                if (value != null) {
                    cursor.addRow(new String[]{key, value});
                }
                return cursor;
        }
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String space;
        String key;
        String value;
        switch (uriMatcher.match(uri)) {
            case CODE_ROOT:
                // content://com.pk.datastore.data/
                break;
            case CODE_SPACE:
                // content://com.pk.datastore.data/{SPACE}
                space = uri.getPathSegments().get(0);
                dbAdapter.createTable(space);
                break;
            case CODE_ITEM:
                // content://com.pk.datastore.data/{SPACE}/{ITEM_KEY}
                space = uri.getPathSegments().get(0);
                key = uri.getPathSegments().get(1);
                value = values.getAsString(FN_VALUE);
                if (!dbAdapter.getTables().contains(value)) {
                    dbAdapter.createTable(space);
                }
                dbAdapter.insert(space, key, value);
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String space;
        String key;
        switch (uriMatcher.match(uri)) {
            case CODE_ROOT:
                break;
            case CODE_SPACE:
                space = uri.getPathSegments().get(0);
                dbAdapter.dropTable(space);
                break;
            case CODE_ITEM:
                space = uri.getPathSegments().get(0);
                key = uri.getPathSegments().get(1);
                dbAdapter.delete(space, key);
                break;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
        }
        return 0;
    }
}