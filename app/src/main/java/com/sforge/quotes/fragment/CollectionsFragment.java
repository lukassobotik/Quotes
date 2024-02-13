package com.sforge.quotes.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
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
public class CollectionsFragment extends Fragment implements CollectionActivityAdapter.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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

    public CollectionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CollectionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CollectionsFragment newInstance(String param1, String param2) {
        CollectionsFragment fragment = new CollectionsFragment();
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
        View fragmentView = inflater.inflate(R.layout.fragment_collections, container, false);

        defineViews(fragmentView);

        collectionsAdapter.setOnItemClickListener(this);

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
                // TODO: Create finish() method
//                finish();
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

        return fragmentView;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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
        addLayout = view.findViewById(R.id.createCollectionLayout);
        createCollectionEditText = view.findViewById(R.id.createCollectionEditText);
        create = view.findViewById(R.id.createCollectionButton);
        cancel = view.findViewById(R.id.createCollectionButtonCancel);
        remove = view.findViewById(R.id.removeCollection);
        back = view.findViewById(R.id.collectionBackButton);
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
        onClickCalled(itemName);
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