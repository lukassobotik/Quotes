package com.sforge.quotes.activity;

import android.os.Bundle;
import android.widget.Button;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    SearchView searchView;
    LinearLayout searchLayout;
    RecyclerView searchRV;
    Button backButton;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_search, container, false);

        defineViews(fragmentView);

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
        // TODO: make finish() work
//            finish();
        });

        return fragmentView;
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
        backButton = view.findViewById(R.id.searchBackButton);
        LinearLayoutManager searchManager = new LinearLayoutManager(getActivity());
        searchRV.setLayoutManager(searchManager);
    }
}