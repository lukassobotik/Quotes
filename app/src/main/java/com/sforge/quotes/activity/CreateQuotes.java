package com.sforge.quotes.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.sforge.quotes.R;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserQuoteRepository;

import java.util.Objects;

public class CreateQuotes extends AppCompatActivity {

    private String user;
    private final int quoteLengthLimit = 200;
    private final int authorLengthLimit = 50;
    private final int minQuoteLengthLimit = 5;
    private final int minAuthorLengthLimit = 2;

    private QuoteRepository quoteRepository;
    private EditText createQuoteEditText, createAuthorEditText;
    private UserQuoteRepository userQuoteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quotes);

        createActionBar();

        createQuoteEditText = findViewById(R.id.createQuoteEditText);
        createAuthorEditText = findViewById(R.id.createAuthorEditText);

        quoteRepository = new QuoteRepository();

        user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userQuoteRepository = new UserQuoteRepository(user);
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
            if (createQuoteEditText.getText().toString().length() <= quoteLengthLimit && createAuthorEditText.getText().toString().length() <= authorLengthLimit
                    && createQuoteEditText.getText().toString().length() >= minQuoteLengthLimit && createAuthorEditText.getText().toString().length() >= minAuthorLengthLimit) {
                createQuote();
                finish();
            } else if (createQuoteEditText.getText().toString().length() > quoteLengthLimit) {
                createQuoteEditText.setError("Quote Length Limit is " + quoteLengthLimit + " Letters!");
                createQuoteEditText.requestFocus();
            } else if (createAuthorEditText.getText().toString().length() > authorLengthLimit) {
                createAuthorEditText.setError("Author Length Limit is " + authorLengthLimit + " Letters!");
                createAuthorEditText.requestFocus();
            } else if (createQuoteEditText.getText().toString().length() < minQuoteLengthLimit) {
                createQuoteEditText.setError("Quote Must Have at Least " + minQuoteLengthLimit + " Letters!");
                createQuoteEditText.requestFocus();
            } else if (createAuthorEditText.getText().toString().length() < minAuthorLengthLimit) {
                createAuthorEditText.setError("Author's Name Must Have At Least " + minAuthorLengthLimit + " Letters!");
                createAuthorEditText.requestFocus();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void createQuote(){
        Quote quote = new Quote(createQuoteEditText.getText().toString(), createAuthorEditText.getText().toString(), user);
        String key = userQuoteRepository.addWithKeyReturn(quote);
        quoteRepository.addWithKey(key, quote)
                .addOnSuccessListener(suc -> Toast.makeText(this, "Quote is Added Successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(er-> Toast.makeText(this, "Failed To Add the Quote", Toast.LENGTH_SHORT).show())
                .addOnCanceledListener(() -> Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show());
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