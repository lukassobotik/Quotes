package com.sforge.quotes.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.CollectionActivityAdapter;
import com.sforge.quotes.adapter.QuoteAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.repository.UserCollectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectionsFragment extends Fragment implements CollectionActivityAdapter.OnItemClickListener, CollectionActivityAdapter.OnItemLongClickListener, QuoteAdapter.OnQuoteLongClickListener {
    private static final String ARG_VIEWED_COLLECTION = "viewedCollection";

    private String viewedCollectionParam;

    RecyclerView recyclerView;
    DatabasePagingOptions<Quote> quoteOptions;
    CollectionActivityAdapter collectionsAdapter;
    Button addButton, backButton;
    View placeholder;
    QuoteAdapter quoteAdapter;
    boolean viewingQuotes = false;
    String localCollection = "";
    private final int PREFETCH_DISTANCE = 10;

    public CollectionsFragment() {
        // Required empty public constructor
    }

    public static CollectionsFragment newInstance(String viewedCollectionParam) {
        CollectionsFragment fragment = new CollectionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIEWED_COLLECTION, viewedCollectionParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewedCollectionParam = getArguments().getString(ARG_VIEWED_COLLECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_collections, container, false);

        defineViews(fragmentView);

        collectionsAdapter.setOnItemClickListener(this);
        collectionsAdapter.setOnItemLongClickListener(this);
        quoteAdapter.setOnQuoteLongClickListener(this);

        if (viewedCollectionParam != null) {
            loadCollection(viewedCollectionParam);
        }

        addButton.setOnClickListener(view -> {
            createCollection();
        });

        backButton.setOnClickListener(view -> {
            if (viewingQuotes) {
                recyclerView.setAdapter(collectionsAdapter);
                viewingQuotes = false;
                viewedCollectionParam = null;
                placeholder.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);
            }
        });

        return fragmentView;
    }

    private void createCollection() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Create Collection");

        LinearLayout layout = new LinearLayout(requireActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 0, 50, 0);

        EditText input = new EditText(requireActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setLayoutParams(params);

        layout.addView(input);
        builder.setView(layout);

        builder.setPositiveButton("Create", (dialogInterface, i) -> {
            String collection = input.getText().toString().trim();
            if (collection.length() > 0) {
                // Create an empty quote (which will not be shown) so the directory will exist in Firebase
                new UserCollectionRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), collection).add(new Quote("", "", ""));
            }
            recyclerView.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {});
        builder.create().show();
    }

    public void loadQuotes(String nodeId, String collection, boolean deleteOldQuotes) {
        Query query;

        if (nodeId == null) {
            query = new UserCollectionRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), collection)
                    .getDatabaseReference()
                    .orderByKey()
                    .limitToFirst(PREFETCH_DISTANCE);
        } else {
            query = new UserCollectionRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), collection)
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

                    if (quote == null) {
                        continue;
                    }

                    if ("".equals(quote.getQuote()) && "".equals(quote.getAuthor()) && "".equals(quote.getUser())) {
                        continue;
                    }

                    Quote quoteWithKey = new Quote(quote.getQuote(), quote.getAuthor(), quote.getUser(), data.getKey());
                    quotes.add(quoteWithKey);
                }

                if (deleteOldQuotes) {
                    quoteAdapter.setItems(new ArrayList<>(quotes));
                    quoteAdapter.notifyDataSetChanged();
                } else {
                    quoteAdapter.addItems(new ArrayList<>(quotes));
                    quoteAdapter.notifyItemRangeInserted(quoteAdapter.getItemCount(), quotes.size());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadCollection(String collection) {
        localCollection = collection;
        viewedCollectionParam = collection;
        backButton.setVisibility(View.VISIBLE);
        loadQuotes(null, collection, true);
        recyclerView.setAdapter(quoteAdapter);
        quoteAdapter.notifyDataSetChanged();
        placeholder.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        viewingQuotes = true;
    }

    public void delete(String collection) {
        UserCollectionRepository collectionRepository = new UserCollectionRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), collection);
        collectionRepository.getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
                builder.setTitle("Delete " + collection + "?");
                builder.setMessage("All of the Quotes Saved in \"" + collection + "\" Will Be Deleted.");
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String quoteKey = child.getKey();
                        Log.d("createActionBar", "enter");

                        if (quoteKey != null) {
                            collectionRepository.remove(quoteKey);
                        }
                    }
                });
                builder.setNegativeButton("No", (dialogInterface, i) -> {});
                builder.create().show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed To Retrieve Data. " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteQuote(int position) {
        Quote quote = quoteAdapter.getQuoteFromPosition(position);
        UserCollectionRepository collectionRepository = new UserCollectionRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), localCollection);
        collectionRepository.getDatabaseReference().orderByChild("quote").equalTo(quote.getQuote())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String quoteKey = child.getKey();
                            Quote childQuote = child.getValue(Quote.class);

                            if (childQuote != null && childQuote.getAuthor().equals(quote.getAuthor())) {
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
                                builder.setTitle("Delete " + quote.getQuote() + " From " + "\"" + localCollection + "\"" +"?");
                                builder.setMessage("Are you sure you want to delete \"" + quote.getQuote() + "\"" + " From " + "\"" + localCollection + "\"" +"?");
                                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                                    collectionRepository.remove(quoteKey);
                                });
                                builder.setNegativeButton("No", (dialogInterface, i) -> {});
                                builder.create().show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void defineViews(View view) {
        recyclerView = view.findViewById(R.id.collectionActivityRecyclerView);
        addButton = view.findViewById(R.id.addToCollection);
        placeholder = view.findViewById(R.id.collection_placeholder);
        backButton = view.findViewById(R.id.collectionBackButton);
        backButton.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Query query = new UserBookmarksRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).getDatabaseReference();
        FirebaseRecyclerOptions<DataSnapshot> options = new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();
        quoteAdapter = new QuoteAdapter(getActivity());
        collectionsAdapter = new CollectionActivityAdapter(getContext(), options);
        collectionsAdapter.startListening();
        recyclerView.setAdapter(collectionsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        SnapHelper snapHelper = new PagerSnapHelper();
        if (recyclerView.getOnFlingListener() == null) {
            snapHelper.attachToRecyclerView(recyclerView);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    loadQuotes(quoteAdapter.getLastItemId(), localCollection, false);
                }
            }
        });
    }

    @Override
    public void onItemClick(String itemName) {
        loadCollection(itemName);
    }

    @Override
    public void onItemLongClick(final String itemName) {
        delete(itemName);
    }

    @Override
    public void onQuoteLongClick(final int position) {
        deleteQuote(position);
    }

    //      TODO: Make this work in the Fragment
//    @Override
//    public void onBackPressed() {
//        if (viewingQuotes) {
//            recyclerView.setAdapter(collectionsAdapter);
//            viewingQuotes = false;
//            placeholder.setVisibility(View.VISIBLE);
//            addButton.setVisibility(View.VISIBLE);
//            back.setVisibility(View.VISIBLE);
//            remove.setVisibility(View.VISIBLE);
//            return;
//        }
//
//        super.onBackPressed();
//    }
}