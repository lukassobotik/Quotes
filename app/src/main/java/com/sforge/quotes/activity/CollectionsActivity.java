package com.sforge.quotes.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.sforge.quotes.R;

public class CollectionsActivity extends AppCompatActivity {

    /**
     * Temporary activity that redirects to the main activity with the arguments to view collections.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirect to the main activity with the arguments to view collections.
        startActivity(new Intent(this, MainActivity.class).putExtra("collections", true));
    }
}