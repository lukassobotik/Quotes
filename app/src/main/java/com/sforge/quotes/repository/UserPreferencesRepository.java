package com.sforge.quotes.repository;

/**
 * Repository with All Preferences related to a user
 */
public class UserPreferencesRepository extends CrudRepository{

    private static final String STORAGE_NAME = "Users/%s/User Preferences";

    public UserPreferencesRepository(final String userID) {
        databaseReference = FirebaseStorage.getInstance().getReference(String.format(STORAGE_NAME, userID));
    }
}
