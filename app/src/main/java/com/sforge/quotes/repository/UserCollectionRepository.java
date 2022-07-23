package com.sforge.quotes.repository;

import com.sforge.quotes.entity.Quote;

public class UserCollectionRepository extends CrudRepository{

    private static final String STORAGE_NAME = "Users/%s/Bookmarks/%s";

    public UserCollectionRepository(final String userID, final String collection) {
        databaseReference = FirebaseStorage.getInstance().getReference(String.format(STORAGE_NAME, userID, collection));
    }
}

