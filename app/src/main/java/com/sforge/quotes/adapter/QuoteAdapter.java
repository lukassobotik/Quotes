package com.sforge.quotes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.sforge.quotes.fragment.CollectionsFragment;
import com.sforge.quotes.repository.UserPreferencesRepository;
import com.sforge.quotes.view.QuoteVH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnQuoteLongClickListener {
        void onQuoteLongClick(int position);
    }

    private final Context context;
    private OnQuoteLongClickListener longClickListener;
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

    public void setOnQuoteLongClickListener(OnQuoteLongClickListener listener) {
        this.longClickListener = listener;
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
    public String getCreatorAccountFromPosition(int position) {
        if ((list == null || list.isEmpty())) {
            return "";
        } else if (position < 0 || position >= list.size()) {
            return "";
        } else {
            return list.get(position).getUser();
        }
    }

    /**
     * Method used to define what quote to add to a collection
     * @param position what position is the recycler view scrolled to
     * @return quote what is supposed to be added to a collection
     */
    public Quote getQuoteFromPosition(int position) {
        if (list != null && !list.isEmpty()) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quote_item, parent, false);
        Background backgroundEntity = new Background();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setBackground(user, view, backgroundEntity);
        return new QuoteVH(view);
    }

    private static void setBackground(final FirebaseUser user, final View view, final Background backgroundEntity) {
        if (user == null) {
            return;
        }
        new UserPreferencesRepository(user.getUid()).getDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
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

                            applyUserPreferences(usrPrefs, view, tvQuote, tvAuthor, backgroundEntity);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private static void setBackgroundAndTextColor(View view, TextView tvQuote, TextView tvAuthor, int backgroundResource, int textColor) {
        view.findViewById(R.id.quoteItemBackground).setBackgroundResource(backgroundResource);
        tvQuote.setTextColor(view.getResources().getColor(textColor, null));
        tvAuthor.setTextColor(view.getResources().getColor(textColor, null));
    }

    private static void applyUserPreferences(List<UserPreferences> usrPrefs, View view, TextView tvQuote, TextView tvAuthor, Background backgroundEntity) {
        String bgId = usrPrefs.get(0).getBgId();
        if (bgId.equals(backgroundEntity.BLUE_SKY_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.blue_sky_1, R.color.grey);
        else if (bgId.equals(backgroundEntity.BRIDGE_IN_FOREST_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.bridge_in_forest_1, R.color.white);
        else if (bgId.equals(backgroundEntity.NIGHT_CITY_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.night_city_1, R.color.white);
        else if (bgId.equals(backgroundEntity.DARK_MOUNTAINS_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.dark_mountains_1, R.color.white);
        else if (bgId.equals(backgroundEntity.BRIDGE_IN_FOREST_2)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.bridge_in_forest_2, R.color.white);
        else if (bgId.equals(backgroundEntity.FOREST_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.forest_1, R.color.white);
        else if (bgId.equals(backgroundEntity.GREY)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.grey, R.color.white);
        else if (bgId.equals(backgroundEntity.ORANGE_PURPLE_GRADIENT)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.orange_purple_gradient, R.color.white);
        else if (bgId.equals(backgroundEntity.WHITE_GRADIENT)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.white_gradient, R.color.grey);
        else if (bgId.equals(backgroundEntity.RSZ_BLUE_SKY_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_blue_sky_1, R.color.grey);
        else if (bgId.equals(backgroundEntity.RSZ_BRIDGE_IN_FOREST_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_bridge_in_forest_1, R.color.white);
        else if (bgId.equals(backgroundEntity.RSZ_NIGHT_CITY_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_night_city_1, R.color.white);
        else if (bgId.equals(backgroundEntity.RSZ_DARK_MOUNTAINS_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_dark_mountains_1, R.color.white);
        else if (bgId.equals(backgroundEntity.RSZ_BRIDGE_IN_FOREST_2)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_bridge_in_forest_2, R.color.white);
        else if (bgId.equals(backgroundEntity.RSZ_FOREST_1)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_forest_1, R.color.white);
        else if (bgId.equals(backgroundEntity.RSZ_GREY)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_grey, R.color.white);
        else if (bgId.equals(backgroundEntity.RSZ_ORANGE_PURPLE_GRADIENT)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_orange_purple_gradient, R.color.white);
        else if (bgId.equals(backgroundEntity.RSZ_WHITE_GRADIENT)) setBackgroundAndTextColor(view, tvQuote, tvAuthor, R.drawable.rsz_white_gradient, R.color.grey);
        else if (bgId.equals(backgroundEntity.DYNAMIC)) {

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QuoteVH vh = (QuoteVH) holder;
        Quote quote = list.get(position);
        vh.textQuote.setText(quote.getQuote());
        vh.textAuthor.setText(quote.getAuthor());

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

        if (context instanceof MainActivity) {
            Fragment currentFragment = ((MainActivity) context).getCurrentFragment();
            if (currentFragment instanceof CollectionsFragment && auth != null) {
                vh.itemView.setOnLongClickListener(view -> {
                    longClickListener.onQuoteLongClick(position);
                    return true;
                });
            }
        }
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
        if (!list.isEmpty()) {
            return list.get(list.size() - 1).getKey();
        } else {
            return "";
        }
    }
}