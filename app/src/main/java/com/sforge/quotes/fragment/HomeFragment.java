package com.sforge.quotes.fragment;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.BookmarksAdapter;
import com.sforge.quotes.entity.User;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String ARG_USERNAME = "username";
    private static final String ARG_GREETING = "greeting";
    private static final String ARG_USER_QUOTES = "userQuotes";
    private static final String ARG_RANDOM_QUOTE = "randomQuote";
    private static final String ARG_RANDOM_QUOTE_AUTHOR = "randomQuoteAuthor";


    private String usernameParam, greetingParam, randomQuoteParam, randomQuoteAuthorParam, userQuotesParam;

    RecyclerView pinnedCollections;
    BookmarksAdapter collectionsAdapter;
    TextView greetingsTextView, collectionsQuoteCount, quoteCount, randomQuote, randomQuoteAuthor;
    UserBookmarksRepository bookmarksRepository;
    UserQuoteRepository userQuoteRepository;
    Skeleton collectionsSkeleton;

    public int collectionQuoteCount = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String username, String greeting, String randomQuote, String randomQuoteAuthor, String userQuotes) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_GREETING, greeting);
        args.putString(ARG_USER_QUOTES, userQuotes);
        args.putString(ARG_RANDOM_QUOTE, randomQuote);
        args.putString(ARG_RANDOM_QUOTE_AUTHOR, randomQuoteAuthor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usernameParam = getArguments().getString(ARG_USERNAME);
            greetingParam = getArguments().getString(ARG_GREETING);
            userQuotesParam = getArguments().getString(ARG_USER_QUOTES);
            randomQuoteParam = getArguments().getString(ARG_RANDOM_QUOTE);
            randomQuoteAuthorParam = getArguments().getString(ARG_RANDOM_QUOTE_AUTHOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        defineViews(fragmentView);

        loadUsername();

        return fragmentView;
    }

    private void loadUsername() {
        if (usernameParam != null) {
            greetingsTextView.setText(getGreeting());
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            new UserRepository()
                    .getDatabaseReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        usernameParam = userProfile.getUsername();
                        greetingsTextView.setText(getGreeting());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    greetingsTextView.setText("Hello!");
                }
            });
        }
    }

    public String getRandomGreeting() {
        String[] allGreetings = {"Hello", "Hi", "Hey", "Greetings", "Good day", "How are you"};
        return allGreetings[(int) (Math.random() * allGreetings.length)];
    }

    public String getGreeting() {
        if (greetingParam != null) {
            return greetingParam;
        }

        String randomGreeting = getRandomGreeting();
        String greeting;
        if (randomGreeting.equals("How are you")) {
            greeting = randomGreeting + ", " + usernameParam + "?";
        } else {
            greeting = randomGreeting + ", " + usernameParam + "!";
        }

        greetingParam = greeting;
        return greeting;
    }

    private void loadCollections() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            bookmarksRepository = new UserBookmarksRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
            List<String> items = new ArrayList<>();
            bookmarksRepository.getAll().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    items.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String text = data.getKey();

                        int count = (int) data.getChildrenCount();
                        if (Boolean.TRUE.equals(data.child("favorite").getValue(Boolean.class))) {
                            items.add(text);
                            count -= 1;
                        }

                        // Remove the count of the empty quote
                        count -= 1;
                        collectionQuoteCount += count;
                    }
                    collectionsAdapter.setItems(items);
                    collectionsAdapter.notifyDataSetChanged();
                    pinnedCollections.setAdapter(collectionsAdapter);
                    collectionsQuoteCount.setText(String.valueOf(collectionQuoteCount));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Cannot Access the Database Right Now. " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadUserQuotes() {
        if (userQuotesParam != null) {
            quoteCount.setText(userQuotesParam);
        }

        if (randomQuoteParam != null && randomQuoteAuthorParam != null) {
            randomQuote.setText(randomQuoteParam);
            randomQuoteAuthor.setText(randomQuoteAuthorParam);
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userQuoteRepository = new UserQuoteRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userQuoteRepository.getAll().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = (int) snapshot.getChildrenCount();
                    int randomIndex = (int) (Math.random() * count);
                    int index = 0;
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (index == randomIndex) {
                            randomQuote.setText("\"" + data.child("quote").getValue(String.class) + "\"");
                            randomQuoteParam = randomQuote.getText().toString();
                            randomQuoteAuthor.setText("- " + data.child("author").getValue(String.class));
                            randomQuoteAuthorParam = randomQuoteAuthor.getText().toString();
                            break;
                        }
                        index++;
                    }
                    quoteCount.setText(String.valueOf(count));
                    userQuotesParam = String.valueOf(count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Cannot Access the Database Right Now. " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            randomQuote.setText("You have not logged in yet.");
            randomQuoteAuthor.setText("Please log in to see your quotes.");
            collectionsSkeleton.showOriginal();
        }
    }

    private void defineViews(View fragmentView) {
        greetingsTextView = fragmentView.findViewById(R.id.greetings_text);
        pinnedCollections = fragmentView.findViewById(R.id.pinned_collections_list);
        LinearLayoutManager collectionsManager = new LinearLayoutManager(getActivity());
        pinnedCollections.setLayoutManager(collectionsManager);
        collectionsAdapter = new BookmarksAdapter(getActivity());
        collectionsQuoteCount = fragmentView.findViewById(R.id.collection_quote_count);
        quoteCount = fragmentView.findViewById(R.id.quote_count);
        randomQuote = fragmentView.findViewById(R.id.random_quote);
        randomQuoteAuthor = fragmentView.findViewById(R.id.random_quote_author);

        createFavoriteCollectionsSkeleton();
        loadCollections();
        loadUserQuotes();
    }

    private void createFavoriteCollectionsSkeleton() {
        collectionsSkeleton = SkeletonLayoutUtils.applySkeleton(pinnedCollections, R.layout.collection_item, 2);
        collectionsSkeleton.showSkeleton();
        collectionsSkeleton.setMaskColor(0);
        collectionsSkeleton.setMaskCornerRadius(50f);
    }

    @Override
    public void onPause() {
        super.onPause();
        collectionQuoteCount = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
        collectionQuoteCount = 0;
    }
}