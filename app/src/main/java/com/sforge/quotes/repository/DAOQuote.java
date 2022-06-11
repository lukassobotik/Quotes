package com.sforge.quotes.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sforge.quotes.entity.Quote;

import java.util.HashMap;

public class DAOQuote {

    private final DatabaseReference databaseReference;
    public DAOQuote() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://quotes-30510-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference("Quotes");
    }

    public DAOQuote(String path) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://quotes-30510-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(path);
    }
    public Task<Void> add(Quote emp) {
        return databaseReference.push().setValue(emp);
    }
    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }
    public Query get(){
        return databaseReference.orderByKey();
    }
    public DatabaseReference getReference() {
        return databaseReference;
    }
}
