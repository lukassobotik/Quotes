package com.sforge.quotes.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.BookmarksAdapter;
import com.sforge.quotes.adapter.QuoteAdapter;
import com.sforge.quotes.adapter.SearchAdapter;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.animation.FadeInItemAnimator;
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
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    private static final String ARG_LAST_SEARCH = "lastSearch";
    private static final String ARG_LOADED_QUOTES = "loadedQuotes";
    private static final String ARG_LOADED_NODE_ID = "loadedQueryPath";

    private String lastSearchParam;
    private List<Quote> loadedQuotesParam;
    private String loadedNodeIDParam;

    com.google.android.material.search.SearchBar searchBar;
    com.google.android.material.search.SearchView searchView;
    LinearLayout searchLayout;
    RecyclerView searchRV;


    // Discover
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
    FrameLayout collectionsBottomSheet;
    BottomSheetBehavior<FrameLayout> collectionsBottomSheetBehavior;

    int lastFirstVisiblePosition;
    int position = 0;

    private final int PREFETCH_DISTANCE = 10;
    private final int RANDOM_START_BOUND = 500;

    RecyclerView recyclerView, usrQuotesRV, collectionsList;

    public ExploreFragment() {
        // Required empty public constructor
    }

    public static ExploreFragment newInstance(String lastSearch, List<Quote> loadedQuotesParam, String loadedNodeIDParam) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LAST_SEARCH, lastSearch);
        args.putParcelableArrayList(ARG_LOADED_QUOTES, (ArrayList<? extends Parcelable>) loadedQuotesParam);
        args.putString(ARG_LOADED_NODE_ID, loadedNodeIDParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lastSearchParam = getArguments().getString(ARG_LAST_SEARCH);
            loadedQuotesParam = getArguments().getParcelableArrayList(ARG_LOADED_QUOTES);
            loadedNodeIDParam = getArguments().getString(ARG_LOADED_NODE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_explore, container, false);

        defineViews(fragmentView);
        changeStatusBarColor(true);

        if (lastSearchParam != null) {
            searchBar.setText(lastSearchParam);
            search(lastSearchParam);
        }

        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                search(charSequence.toString());
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        searchBar.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                search(searchBar.getText().toString());
            }
            return false;
        });

        // Discover
        loadDiscoverElements();

        return fragmentView;
    }

    private void changeStatusBarColor(boolean bright) {
        int resId = bright ? com.google.android.material.R.attr.colorSurfaceBright
                           : com.google.android.material.R.attr.colorSurface;
        Activity activity = getActivity();
        if (activity != null) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // Retrieve the color using TypedValue
            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(resId, typedValue, true);
            int color = typedValue.data;

            window.setStatusBarColor(color);
        }
    }

    private void loadDiscoverElements() {
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
    }

    public void expandSearch() {
        searchBar.performClick();
    }

    public void search(String query) {
        lastSearchParam = query;
        String type = "quote";
        if (query.length() > 2 && query.charAt(0) == 'a' && query.charAt(1) == ':') {
            type = "author";
            query = query.substring(2);
        } else if (query.length() > 2 && query.charAt(0) == 'q' && query.charAt(1) == ':') {
            query = query.substring(2);
        }

        if (query.equals("") || query.length() < 2) {
            return;
        }

        SearchAdapter searchAdapter = new SearchAdapter(getActivity());
        searchRV.setAdapter(searchAdapter);

        new QuoteRepository().getDatabaseReference().orderByChild(type).startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Quote> quotes = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Quote quote = data.getValue(Quote.class);

                    if (quote == null) {
                        break;
                    }

                    quotes.add(quote);
                }

                searchAdapter.setItems(new ArrayList<>(quotes));
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void defineViews(View view) {
        searchLayout = view.findViewById(R.id.searchLinearLayout);
        searchBar = view.findViewById(R.id.search_bar);
        searchView = view.findViewById(R.id.search_view);
        searchRV = view.findViewById(R.id.searchRecyclerView);
        LinearLayoutManager searchManager = new LinearLayoutManager(getActivity());
        searchRV.setLayoutManager(searchManager);

        // Discover
        recyclerView = view.findViewById(R.id.quoteRecyclerView);
        recyclerView.setItemAnimator(new FadeInItemAnimator());
        usrQuotesRV = view.findViewById(R.id.usrQuotes);
        collectionsList = view.findViewById(R.id.collectionsList);
        includeAccountProfile = view.findViewById(R.id.includeAccountProfile);
        mainActivityUsername = view.findViewById(R.id.username);
        swipeRefreshLayout = view.findViewById(R.id.mainSwipeRefreshLayout);
        quoteSwipeLayout = view.findViewById(R.id.quoteSwipeLayout);
        bookmarkButton = view.findViewById(R.id.quoteBookmarkButton);
        mainBackButton = view.findViewById(R.id.mainBackButton);
        mainBackButton.setVisibility(View.GONE);
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
        collectionsBottomSheet = view.findViewById(R.id.collection_bottom_sheet);
        collectionsBottomSheetBehavior = BottomSheetBehavior.from(collectionsBottomSheet);
        collectionsBottomSheetBehavior.setHideable(true);
        collectionsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
                collectionsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                areBookmarksOpen = true;
            } else {
                closeBookmarks();
            }
        });
    }

    public void closeBookmarks() {
        if (areBookmarksOpen) {
            collectionsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            areBookmarksOpen = false;
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
            nodeId = loadedNodeIDParam;
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

    @Override
    public void onStart() {
        super.onStart();
        SnapHelper snapHelper = new PagerSnapHelper();
        if (recyclerView.getOnFlingListener() == null) {
            snapHelper.attachToRecyclerView(recyclerView);
        }

        // Listens for scrolls, sets the info of the quote creator and sends the quote to the collections adapter to process it
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

        // Try to set the creator info on the first position
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

    @Override
    public void onStop() {
        super.onStop();
        changeStatusBarColor(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        changeStatusBarColor(false);
    }
}