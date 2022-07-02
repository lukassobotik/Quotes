package com.sforge.quotes.repository;

/**
 * Class preparing instantiation of {@link com.google.firebase.database.FirebaseDatabase} so we do not create that instantiation again and again.
 * Consider missing `public` keyword which makes this class `package private` (Only classes from this package can
 * access it).
 */
public class FirebaseStorage {

    /**
     * Path tho the Firebase. It would be better to put it in configuration file. For example like here:
     * <a href="https://robosoft.medium.com/configuring-android-builds-a-step-by-step-guide-2e29e2e56487">...</a>
     */
    public static final String REPOSITORY_PATH = "https://quotes-30510-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final com.google.firebase.database.FirebaseDatabase
            firebase = com.google.firebase.database.FirebaseDatabase.getInstance(REPOSITORY_PATH);

    /**
     * Get instance of already connected {@link com.google.firebase.database.FirebaseDatabase}.
     *
     * @return instance of database
     */
    public static com.google.firebase.database.FirebaseDatabase getInstance() {
        return firebase;
    }
}
