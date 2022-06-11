package com.sforge.quotes.repository;

/**
 * Repository with all Quotes stored.
 */
public class QuoteRepository extends CrudRepository {

    private static final String STORAGE_NAME = "Quotes";

    public QuoteRepository() {
        databaseReference = FirebaseStorage.getInstance().getReference(STORAGE_NAME);
    }
}
