package com.sforge.quotes.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private final int quoteLengthLimit = 999;
    private final int authorLengthLimit = 99;

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
        Button submitQuoteButton = findViewById(R.id.submitQuoteButton);

        quoteRepository = new QuoteRepository();

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userQuoteRepository = new UserQuoteRepository(user);

        submitQuoteButton.setOnClickListener(view -> {
            if (createQuoteEditText.getText().toString().length() <= quoteLengthLimit && createAuthorEditText.getText().toString().length() <= authorLengthLimit) {
                createQuote();
            } else if (createQuoteEditText.getText().toString().length() > quoteLengthLimit) {
                createQuoteEditText.setError("Quote Length Limit is 999 Letters!");
                createQuoteEditText.requestFocus();
            } else if (createAuthorEditText.getText().toString().length() > authorLengthLimit) {
                createAuthorEditText.setError("Author Length Limit is 99 Letters!");
                createAuthorEditText.requestFocus();
            }
        });
    }

    public void createQuote(){
        Quote quote = new Quote(createQuoteEditText.getText().toString(), createAuthorEditText.getText().toString(), user);
        userQuoteRepository.add(quote);
        quoteRepository.add(quote)
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