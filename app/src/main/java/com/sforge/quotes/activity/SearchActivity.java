package com.sforge.quotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.SearchAdapter;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.dialog.CollectionsDialog;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements CollectionsDialog.CollectionsDialogListener {

    SearchView searchView;
    LinearLayout searchLayout;
    RecyclerView searchRV;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        defineViews();

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

        backButton.setOnClickListener(view -> {
            finish();
        });
    }

    public void search(String query) {
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

        SearchAdapter searchAdapter = new SearchAdapter(SearchActivity.this);
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

    public void defineViews() {
        searchLayout = findViewById(R.id.searchLinearLayout);
        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.requestFocusFromTouch();
        searchRV = findViewById(R.id.searchRecyclerView);
        backButton = findViewById(R.id.searchBackButton);
        LinearLayoutManager searchManager = new LinearLayoutManager(this);
        searchRV.setLayoutManager(searchManager);
    }

    @Override
    public void getData(int option) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}