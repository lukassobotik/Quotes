package com.sforge.quotes.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.HashMap;

/**
 * Abstract class for keeping clean and smooth references to each repository storages.
 * Each storage (entity collection) should have separate access.
 */
abstract class CrudRepository {

    DatabaseReference databaseReference;

    /**
     * Common method for adding object.
     * @param value Value to be stored.
     * @return Result of the process.
     */
    public Task<Void> add(Object value) {
        return databaseReference
                .push()
                .setValue(value);
    }
    /**
     * Method for adding an object with a specified key.
     * @param value Value to be stored.
     * @param key Specified key to store value under.
     * @return Result of the process.
     */
    public Task<Void> addWithKey(String key, Object value) {
        return databaseReference
                .child(key)
                .setValue(value);
    }

    /**
     * Common method for adding object.
     * @param key key (id) of entity to be updated.
     * @param values values to store.
     * @return Result of the process.
     */
    public Task<Void> update(String key, HashMap<String, Object> values) {
        return databaseReference
                .child(key)
                .updateChildren(values);
    }

    /**
     * Common method for adding object.
     * @param key entity to be removed.
     * @return Result of the process.
     */
    public Task<Void> remove(String key) {
        return databaseReference
                .child(key)
                .removeValue();
    }

    /**
     * Get all entities.
     * @return all stored entities.
     */
    public Query getAll() {
        return databaseReference.orderByKey();
    }

    /**
     * Return reference to Firebase storage of the instance.
     * @return reference to the database.
     */
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}
