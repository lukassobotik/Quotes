package com.sforge.quotes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.entity.Background;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.entity.UserPreferences;
import com.sforge.quotes.repository.UserPreferencesRepository;
import com.sforge.quotes.view.QuoteVH;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<Quote> list = new ArrayList<>();
    public QuoteAdapter(Context ctx){
        this.context = ctx;
    }
    public void setItems(List<Quote> quote){
        list.clear();
        list.addAll(quote);
    }

    public String getCreatorAccountFromPosition(int position){
        return list.get(position).getUser();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QuoteVH vh = (QuoteVH) holder;
        Quote quote = list.get(position);
        vh.textQuote.setText(quote.getQuote());
        vh.textAuthor.setText(quote.getAuthor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}