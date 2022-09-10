package com.sforge.quotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.apachat.swipereveallayout.core.SwipeLayout;
import com.apachat.swipereveallayout.core.interfaces.Swipe;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.BookmarksAdapter;
import com.sforge.quotes.adapter.FirebaseAdapter;
import com.sforge.quotes.adapter.SearchAdapter;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.repository.UsernameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Firebase Related
    boolean isLoggedIn = false;
    boolean areBookmarksOpen = false;
    boolean profileIsOpen = false;
    QuoteRepository quoteRepository;
    UserBookmarksRepository bookmarksRepository;
    FirebaseAdapter firebaseAdapter;
    SearchAdapter searchAdapter;

    //Account Profile Related
    LinearLayout includeAccountProfile;
    TextView mainActivityUsername;
    List<Quote> usrQuotes;

    //UI Related
    Button createQuote, profileButton, showUserProfileButton, profileLogoutButton, profileLoginButton, mainBackButton, bookmarkButton, profileCollectionsButton;
    UserQuoteAdapter usrAdapter;
    BookmarksAdapter collectionsAdapter;
    ConstraintLayout quoteItemBackground;
    SwipeLayout quoteSwipeLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout addToBookmarksLayout, searchLayout;
    SearchView searchView;

    int lastFirstVisiblePosition;
    int position = 0;

    RecyclerView recyclerView, usrQuotesRV, collectionsList, searchRV;

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

        quoteSwipeLayout.setSwipeListener(new Swipe() {
            @Override
            public void onClosed(SwipeLayout view) {
                recyclerView.suppressLayout(false);
                usrQuotesRV.suppressLayout(false);
                swipeRefreshLayout.setEnabled(true);
            }

            @Override
            public void onOpened(SwipeLayout view) {
                recyclerView.suppressLayout(false);
                usrQuotesRV.suppressLayout(false);
                swipeRefreshLayout.setEnabled(false);

                //precaution if the creator on the first position doesn't load
                if (position == 0) {
                    setCurrentQuoteCreatorInfo(0);
                }
            }

            @Override
            public void onSlide(SwipeLayout view, float slideOffset) {
                recyclerView.suppressLayout(true);
                usrQuotesRV.suppressLayout(true);
                swipeRefreshLayout.setEnabled(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            firebaseAdapter.refresh();
            firebaseAdapter.shuffleList();
            swipeRefreshLayout.setRefreshing(false);
        });

        createFirebaseAdapters();

        createBookmarkButton();

        searchView.setOnQueryTextFocusChangeListener((view, b) -> {
            ViewGroup.LayoutParams layoutParams = searchView.getLayoutParams();
            if (b) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                searchLayout.setVisibility(View.VISIBLE);
            } else {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                searchLayout.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });
    }

    public void search(String query) {
        Query searchQuery = quoteRepository.getDatabaseReference().orderByChild("quote").startAt(query).endAt(query + "~");
        FirebaseRecyclerOptions<Quote> searchOptions = new FirebaseRecyclerOptions.Builder<Quote>()
                .setQuery(searchQuery, Quote.class)
                .build();
        searchAdapter = new SearchAdapter(this, searchOptions);
        searchRV.setAdapter(searchAdapter);
        searchAdapter.startListening();
    }

    private void createBookmarkButton() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            bookmarkButton.setVisibility(View.GONE);
            return;
        }

        bookmarksRepository = new UserBookmarksRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        bookmarkButton.setOnClickListener(view -> {

            if (position == 0 && !areBookmarksOpen) {
                collectionsAdapter.setQuote(firebaseAdapter.getQuoteFromPosition(0));
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
        //FirebaseAdapter
        Query quoteQuery = quoteRepository.getDatabaseReference();
        PagingConfig quotePagingConfig = new PagingConfig(1, 1, false);
        DatabasePagingOptions<Quote> quoteOptions = new DatabasePagingOptions.Builder<Quote>()
                .setLifecycleOwner(this)
                .setQuery(quoteQuery, quotePagingConfig, Quote.class)
                .build();
        firebaseAdapter = new FirebaseAdapter(this, quoteOptions, true, recyclerView);
        recyclerView.setAdapter(firebaseAdapter);
        firebaseAdapter.startListening();

        Query searchQuery = quoteRepository.getDatabaseReference();
        FirebaseRecyclerOptions<Quote> searchOptions = new FirebaseRecyclerOptions.Builder<Quote>()
                .setQuery(searchQuery, Quote.class)
                .build();
        searchAdapter = new SearchAdapter(this, searchOptions);
        searchRV.setAdapter(searchAdapter);
        searchAdapter.startListening();

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
        String swipeUID = firebaseAdapter.getCreatorAccountFromPosition(position);
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
        UserQuoteRepository userQuotesReference = new UserQuoteRepository(swipeUID);
        userQuotesReference
                .getAll()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usrQuotes = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Quote quote = data.getValue(Quote.class);
                            usrQuotes.add(quote);
                        }
                        usrAdapter.setItems(usrQuotes);
                        usrAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Something went Wrong.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
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
                    collectionsAdapter.setQuote(firebaseAdapter.getQuoteFromPosition(position));
                    quoteSwipeLayout.setLockDrag(true);
                    closeBookmarks();
                }
                quoteSwipeLayout.setLockDrag(false);
            }
        });
        //try to set the creator info on the first position
        try {
            setCurrentQuoteCreatorInfo(0);
            collectionsAdapter.setQuote(firebaseAdapter.getQuoteFromPosition(0));
        } catch (IndexOutOfBoundsException e) {
            e.getCause();
        }
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
        if (firebaseAdapter.getItemCount() != 0) {
            setCurrentQuoteCreatorInfo(lastFirstVisiblePosition);
            collectionsAdapter.setQuote(firebaseAdapter.getQuoteFromPosition(lastFirstVisiblePosition));
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
        searchView = findViewById(R.id.searchView);
        searchLayout = findViewById(R.id.searchLinearLayout);
        searchRV = findViewById(R.id.searchRecyclerView);
        mainBackButton.setVisibility(View.VISIBLE);
        quoteItemBackground = findViewById(R.id.quoteItemBackground);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        LinearLayoutManager usrManager = new LinearLayoutManager(this);
        LinearLayoutManager collectionsManager = new LinearLayoutManager(this);
        LinearLayoutManager searchManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        searchRV.setLayoutManager(searchManager);
        usrQuotesRV.setLayoutManager(usrManager);
        collectionsList.setLayoutManager(collectionsManager);
        usrAdapter = new UserQuoteAdapter(this);
        collectionsAdapter = new BookmarksAdapter(this);
        usrQuotesRV.setAdapter(usrAdapter);
        quoteRepository = new QuoteRepository();
    }

}