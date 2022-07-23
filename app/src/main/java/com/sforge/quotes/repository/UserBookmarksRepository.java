package com.sforge.quotes.repository;

/**
 * Repository with All Bookmarks the user bookmarked
 */
public class UserBookmarksRepository extends CrudRepository{

    private static final String STORAGE_NAME = "Users/%s/Bookmarks";

    public UserBookmarksRepository(final String userID) {
        databaseReference = FirebaseStorage.getInstance().getReference(String.format(STORAGE_NAME, userID));
    }
}
