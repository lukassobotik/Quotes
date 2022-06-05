package com.sforge.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    boolean isLoggedIn = false;
    final boolean[] profileIsOpen = {false};

    Button createQuote, creatorAccount, profileShow, profileLogout, profileLogin;
    Button usrQuotesNext, usrQuotesPrev;
    QuoteAdapter adapter;

    float x1, x2, y1, y2;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView, usrQuotesRV;
    int scrollDy = 0;
    int quoteHeight = 0;

    int dbSize = 0;
    String key = null;
    DAOQuote dao = new DAOQuote();
    List<Quote> quotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createActionBar();
        defineViews();

        createQuote.setOnClickListener(view -> {
            finish();
            Intent i = new Intent(MainActivity.this, CreateQuotes.class);
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);
        usrQuotesRV.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            recyclerView.setAdapter(null);
            recyclerView.setAdapter(loadData());
            swipeRefreshLayout.setRefreshing(false);
            Log.d("createActionBar", "" + adapter.list.size() + " - " + adapter.getItemCount());
        });
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            isLoggedIn = true;
        }

        if(isLoggedIn){
            profileLogin.setVisibility(View.GONE);
        }

        profileShow.setVisibility(View.GONE);
        profileLogout.setVisibility(View.GONE);
        profileLogin.setVisibility(View.GONE);


        creatorAccount.setOnClickListener(view -> {
            profileIconOnClickEvent();
        });

        profileLogin.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        profileShow.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, UserProfile.class));
        });

        profileLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            profileIconOnClickEvent();
            isLoggedIn = false;
        });

        dao = new DAOQuote();
        loadData();

        usrQuotesNext.setVisibility(View.GONE);
        usrQuotesPrev.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        usrQuotesNext.setOnClickListener(view -> {
            usrQuotesRV.scrollBy(width, 0);
        });
        usrQuotesPrev.setOnClickListener(view -> {
            usrQuotesRV.scrollBy(-width, 0);
        });

    }

    public void profileIconOnClickEvent(){
        if (!profileIsOpen[0]){
            if (isLoggedIn) {profileShow.setVisibility(View.VISIBLE);}
            profileShow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_top_to_bottom));
            if (isLoggedIn) {profileLogout.setVisibility(View.VISIBLE);}
            profileLogout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_top_to_bottom));
            if (!isLoggedIn) {profileLogin.setVisibility(View.VISIBLE);}
            profileLogin.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_top_to_bottom));
            profileIsOpen[0] = true;
        } else {
            profileShow.setVisibility(View.GONE);
            profileShow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_bottom_to_top));
            profileLogout.setVisibility(View.GONE);
            profileLogout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_bottom_to_top));
            profileLogin.setVisibility(View.GONE);
            profileLogin.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_bottom_to_top));
            profileIsOpen[0] = false;
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT /*| ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.*/) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            Log.d("createActionBar", "move");
            return false;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                case ItemTouchHelper.RIGHT:
                    recyclerView.setVisibility(View.GONE);
                    usrQuotesNext.setVisibility(View.VISIBLE);
                    usrQuotesPrev.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2){
                    recyclerView.setVisibility(View.VISIBLE);
                    usrQuotesNext.setVisibility(View.GONE);
                    usrQuotesPrev.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    recyclerView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_to_right));
                }else if(x1 > x2){
                    return super.onTouchEvent(touchEvent);
                }
                break;
        }
        return super.onTouchEvent(touchEvent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(usrQuotesRV);
    }

    public QuoteAdapter loadData(){
        dao.get().addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    Quote quote = data.getValue(Quote.class);
                    quotes.add(quote);
                    key = data.getKey();
                    dbSize++;
                }
                Collections.shuffle(quotes);
                adapter.setItems(quotes);
                adapter.notifyDataSetChanged();
                quotes.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return adapter;
    }

    public void defineViews(){
        createQuote = findViewById(R.id.createQuote);
        swipeRefreshLayout = findViewById(R.id.quoteSwipeRefreshLayout);
        recyclerView = findViewById(R.id.quoteRecyclerView);
        usrQuotesRV = findViewById(R.id.usrQuotes);
        recyclerView.setHasFixedSize(true);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        GridLayoutManager gridManager = new GridLayoutManager(this, 3, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        usrQuotesRV.setLayoutManager(gridManager);
        adapter = new QuoteAdapter(this);
        creatorAccount = findViewById(R.id.creatorAccount);
        usrQuotesNext = findViewById(R.id.usrQuotesNextButton);
        usrQuotesPrev = findViewById(R.id.usrQuotesPreviousButton);
        profileShow = findViewById(R.id.profileShowProfile);
        profileLogout = findViewById(R.id.profileLogout);
        profileLogin = findViewById(R.id.profileLogin);
        mAuth = FirebaseAuth.getInstance();
    }

    public void createActionBar(){
        String localNightMode = "undefined";
        int nightModeFlags = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                localNightMode = "night";
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                localNightMode = "light";
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                localNightMode = "undefined";
                break;
        }

        if(localNightMode.equals("night")){
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dark_action_bar, null));
        }
        Log.d("createActionBar", localNightMode);
    }

}