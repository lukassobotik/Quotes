package com.sforge.quotes.repository;

/**
 * Repository used to get a username of a certain user
 */
public class UsernameRepository extends CrudRepository{

    private static final String STORAGE_NAME = "Users/%s/username";

    public UsernameRepository(final String userID) {
        databaseReference = FirebaseStorage.getInstance().getReference(String.format(STORAGE_NAME, userID));
    }
}
