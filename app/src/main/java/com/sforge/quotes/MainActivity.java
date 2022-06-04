package com.sforge.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button createQuote;
    QuoteAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
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
            Intent i = new Intent(MainActivity.this, CreateQuotes.class);
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            recyclerView.setAdapter(null);
            recyclerView.setAdapter(loadData());
            swipeRefreshLayout.setRefreshing(false);
            Log.d("createActionBar", "" + adapter.list.size() + " - " + adapter.getItemCount());
        });

        dao = new DAOQuote();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
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
        createQuote = (Button) findViewById(R.id.createQuote);
        swipeRefreshLayout = findViewById(R.id.quoteSwipeRefreshLayout);
        recyclerView = findViewById(R.id.quoteRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new QuoteAdapter(this);
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