package com.pk.datastore.sample;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestSample {
    private static final String TAG = "TestSample";
    private Context context;
    private DataStoreManager dsm;
    public TestSample(Context _context) {
        context = _context;
        dsm = new DataStoreManager(context);
    }

    public void doTest() {
        List<String> keys = new ArrayList<>();
        String testSpace= "space_test";

        Log.d(TAG, "[Insert 2 spaces] & [Query all spaces] ");
        dsm.insertSpace("space_test_1");
        dsm.insertSpace("space_test_2");
        for (String space : dsm.getAllSpace()) {
            Log.d(TAG, "    " + space);
        }

        Log.d(TAG, "[Delete 2 spaces]");
        dsm.deleteSpace("space_test_1");
        dsm.deleteSpace("space_test_2");
        for (String space : dsm.getAllSpace()) {
            Log.d(TAG, "    " + space);
        }

        Log.d(TAG, "[Insert 10 keys&values] & [Query all keys&values]");
        for (int i = 0; i < 10; i++) {
            dsm.insertKV(testSpace, "key_" + i, "value_" + i);
            keys.add("key_" + i);
        }
        HashMap<String, String> kvs = dsm.getAllKV(testSpace);
        for (String key : kvs.keySet()) {
            Log.d(TAG, "    " + key + " : " + kvs.get(key));
        }
        Log.d(TAG, "[Delete 5 keys&values]");
        for (int i = 0; i < 5; i++) {
            dsm.deleteKV(testSpace, keys.get(i));
        }
        kvs = dsm.getAllKV(testSpace);
        for (String key : kvs.keySet()) {
            Log.d(TAG, "    " + key + " : " + kvs.get(key));
        }


    }
}
