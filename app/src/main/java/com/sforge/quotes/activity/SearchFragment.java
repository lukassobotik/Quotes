package com.sforge.quotes.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.SearchAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final String ARG_LAST_SEARCH = "lastSearch";

    private String lastSearchParam;

    SearchView searchView;
    LinearLayout searchLayout;
    RecyclerView searchRV;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String lastSearch) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LAST_SEARCH, lastSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lastSearchParam = getArguments().getString(ARG_LAST_SEARCH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_search, container, false);

        defineViews(fragmentView);

        if (lastSearchParam != null) {
            searchView.setQuery(lastSearchParam, false);
            search(lastSearchParam);
        }

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

        return fragmentView;
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
        searchView = view.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.requestFocusFromTouch();
        searchRV = view.findViewById(R.id.searchRecyclerView);
        LinearLayoutManager searchManager = new LinearLayoutManager(getActivity());
        searchRV.setLayoutManager(searchManager);
    }
}