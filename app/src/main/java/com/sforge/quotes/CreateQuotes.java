package com.sforge.quotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class CreateQuotes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quotes);

        createActionBar();

        final EditText createQuoteEditText = findViewById(R.id.createQuoteEditText);
        final EditText createAuthorEditText = findViewById(R.id.createAuthorEditText);
        Button submitQuoteButton = findViewById(R.id.submitQuoteButton);
        DAOQuote dao = new DAOQuote();

        submitQuoteButton.setOnClickListener(view -> {
            Quote quote = new Quote(createQuoteEditText.getText().toString(), createAuthorEditText.getText().toString());
            dao.add(quote).addOnSuccessListener(suc -> {
                Toast.makeText(this, "Quote is Added Successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(er->{
                Toast.makeText(this, "Failed To Add the Quote", Toast.LENGTH_SHORT).show();
            }).addOnCanceledListener(() -> {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            });
        });
    }

    public void createActionBar(){
        String localNightMode = "undefined";
        int nightModeFlags = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                localNightMode = "night";
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                localNightMode = "light";
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                localNightMode = "undefined";
                break;
        }

        if(localNightMode.equals("night")){
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dark_action_bar, null));
        }
        Log.d("createActionBar", localNightMode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(CreateQuotes.this, MainActivity.class);
        startActivity(intent);
    }
    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel?");
        builder.setMessage("Are you sure you want to exit without saving?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            finish();
            Intent intent = new Intent(CreateQuotes.this, MainActivity.class);
            startActivity(intent);
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.create().show();
    }
}