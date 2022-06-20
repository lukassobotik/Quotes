package com.sforge.quotes.repository;

/**
 * Repository with all Users stored.
 */
public class UserRepository extends CrudRepository {

    private static final String STORAGE_NAME = "Users";

    public UserRepository() {
        databaseReference = FirebaseStorage.getInstance().getReference(STORAGE_NAME);
    }
}
