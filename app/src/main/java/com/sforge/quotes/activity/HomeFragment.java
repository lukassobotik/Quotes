package com.sforge.quotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.apachat.swipereveallayout.core.SwipeLayout;
import com.apachat.swipereveallayout.core.interfaces.Swipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.BookmarksAdapter;
import com.sforge.quotes.adapter.QuoteAdapter;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.repository.UsernameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_LOADED_QUOTES = "loadedQuotes";
    private static final String ARG_LOADED_NODE_ID = "loadedQueryPath";

    private List<Quote> loadedQuotesParam;
    private String loadedNodeIDParam;

    // Firebase Related
    boolean isLoggedIn = false;
    boolean areBookmarksOpen = false;
    boolean profileIsOpen = false;
    QuoteRepository quoteRepository;
    UserBookmarksRepository bookmarksRepository;
    QuoteAdapter quoteAdapter;

    // Account Profile Related
    LinearLayout includeAccountProfile;
    TextView mainActivityUsername;

    // UI Related
    Button mainBackButton, bookmarkButton, shareButton;
    UserQuoteAdapter usrAdapter;
    BookmarksAdapter collectionsAdapter;
    ConstraintLayout quoteItemBackground;
    SwipeLayout quoteSwipeLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout addToBookmarksLayout;

    int lastFirstVisiblePosition;
    int position = 0;

    private final int PREFETCH_DISTANCE = 10;
    private final int RANDOM_START_BOUND = 500;

    RecyclerView recyclerView, usrQuotesRV, collectionsList;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(List<Quote> loadedQuotesParam, String loadedNodeIDParam) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LOADED_QUOTES, (ArrayList<? extends Parcelable>) loadedQuotesParam);
        args.putString(ARG_LOADED_NODE_ID, loadedNodeIDParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loadedQuotesParam = getArguments().getParcelableArrayList(ARG_LOADED_QUOTES);
            loadedNodeIDParam = getArguments().getString(ARG_LOADED_NODE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        defineViews(fragmentView);

        if (loadedQuotesParam == null) {
            loadedQuotesParam = new ArrayList<>();
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            isLoggedIn = true;
        }

        createMainBackButton();

        quoteSwipeLayout.setSwipeListener(new Swipe() {
            @Override
            public void onClosed(SwipeLayout view) {
                recyclerView.suppressLayout(false);
                usrQuotesRV.suppressLayout(false);
                swipeRefreshLayout.setEnabled(position == 0);
            }

            @Override
            public void onOpened(SwipeLayout view) {
                recyclerView.suppressLayout(false);
                usrQuotesRV.suppressLayout(false);
                swipeRefreshLayout.setEnabled(false);
            }

            @Override
            public void onSlide(SwipeLayout view, float slideOffset) {
                recyclerView.suppressLayout(true);
                usrQuotesRV.suppressLayout(true);
                swipeRefreshLayout.setEnabled(position == 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        createFirebaseAdapters();

        createBookmarkButton();

        shareButton.setOnClickListener(view -> {
            StringBuilder stringBuilder = new StringBuilder();
            Quote quote = quoteAdapter.getQuoteFromPosition(position);
            stringBuilder.append(quote.getQuote()).append(" - By ").append(quote.getAuthor());

            Intent myIntent = new Intent (Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
            startActivity(Intent.createChooser(myIntent, "Share using"));
        });

        return fragmentView;
    }

    public void refresh() {
        loadedQuotesParam.clear();
        loadedNodeIDParam = null;
        usrAdapter.setItems(new ArrayList<>());
        quoteAdapter = new QuoteAdapter(getContext());
        loadQuotes(null);
        recyclerView.setAdapter(quoteAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void createBookmarkButton() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            bookmarkButton.setVisibility(View.GONE);
            return;
        }

        bookmarksRepository = new UserBookmarksRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        bookmarkButton.setOnClickListener(view -> {

            if (position == 0 && !areBookmarksOpen) {
                collectionsAdapter.setQuote(quoteAdapter.getQuoteFromPosition(0));
            }

            if (!areBookmarksOpen) {
                addToBookmarksLayout.setVisibility(View.VISIBLE);
                areBookmarksOpen = true;
                addToBookmarksLayout.startAnimation(
                        AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                                                     R.anim.profile_button_slide_top_to_bottom));
            } else {
                closeBookmarks();
            }
        });
    }

    public void closeBookmarks() {
        if (areBookmarksOpen) {
            addToBookmarksLayout.setVisibility(View.GONE);
            areBookmarksOpen = false;
            addToBookmarksLayout.startAnimation(
                    AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                                                 R.anim.profile_button_slide_bottom_to_top));
        }
    }

    public void createFirebaseAdapters() {
        // CollectionsAdapter
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            bookmarksRepository = new UserBookmarksRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
            List<String> items = new ArrayList<>();
            bookmarksRepository.getAll().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    items.clear();
                    closeBookmarks();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String text = data.getKey();
                        items.add(text);
                    }
                    collectionsAdapter.setItems(items);
                    collectionsAdapter.notifyDataSetChanged();
                    collectionsList.setAdapter(collectionsAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Cannot Access the Database Right Now. " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }

        // QuoteAdapter
        quoteAdapter = new QuoteAdapter(getContext());
        loadQuotes(null);
        recyclerView.setAdapter(quoteAdapter);
    }

    public void loadQuotes(String nodeId) {
        loadedNodeIDParam = nodeId;

        if (loadedNodeIDParam != null) {
            loadQuotes(loadedNodeIDParam);
            return;
        }

        if (!loadedQuotesParam.isEmpty()) {
            quoteAdapter.setItems(new ArrayList<>(loadedQuotesParam));
            quoteAdapter.notifyDataSetChanged();
        }

        Query query = null;

        if (nodeId == null) {
            quoteRepository.getDatabaseReference().limitToFirst(RANDOM_START_BOUND).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i = new Random().nextInt(RANDOM_START_BOUND);
                    int iteration = 0;
                    Query query1;

                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (iteration == i) {
                            query1 = quoteRepository.getDatabaseReference()
                                    .orderByKey()
                                    .startAfter(data.getKey())
                                    .limitToFirst(PREFETCH_DISTANCE);

                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    List<Quote> quotes = new ArrayList<>();
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Quote quote = data.getValue(Quote.class);
                                        if (quote != null) {
                                            Quote quoteWithKey = new Quote(quote.getQuote(), quote.getAuthor(), quote.getUser(), data.getKey());
                                            quotes.add(quoteWithKey);
                                        }
                                    }

                                    quoteAdapter.addItems(quotes);
                                    loadedQuotesParam.addAll(quotes);
                                    quoteAdapter.notifyDataSetChanged();

                                    // Load the first quote creator info
                                    if (position == 0) {
                                        setCurrentQuoteCreatorInfo(0);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        iteration++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (nodeId != null) {
            query = quoteRepository.getDatabaseReference()
                    .orderByKey()
                    .startAfter(nodeId)
                    .limitToFirst(PREFETCH_DISTANCE);
        }

        if (query != null) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Quote> quotes = new ArrayList<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Quote quote = data.getValue(Quote.class);
                        if (quote != null) {
                            Quote quoteWithKey = new Quote(quote.getQuote(), quote.getAuthor(), quote.getUser(), data.getKey());
                            quotes.add(quoteWithKey);
                        }
                    }

                    quoteAdapter.addItems(quotes);
                    loadedQuotesParam.addAll(quotes);
                    quoteAdapter.notifyDataSetChanged();

                    // Load the first quote creator info
                    if (position == 0) {
                        setCurrentQuoteCreatorInfo(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void loadUserQuotes(String nodeId, String userId, boolean deleteOldQuotes) {
        Query query;

        if (nodeId == null) {
            query = new UserQuoteRepository(userId)
                    .getDatabaseReference()
                    .orderByKey()
                    .limitToFirst(PREFETCH_DISTANCE);
        } else {
            query = new UserQuoteRepository(userId)
                    .getDatabaseReference()
                    .orderByKey()
                    .startAfter(nodeId)
                    .limitToFirst(PREFETCH_DISTANCE);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Quote> quotes = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Quote quote = data.getValue(Quote.class);
                    if (quote != null) {
                        Quote quoteWithKey = new Quote(quote.getQuote(), quote.getAuthor(), quote.getUser(), data.getKey());
                        quotes.add(quoteWithKey);
                    }
                }

                if (deleteOldQuotes) {
                    usrAdapter.setItems(new ArrayList<>(quotes));
                    usrAdapter.notifyDataSetChanged();
                } else {
                    usrAdapter.addItems(new ArrayList<>(quotes));
                    usrAdapter.notifyItemRangeInserted(usrAdapter.getItemCount(), quotes.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createMainBackButton() {
        mainBackButton.setOnClickListener(view -> {
            quoteSwipeLayout.close(true);
        });
    }

    public void setCurrentQuoteCreatorInfo(int position) {
        String swipeUID = quoteAdapter.getCreatorAccountFromPosition(position);
        new UsernameRepository(swipeUID).getAll().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userProfile = snapshot.getValue(String.class);
                if (userProfile != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("@").append(userProfile);

                    mainActivityUsername.setText(stringBuilder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Cannot Access the Database Right Now. " + error,
                               Toast.LENGTH_SHORT).show();
            }
        });
        loadUserQuotes(null, swipeUID, true);
    }

    public void defineViews(View view) {
        recyclerView = view.findViewById(R.id.quoteRecyclerView);
        usrQuotesRV = view.findViewById(R.id.usrQuotes);
        collectionsList = view.findViewById(R.id.collectionsList);
        addToBookmarksLayout = view.findViewById(R.id.addToBookmarksRVLinearLayout);
        includeAccountProfile = view.findViewById(R.id.includeAccountProfile);
        mainActivityUsername = view.findViewById(R.id.username);
        swipeRefreshLayout = view.findViewById(R.id.mainSwipeRefreshLayout);
        quoteSwipeLayout = view.findViewById(R.id.quoteSwipeLayout);
        bookmarkButton = view.findViewById(R.id.quoteBookmarkButton);
        mainBackButton = view.findViewById(R.id.mainBackButton);
        mainBackButton.setVisibility(View.VISIBLE);
        shareButton = view.findViewById(R.id.shareButton);
        quoteItemBackground = view.findViewById(R.id.quoteItemBackground);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        LinearLayoutManager usrManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager collectionsManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        usrQuotesRV.setLayoutManager(usrManager);
        collectionsList.setLayoutManager(collectionsManager);
        usrAdapter = new UserQuoteAdapter(getActivity());
        collectionsAdapter = new BookmarksAdapter(getActivity());
        usrQuotesRV.setAdapter(usrAdapter);
        quoteRepository = new QuoteRepository();
    }

    @Override
    public void onStart() {
        super.onStart();
        SnapHelper snapHelper = new PagerSnapHelper();
        if (recyclerView.getOnFlingListener() == null) {
            snapHelper.attachToRecyclerView(recyclerView);
        }
        //Listens for scrolls, sets the info of the quote creator and sends the quote to the collections adapter to process it
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                position = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && position != -1) {
                    setCurrentQuoteCreatorInfo(position);
                    collectionsAdapter.setQuote(quoteAdapter.getQuoteFromPosition(position));
                    quoteSwipeLayout.setLockDrag(true);
                    closeBookmarks();
                }
                swipeRefreshLayout.setEnabled(position == 0);
                quoteSwipeLayout.setLockDrag(false);

                if (!recyclerView.canScrollVertically(1)) {
                    loadQuotes(quoteAdapter.getLastItemId());
                }
            }
        });
        //try to set the creator info on the first position
        try {
            setCurrentQuoteCreatorInfo(0);
            collectionsAdapter.setQuote(quoteAdapter.getQuoteFromPosition(0));
        } catch (IndexOutOfBoundsException e) {
            e.getCause();
        }

        usrQuotesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    loadUserQuotes(usrAdapter.getLastItemId(), quoteAdapter.getCreatorAccountFromPosition(position), false);
                }
            }
        });
    }
}