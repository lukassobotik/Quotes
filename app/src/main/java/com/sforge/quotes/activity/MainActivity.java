package com.sforge.quotes.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.QuoteAdapter;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.repository.UsernameRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Firebase Related
    boolean isLoggedIn = false;
    final boolean[] profileIsOpen = {false};
    int databaseSize = 0;
    QuoteRepository quoteRepository;
    List<Quote> quotes = new ArrayList<>();
    List<Quote> currentQuotes = new ArrayList<>();

    //Account Profile Related
    LinearLayout includeAccountProfile;
    TextView mainActivityUsername;
    List<Quote> usrQuotes;

    //UI Related
    Button createQuote, profileButton, showUserProfileButton, profileLogoutButton, profileLoginButton, mainBackButton;
    QuoteAdapter adapter;
    UserQuoteAdapter usrAdapter;
    ConstraintLayout quoteItemBackground;

    int oldPosition = 0;
    int lastFirstVisiblePosition;
    float x1, x2, y1, y2;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView, usrQuotesRV;

    List<Quote> loadedQuotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defineViews();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentQuotes.clear();
            loadAllDataFromDatabase();
            swipeRefreshLayout.setRefreshing(false);
            oldPosition = 0;
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            isLoggedIn = true;
        }

        if (isLoggedIn) {
            profileLoginButton.setVisibility(View.GONE);
        }

        createQuotesButton();

        profileMenuClickListeners();

        loadAllDataFromDatabase();

        createMainBackButton();
    }

    public void createMainBackButton() {
        mainBackButton.setOnClickListener(view -> {
            swipeRefreshLayout.setEnabled(true);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            recyclerView.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_to_right));
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
        });
    }

    //Profile Button On Click Logic
    public void profileIconOnClickEvent() {
        if (!profileIsOpen[0]) {
            if (isLoggedIn) {
                showUserProfileButton.setVisibility(View.VISIBLE);
                profileLogoutButton.setVisibility(View.VISIBLE);
            } else {
                profileLoginButton.setVisibility(View.VISIBLE);
            }
            showUserProfileButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_top_to_bottom));
            profileLogoutButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_top_to_bottom));
            profileLoginButton.startAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.profile_button_slide_top_to_bottom));
            profileIsOpen[0] = true;
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
            profileIsOpen[0] = false;
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                    //Set swipe threshold
                    return 0.2f;
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.LEFT) {
                        recyclerView.setVisibility(View.GONE);
                        swipeRefreshLayout.setEnabled(false);
                    }
                }
            };

    public void setCurrentQuoteCreatorInfo(int position){
        String swipeUID = adapter.getCreatorAccountFromPosition(position);
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
                Toast.makeText(MainActivity.this, "Cannot Access the Database Right Now", Toast.LENGTH_SHORT).show();
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

    //Motion event detection in Creator Account to show all the quotes (left to right swipe detection)
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    swipeRefreshLayout.setEnabled(true);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    recyclerView.startAnimation(
                            AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_to_right));
                }
                break;
        }
        return super.onTouchEvent(touchEvent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SnapHelper snapHelper = new PagerSnapHelper();
        if (recyclerView.getOnFlingListener() == null) {
            snapHelper.attachToRecyclerView(recyclerView);
        }

        //Listens for scrolls and sets the info of the quote creator + loads only a few quotes at a time to save performance
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && position != -1) {
                    setCurrentQuoteCreatorInfo(position);

                    //Load the next quote
                    if (position + 1 < databaseSize && position > oldPosition) {
                        loadedQuotes.add(currentQuotes.get(position + 1));
                    }

                    adapter.setItems(loadedQuotes);
                    adapter.notifyDataSetChanged();

                    if (oldPosition < position) {
                        oldPosition = position;
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Store the Recyclerview scroll position
        lastFirstVisiblePosition =
                ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Restore the last stored Recyclerview scroll position
        recyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
    }

    public void loadAllDataFromDatabase() {
        quoteRepository.getAll().addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Quote quote = data.getValue(Quote.class);
                            quotes.add(quote);
                        }
                        Collections.shuffle(quotes);
                        adapter.setItems(quotes);
                        adapter.notifyDataSetChanged();
                        currentQuotes.addAll(quotes);
                        if (!currentQuotes.isEmpty()) {
                            includeAccountProfile.setVisibility(View.VISIBLE);
                        }
                        //Load the creator info of the first loaded Quote
                        setCurrentQuoteCreatorInfo(0);

                        databaseSize = quotes.size();

                        loadedQuotes.clear();
                        loadedQuotes.add(quotes.get(0));
                        loadedQuotes.add(quotes.get(1));
                        quotes.clear();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Failed to Retrieve Data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void defineViews() {
        createQuote = findViewById(R.id.createQuote);
        swipeRefreshLayout = findViewById(R.id.quoteSwipeRefreshLayout);
        recyclerView = findViewById(R.id.quoteRecyclerView);
        usrQuotesRV = findViewById(R.id.usrQuotes);
        profileButton = findViewById(R.id.userProfileButton);
        showUserProfileButton = findViewById(R.id.profileShowProfile);
        profileLogoutButton = findViewById(R.id.profileLogout);
        profileLoginButton = findViewById(R.id.profileLogin);
        includeAccountProfile = findViewById(R.id.includeAccountProfile);
        mainActivityUsername = findViewById(R.id.username);
        mainBackButton = findViewById(R.id.mainBackButton);
        mainBackButton.setVisibility(View.VISIBLE);
        quoteItemBackground = findViewById(R.id.quoteItemBackground);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        LinearLayoutManager usrManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        usrQuotesRV.setLayoutManager(usrManager);
        adapter = new QuoteAdapter(this);
        usrAdapter = new UserQuoteAdapter(this);
        recyclerView.setAdapter(adapter);
        usrQuotesRV.setAdapter(usrAdapter);
        quoteRepository = new QuoteRepository();
    }

}