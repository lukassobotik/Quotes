package com.sforge.quotes.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.sforge.quotes.R;
import com.sforge.quotes.activity.LoginActivity;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserQuoteRepository;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateQuotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateQuotesFragment extends Fragment {
    private String user;
    private final int quoteLengthLimit = 200;
    private final int authorLengthLimit = 50;
    private final int minQuoteLengthLimit = 5;
    private final int minAuthorLengthLimit = 2;

    private QuoteRepository quoteRepository;
    private EditText createQuoteEditText, createAuthorEditText;
    private Button createQuoteButton;
    private UserQuoteRepository userQuoteRepository;


    public CreateQuotesFragment() {
        // Required empty public constructor
    }

    public static CreateQuotesFragment newInstance() {
        return new CreateQuotesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean isUserLoggedIn = false;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            Toast.makeText(getActivity(), "Please Log In to Create Quotes.", Toast.LENGTH_SHORT).show();
        } else {
            isUserLoggedIn = true;
        }

        View fragmentView = inflater.inflate(R.layout.fragment_create_quotes, container, false);

        createQuoteEditText = fragmentView.findViewById(R.id.createQuoteEditText);
        createAuthorEditText = fragmentView.findViewById(R.id.createAuthorEditText);
        createQuoteButton = fragmentView.findViewById(R.id.createQuoteButton);

        if (isUserLoggedIn) {
            createQuoteButton.setOnClickListener(view -> submitQuote());

            quoteRepository = new QuoteRepository();

            user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            userQuoteRepository = new UserQuoteRepository(user);
        }

        return fragmentView;
    }

    private void submitQuote() {
        if (createQuoteEditText.getText().toString().length() <= quoteLengthLimit && createAuthorEditText.getText().toString().length() <= authorLengthLimit
                && createQuoteEditText.getText().toString().length() >= minQuoteLengthLimit && createAuthorEditText.getText().toString().length() >= minAuthorLengthLimit) {
            createQuote();
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

    public void createQuote(){
        Quote quote = new Quote(createQuoteEditText.getText().toString(), createAuthorEditText.getText().toString(), user);
        String key = userQuoteRepository.addWithKeyReturn(quote);
        quoteRepository.addWithKey(key, quote)
                .addOnSuccessListener(suc -> Toast.makeText(getActivity(), "Quote is Added Successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(er-> Toast.makeText(getActivity(), "Failed To Add the Quote", Toast.LENGTH_SHORT).show())
                .addOnCanceledListener(() -> Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show());
    }
}