package com.sforge.quotes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.activity.MainActivity;
import com.sforge.quotes.entity.Background;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.entity.UserPreferences;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.repository.UserPreferencesRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.view.QuoteVH;
import com.sforge.quotes.view.UserQuoteVH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class QuoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<Quote> list = new ArrayList<>();

    /**
     * Constructor of the Adapter
     * @param context is the Activity where the data is displayed in
     */
    public QuoteAdapter(Context context){
        this.context = context;
    }

    /**
     * Common Method used to change the displayed list
     * @param quotes is the list of quotes that is supposed to be displayed
     */
    public void setItems(List<Quote> quotes){
        list.clear();
        list.addAll(quotes);
    }

    /**
     * Common Method for adding more items to the displayed list
     * @param quotes is the list of quotes where you add more items to
     */
    public void addItems(List<Quote> quotes) {
        Collections.shuffle(quotes);
        list.addAll(quotes);
    }

    /**
     * Method used to setting the quote creator
     * @param position what position is the recycler view scrolled to
     * @return user id
     */
    public String getCreatorAccountFromPosition(int position){
        return list.get(position).getUser();
    }

    /**
     * Method used to define what quote to add to a collection
     * @param position what position is the recycler view scrolled to
     * @return quote what is supposed to be added to a collection
     */
    public Quote getQuoteFromPosition(int position) {
        return list.get(position);
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
                                        TextView tvQuote = view.findViewById(R.id.textQuote);
                                        TextView tvAuthor = view.findViewById(R.id.textAuthor);

                                        if (usrPrefs.get(0).getBgId().equals(backgroundEntity.BLUE_SKY_1)){
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.blue_sky_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.grey, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.grey, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.BRIDGE_IN_FOREST_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.bridge_in_forest_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.NIGHT_CITY_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.night_city_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.DARK_MOUNTAINS_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.dark_mountains_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.BRIDGE_IN_FOREST_2)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.bridge_in_forest_2);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.FOREST_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.forest_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.GREY)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.grey);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.ORANGE_PURPLE_GRADIENT)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.orange_purple_gradient);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.WHITE_GRADIENT)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.white_gradient);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.grey, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.grey, null));
                                        }
                                        if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_BLUE_SKY_1)){
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_blue_sky_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.grey, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.grey, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_BRIDGE_IN_FOREST_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_bridge_in_forest_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_NIGHT_CITY_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_night_city_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_DARK_MOUNTAINS_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_dark_mountains_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_BRIDGE_IN_FOREST_2)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_bridge_in_forest_2);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_FOREST_1)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_forest_1);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_GREY)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_grey);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_ORANGE_PURPLE_GRADIENT)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_orange_purple_gradient);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.white, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.white, null));
                                        } else if (usrPrefs.get(0).getBgId().equals(backgroundEntity.RSZ_WHITE_GRADIENT)) {
                                            view.findViewById(R.id.quoteItemBackground).setBackgroundResource(R.drawable.rsz_white_gradient);
                                            tvQuote.setTextColor(view.getResources().getColor(R.color.grey, null));
                                            tvAuthor.setTextColor(view.getResources().getColor(R.color.grey, null));
                                        }
                                        if (usrPrefs.get(0).getBgId().equals(backgroundEntity.DYNAMIC)) {

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

    /**
     * Method used to load more data from the database
     * @return key of the last quote in the list
     */
    public String getLastItemId() {
        if (list.size() > 0) {
            return list.get(list.size() - 1).getKey();
        } else {
            return "";
        }
    }
}