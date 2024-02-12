package com.sforge.quotes.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.sforge.quotes.R;

import java.util.Objects;

public class CreateQuotes extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quotes);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_quotes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create_quotes_menu_done) {

        }

        return super.onOptionsItemSelected(item);
    }

    public void createActionBar(){
        int nightModeFlags = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                //Set the Action Bar color to Dark Gray
                Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dark_action_bar, null));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }
    }
}