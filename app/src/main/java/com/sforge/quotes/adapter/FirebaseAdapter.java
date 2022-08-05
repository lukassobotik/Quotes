package com.sforge.quotes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.entity.Background;
import com.sforge.quotes.entity.HolderItem;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.entity.UserPreferences;
import com.sforge.quotes.repository.UserPreferencesRepository;
import com.sforge.quotes.view.QuoteVH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FirebaseAdapter extends FirebaseRecyclerPagingAdapter<Quote, QuoteVH> {

    Context context;
    List<Quote> list = new ArrayList<>();
    boolean randomize;
    RecyclerView recyclerView;
    List<HolderItem> holderItems = new ArrayList<>();
    /**
     * Construct a new FirestorePagingAdapter from the given {@link DatabasePagingOptions}.
     *
     * @param options Defined options of the adapter
     */
    public FirebaseAdapter(Context context, @NonNull DatabasePagingOptions<Quote> options, boolean randomizeItems, RecyclerView recyclerView) {
        super(options);
        this.context = context;
        this.randomize = randomizeItems;
        this.recyclerView = recyclerView;
    }

    public String getCreatorAccountFromPosition(int position) {
        return list.get(position).getUser();
    }

    public Quote getQuoteFromPosition(int position) {
        return list.get(position);
    }

    public void setList(List<Quote> list) {
        this.list = list;
    }
    public void shuffleList() {
        Collections.shuffle(list);
    }

    @NonNull
    @Override
    public QuoteVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quote_item, parent, false);
        Background backgroundEntity = new Background();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            new UserPreferencesRepository(user.getUid()).getDatabaseReference()
                    .addValueEventListener(
                            new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    List<UserPreferences> usrPrefs = new ArrayList<>();
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        UserPreferences userPreferences = data.getValue(UserPreferences.class);
                                        usrPrefs.add(userPreferences);
                                    }
                                    if (snapshot.getValue() != null) {
                                        if (usrPrefs.get(0).getBgId().equals(backgroundEntity.BLUE_SKY_1)){
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.blue_sky_1);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.BRIDGE_IN_FOREST_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.bridge_in_forest_1);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.NIGHT_CITY_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.night_city_1);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.DARK_MOUNTAINS_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.dark_mountains_1);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.BRIDGE_IN_FOREST_2)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.bridge_in_forest_2);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.FOREST_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.forest_1);
                                        }
                                        if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_BLUE_SKY_1)){
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_blue_sky_1);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_BRIDGE_IN_FOREST_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_bridge_in_forest_1);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_NIGHT_CITY_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_night_city_1);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_DARK_MOUNTAINS_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_dark_mountains_1);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_BRIDGE_IN_FOREST_2)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_bridge_in_forest_2);
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_FOREST_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_forest_1);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
        }
        return new QuoteVH(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuoteVH holder, int position, @NonNull Quote model) {
        //hide the first quote that is empty in collections
        if (model.getQuote().equals("") && model.getAuthor().equals("") && model.getUser().equals("")) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holderItems.add(new HolderItem(holder, model));
            if (!randomize) {
                list.add(model);
                holder.textQuote.setText(model.getQuote());
                holder.textAuthor.setText(model.getAuthor());
            } else {
                // getting the current position because position from onBindViewHolder isn't accurate
                int pos = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastVisibleItemPosition();

                if (pos < 0) {
                    list = new ArrayList<>();

                    // onBindViewHolder gets called only if it needs to (1 quote ahead),
                    // because of that this loads every quote that is fetched from the db,
                    // but setting it issue because on position 0 only the holder for the current quote is called,
                    // and not the quote ahead.
                    for (int i = 0; i < this.getItemCount(); i++) {
                        list.add(Objects.requireNonNull(this.peek(i)).getValue(Quote.class));
                    }
                }

                if (pos == -1) {
                    Collections.shuffle(list.subList(0, list.size()));
                    this.setList(list);
                } else if (pos > 0) {
                    Collections.shuffle(list.subList(pos + 1, list.size()));
                    this.setList(list);
                }

                // checking if text from holder equals "Text" because if the quote is already shown it shouldn't be overwritten
                if (pos == -1
                        && holder.textQuote.getText().toString().equals("Text")
                        && holder.textAuthor.getText().toString().equals("Text")) {
                    holder.textQuote.setText(list.get(0).getQuote());
                    holder.textAuthor.setText(list.get(0).getAuthor());
                } else if (pos == 0 && pos < this.getItemCount()
                        && holder.textQuote.getText().toString().equals("Text")
                        && holder.textAuthor.getText().toString().equals("Text")) {
                    holder.textQuote.setText(list.get(1).getQuote());
                    holder.textAuthor.setText(list.get(1).getAuthor());
                } else if (pos > 0 && pos < this.getItemCount()
                        && holder.textQuote.getText().toString().equals("Text")
                        && holder.textAuthor.getText().toString().equals("Text")) {
                    holder.textQuote.setText(list.get(pos).getQuote());
                    holder.textAuthor.setText(list.get(pos).getAuthor());
                }
            }
        }
    }
}