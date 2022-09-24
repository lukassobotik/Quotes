package com.sforge.quotes.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.CollectionActivityAdapter;
import com.sforge.quotes.adapter.FirebaseAdapter;
import com.sforge.quotes.adapter.QuoteAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.repository.UserCollectionRepository;
import com.sforge.quotes.repository.UserQuoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CollectionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabasePagingOptions<Quote> quoteOptions;
    CollectionActivityAdapter collectionsAdapter;
    Button addButton, create, cancel, remove, back;
    View placeholder;
    ConstraintLayout addLayout;
    EditText createCollectionEditText;
    QuoteAdapter quoteAdapter;
    boolean viewingQuotes = false;
    boolean deleteTool = false;
    String localCollection = "";
    private final int PREFETCH_DISTANCE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        defineViews();

        addButton.setOnClickListener(view -> {
            addLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
        });
        cancel.setOnClickListener(view -> {
            addLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
        });
        create.setOnClickListener(view -> {
            String collection = createCollectionEditText.getText().toString().trim();
            if (collection.length() > 0) {
                //Create an empty quote (which will not be shown) so the directory will exist in Firebase
                new UserCollectionRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), collection).add(new Quote("", "", ""));
            }
            createCollectionEditText.setText("");
            addLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
        });
        back.setOnClickListener(view -> {
            if (viewingQuotes) {
                recyclerView.setAdapter(collectionsAdapter);
                viewingQuotes = false;
                placeholder.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                remove.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        });
        remove.setOnClickListener(view -> {
            if (viewingQuotes) {
                int position = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                deleteQuote(position);
            } else {
                if (!deleteTool) {
                    remove.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_red, null));
                    deleteTool = true;
                } else {
                    remove.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete, null));
                    deleteTool = false;
                }
            }
        });
    }

    public void defineViews() {
        recyclerView = findViewById(R.id.collectionActivityRecyclerView);
        addButton = findViewById(R.id.addtoCollection);
        placeholder = findViewById(R.id.collection_placeholder);
        addLayout = findViewById(R.id.createCollectionLayout);
        createCollectionEditText = findViewById(R.id.createCollectionEditText);
        create = findViewById(R.id.createCollectionButton);
        cancel = findViewById(R.id.createCollectionButtonCancel);
        remove = findViewById(R.id.removeCollection);
        back = findViewById(R.id.collectionBackButton);
        LinearLayoutManager collectionsManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(collectionsManager);
        Query query = new UserBookmarksRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).getDatabaseReference();
        FirebaseRecyclerOptions<DataSnapshot> options = new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();
        quoteAdapter = new QuoteAdapter(CollectionsActivity.this);
        collectionsAdapter = new CollectionActivityAdapter(this, options);
        collectionsAdapter.startListening();
        recyclerView.setAdapter(collectionsAdapter);
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

                    if (quote.getQuote().equals("") && quote.getAuthor().equals("") && quote.getUser().equals("")) {
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
                Toast.makeText(CollectionsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickCalled(String collection) {
        localCollection = collection;
        if (deleteTool) {
            delete(collection);
            deleteTool = false;
            remove.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete, null));
        } else {
            loadQuotes(null, collection, true);
            recyclerView.setAdapter(quoteAdapter);
            quoteAdapter.notifyDataSetChanged();
            placeholder.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            viewingQuotes = true;
        }
    }

    public void delete(String collection) {
        UserCollectionRepository collectionRepository = new UserCollectionRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), collection);
        collectionRepository.getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CollectionsActivity.this);
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
                Toast.makeText(CollectionsActivity.this, "Failed To Retrieve Data. " + error, Toast.LENGTH_LONG).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(CollectionsActivity.this);
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

    @Override
    protected void onStart() {
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
                    Toast.makeText(CollectionsActivity.this, "Load More", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewingQuotes) {
            recyclerView.setAdapter(collectionsAdapter);
            viewingQuotes = false;
            placeholder.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
            return;
        }

        super.onBackPressed();
    }
}