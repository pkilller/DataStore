# DataStore

A simple data store service，uses the sqlite + ContentProvider. 

## usage

**1. Inser a `record` into a `space`**
> `space` will be created automatically.
```bash
> content insert --uri content://com.pk.datastore.data/space_test/key_aaa --bind value:s:value_aaa
```

**2. Query all `space`**
```bash
> content query --uri content://com.pk.datastore.data/
---------------------------------------
Row: 0 name=space_test
```

**3. Query all `records` in the `space`**
```bash
> content query --uri content://com.pk.datastore.data/space_test
---------------------------------------
Row: 0 key=key_aaa, value=value_aaa
```

**4. Get a `value` by `key`**
```bash
> content query --uri content://com.pk.datastore.data/space_test/key_aaa
---------------------------------------
Row: 0 key=key_aaa, value=value_aaa
```

**5. Delete a `value` by `key`**
```bash
> content delete --uri content://com.pk.datastore.data/space_test/key_aaa
```

**6. Delete a `space`**
> Equivalent to deleting all `records`
```bash
> content delete --uri content://com.pk.datastore.data/space_test
```
