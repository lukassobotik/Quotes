package com.sforge.quotes.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.sforge.quotes.R;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateQuotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateQuotesFragment newInstance(String param1, String param2) {
        CreateQuotesFragment fragment = new CreateQuotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_create_quotes, container, false);

        createQuoteEditText = fragmentView.findViewById(R.id.createQuoteEditText);
        createAuthorEditText = fragmentView.findViewById(R.id.createAuthorEditText);
        createQuoteButton = fragmentView.findViewById(R.id.createQuoteButton);

        createQuoteButton.setOnClickListener(view -> {
            submitQuote();
        });

        quoteRepository = new QuoteRepository();

        user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userQuoteRepository = new UserQuoteRepository(user);

        return fragmentView;
    }

    private void submitQuote() {
        if (createQuoteEditText.getText().toString().length() <= quoteLengthLimit && createAuthorEditText.getText().toString().length() <= authorLengthLimit
                && createQuoteEditText.getText().toString().length() >= minQuoteLengthLimit && createAuthorEditText.getText().toString().length() >= minAuthorLengthLimit) {
            createQuote();
            // Todo: create finish() method
//            finish();
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