package com.sforge.quotes.activity;

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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.repository.UserCollectionRepository;

import java.util.Objects;

public class CollectionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabasePagingOptions<Quote> quoteOptions;
    CollectionActivityAdapter collectionsAdapter;
    Button addButton, create, cancel, remove, back;
    View placeholder;
    ConstraintLayout addLayout;
    EditText createCollectionEditText;
    FirebaseAdapter firebaseAdapter;
    boolean viewingQuotes = false;
    boolean deleteTool = false;
    String localCollection = "";

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
                new UserCollectionRepository(FirebaseAuth.getInstance().getCurrentUser().getUid(), collection).add(new Quote("", "", ""));
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
                deleteQuote(position - 1);
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
        Query query = new UserBookmarksRepository(FirebaseAuth.getInstance().getCurrentUser().getUid()).getDatabaseReference();
        FirebaseRecyclerOptions<DataSnapshot> options = new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();

        collectionsAdapter = new CollectionActivityAdapter(this, options);
        collectionsAdapter.startListening();
        recyclerView.setAdapter(collectionsAdapter);
    }

    public void onClickCalled(String collection) {
        localCollection = collection;
        if (deleteTool) {
            delete(collection);
            deleteTool = false;
            remove.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete, null));
        } else {
            Query quoteQuery = new UserCollectionRepository(FirebaseAuth.getInstance().getCurrentUser().getUid(), collection).getDatabaseReference();
            PagingConfig quotePagingConfig = new PagingConfig(1, 1, false);
            quoteOptions = new DatabasePagingOptions.Builder<Quote>()
                    .setLifecycleOwner(this)
                    .setQuery(quoteQuery, quotePagingConfig, Quote.class)
                    .build();
            firebaseAdapter = new FirebaseAdapter(this, quoteOptions);
            recyclerView.setAdapter(firebaseAdapter);
            firebaseAdapter.startListening();
            placeholder.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            viewingQuotes = true;
            quoteQuery = null;
        }
    }

    public void delete(String collection) {
        //get and delete the Collection
        UserCollectionRepository collectionRepository = new UserCollectionRepository(FirebaseAuth.getInstance().getCurrentUser().getUid(), collection);
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
        Quote quote = firebaseAdapter.getQuoteFromPosition(position);
        UserCollectionRepository collectionRepository = new UserCollectionRepository(FirebaseAuth.getInstance().getCurrentUser().getUid(), localCollection);
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
                            firebaseAdapter.refresh();
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