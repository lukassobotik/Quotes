package com.sforge.quotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.apachat.swipereveallayout.core.SwipeLayout;
import com.apachat.swipereveallayout.core.interfaces.Swipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.sforge.quotes.dialog.CollectionsDialog;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.repository.UsernameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CollectionsDialog.CollectionsDialogListener {

    //Firebase Related
    boolean isLoggedIn = false;
    boolean areBookmarksOpen = false;
    boolean profileIsOpen = false;
    QuoteRepository quoteRepository;
    UserBookmarksRepository bookmarksRepository;
    QuoteAdapter quoteAdapter;

    //Account Profile Related
    LinearLayout includeAccountProfile;
    TextView mainActivityUsername;

    //UI Related
    Button createQuote, profileButton, showUserProfileButton, profileLogoutButton, profileLoginButton, mainBackButton, bookmarkButton, profileCollectionsButton, searchButton, shareButton;
    UserQuoteAdapter usrAdapter;
    BookmarksAdapter collectionsAdapter;
    ConstraintLayout quoteItemBackground;
    SwipeLayout quoteSwipeLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout addToBookmarksLayout;
    BottomNavigationView bottomNavigationView;

    int lastFirstVisiblePosition;
    int position = 0;

    private final int PREFETCH_DISTANCE = 10;
    private final int RANDOM_START_BOUND = 500;

    RecyclerView recyclerView, usrQuotesRV, collectionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defineViews();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            isLoggedIn = true;
        }

        if (isLoggedIn) {
            profileLoginButton.setVisibility(View.GONE);
        }

        createQuotesButton();

        profileMenuClickListeners();

        createMainBackButton();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(this, MainActivity.class));
                    break;
                case R.id.navigation_search:
                    startActivity(new Intent(this, SearchActivity.class));
                    break;
                case R.id.navigation_create:
                    startActivity(new Intent(this, CreateQuotes.class));
                    break;
                case R.id.navigation_collections:
                    startActivity(new Intent(this, CollectionsActivity.class));
                    break;
                case R.id.navigation_account:
                    startActivity(new Intent(this, UserProfile.class));
                    break;
            }
            return true;
        });

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

        swipeRefreshLayout.setOnRefreshListener(() -> {
            usrAdapter.setItems(new ArrayList<>());
            quoteAdapter = new QuoteAdapter(this);
            loadQuotes(null);
            recyclerView.setAdapter(quoteAdapter);
            swipeRefreshLayout.setRefreshing(false);
        });

        createFirebaseAdapters();

        createBookmarkButton();

        searchButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));

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
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_top_to_bottom));
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
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_bottom_to_top));
        }
    }

    public void createFirebaseAdapters() {
        //CollectionsAdapter
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
                    Toast.makeText(MainActivity.this, "Cannot Access the Database Right Now. " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }

        //QuoteAdapter
        quoteAdapter = new QuoteAdapter(this);
        loadQuotes(null);
        recyclerView.setAdapter(quoteAdapter);
    }

    public void loadQuotes(String nodeId) {
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
                                    quoteAdapter.notifyDataSetChanged();

                                    //Load the first quote creator info
                                    if (position == 0) {
                                        setCurrentQuoteCreatorInfo(0);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        iteration++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    quoteAdapter.notifyDataSetChanged();

                    //Load the first quote creator info
                    if (position == 0) {
                        setCurrentQuoteCreatorInfo(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createMainBackButton() {
        mainBackButton.setOnClickListener(view -> {
            quoteSwipeLayout.close(true);
        });
    }

    //Quote Button Logic
    public void createQuotesButton() {
        if (isLoggedIn) {
            createQuote.setOnClickListener(view -> {
                Intent i = new Intent(MainActivity.this, CreateQuotes.class);
                startActivity(i);
            });
        } else {
            createQuote.setOnClickListener(view -> {
                Toast.makeText(this, "Please Log in to Create Quotes", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            });
        }
    }

    public void profileMenuClickListeners() {
        profileButton.setOnClickListener(view -> profileIconOnClickEvent());

        profileLoginButton.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        showUserProfileButton.setOnClickListener(view -> startActivity(new Intent(this, UserProfile.class)));

        profileLogoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            profileIconOnClickEvent();
            isLoggedIn = false;
            createQuotesButton();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        profileCollectionsButton.setOnClickListener(view -> startActivity(new Intent(this, CollectionsActivity.class)));
    }

    //Profile Button On Click Logic
    public void profileIconOnClickEvent() {
        if (!profileIsOpen) {
            if (isLoggedIn) {
                showUserProfileButton.setVisibility(View.VISIBLE);
                profileLogoutButton.setVisibility(View.VISIBLE);
                profileCollectionsButton.setVisibility(View.VISIBLE);
            } else {
                profileLoginButton.setVisibility(View.VISIBLE);
            }
            showUserProfileButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_top_to_bottom));
            profileLogoutButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_top_to_bottom));
            profileLoginButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_top_to_bottom));
            profileCollectionsButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_top_to_bottom));
            profileIsOpen = true;
        } else {
            showUserProfileButton.setVisibility(View.GONE);
            showUserProfileButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_bottom_to_top));
            profileLogoutButton.setVisibility(View.GONE);
            profileLogoutButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_bottom_to_top));
            profileLoginButton.setVisibility(View.GONE);
            profileLoginButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_bottom_to_top));
            profileCollectionsButton.setVisibility(View.GONE);
            profileCollectionsButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_bottom_to_top));
            profileIsOpen = false;
        }
        closeBookmarks();
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
                Toast.makeText(MainActivity.this, "Cannot Access the Database Right Now. " + error, Toast.LENGTH_SHORT).show();
            }
        });
        loadUserQuotes(null, swipeUID, true);
    }

    @Override
    protected void onStart() {
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

    @Override
    protected void onPause() {
        super.onPause();
        //Store the Recyclerview scroll position
        lastFirstVisiblePosition =
                ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Restore the last stored Recyclerview scroll position
        recyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
        if (quoteAdapter.getItemCount() != 0) {
            setCurrentQuoteCreatorInfo(lastFirstVisiblePosition);
            collectionsAdapter.setQuote(quoteAdapter.getQuoteFromPosition(lastFirstVisiblePosition));
        }

        //#5 Issue Fix
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !isLoggedIn) {
            finish();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
    }

    public void defineViews() {
        createQuote = findViewById(R.id.createQuote);
        recyclerView = findViewById(R.id.quoteRecyclerView);
        usrQuotesRV = findViewById(R.id.usrQuotes);
        collectionsList = findViewById(R.id.collectionsList);
        addToBookmarksLayout = findViewById(R.id.addToBookmarksRVLinearLayout);
        profileButton = findViewById(R.id.userProfileButton);
        showUserProfileButton = findViewById(R.id.profileShowProfile);
        profileLogoutButton = findViewById(R.id.profileLogout);
        profileLoginButton = findViewById(R.id.profileLogin);
        includeAccountProfile = findViewById(R.id.includeAccountProfile);
        mainActivityUsername = findViewById(R.id.username);
        swipeRefreshLayout = findViewById(R.id.mainSwipeRefreshLayout);
        quoteSwipeLayout = findViewById(R.id.quoteSwipeLayout);
        bookmarkButton = findViewById(R.id.quoteBookmarkButton);
        mainBackButton = findViewById(R.id.mainBackButton);
        profileCollectionsButton = findViewById(R.id.profileCollections);
        mainBackButton.setVisibility(View.VISIBLE);
        searchButton = findViewById(R.id.searchButton);
        shareButton = findViewById(R.id.shareButton);
        quoteItemBackground = findViewById(R.id.quoteItemBackground);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        LinearLayoutManager usrManager = new LinearLayoutManager(this);
        LinearLayoutManager collectionsManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        usrQuotesRV.setLayoutManager(usrManager);
        collectionsList.setLayoutManager(collectionsManager);
        usrAdapter = new UserQuoteAdapter(this);
        collectionsAdapter = new BookmarksAdapter(this);
        usrQuotesRV.setAdapter(usrAdapter);
        quoteRepository = new QuoteRepository();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    @Override
    public void getData(int option) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}