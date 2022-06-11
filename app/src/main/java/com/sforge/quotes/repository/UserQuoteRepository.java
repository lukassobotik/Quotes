package com.sforge.quotes.repository;

/**
 * Repository with all Quotes related to User.
 */
public class UserQuoteRepository extends CrudRepository {

    private static final String STORAGE_NAME = "Users/%s/User Quotes";

    public UserQuoteRepository(final String userID) {
        databaseReference = FirebaseStorage.getInstance().getReference(String.format(STORAGE_NAME, userID));
    }
}
