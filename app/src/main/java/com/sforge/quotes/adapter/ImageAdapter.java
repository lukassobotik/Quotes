package com.sforge.quotes.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.sforge.quotes.R;
import com.sforge.quotes.entity.Background;
import com.sforge.quotes.entity.UserPreferences;
import com.sforge.quotes.repository.UserPreferencesRepository;
import com.sforge.quotes.view.ImageVH;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;
    List<Drawable> list = new ArrayList<>();
    public ImageAdapter(Context context){
        this.context = context;
    }
    public void setItems(List<Drawable> image){
        list.clear();
        list.addAll(image);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.change_background_item, parent, false);
        return new ImageVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImageVH vh = (ImageVH) holder;
        Drawable image = list.get(position);
        vh.image.setImageDrawable(image);

        UserPreferencesRepository userPreferencesRepository = new UserPreferencesRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        Background background = new Background();
        AtomicReference<String> bgId = new AtomicReference<>("");

        //Long Click To Set Lower Image Quality
        holder.itemView.setOnLongClickListener(view -> {
            if (position == 0) {
                bgId.set(background.RSZ_BLUE_SKY_1);
            } else if (position == 1) {
                bgId.set(background.RSZ_BRIDGE_IN_FOREST_1);
            } else if (position == 2) {
                bgId.set(background.RSZ_NIGHT_CITY_1);
            } else if (position == 3) {
                bgId.set(background.RSZ_DARK_MOUNTAINS_1);
            } else if (position == 4) {
                bgId.set(background.RSZ_BRIDGE_IN_FOREST_2);
            } else if (position == 5){
                bgId.set(background.RSZ_FOREST_1);
            }
            UserPreferences userPreferences = new UserPreferences(bgId.get(), "low");
            userPreferencesRepository.addWithKey("Background", userPreferences);
            return true;
        });

        holder.itemView.setOnClickListener(view -> {
            if (position == 0) {
                bgId.set(background.BLUE_SKY_1);
            } else if (position == 1) {
                bgId.set(background.BRIDGE_IN_FOREST_1);
            } else if (position == 2) {
                bgId.set(background.NIGHT_CITY_1);
            } else if (position == 3) {
                bgId.set(background.DARK_MOUNTAINS_1);
            } else if (position == 4) {
                bgId.set(background.BRIDGE_IN_FOREST_2);
            } else if (position == 5) {
                bgId.set(background.FOREST_1);
            } else if (position == 6) {
                bgId.set(background.FOREST_1);
            }
            UserPreferences userPreferences = new UserPreferences(bgId.get(), "high");
            userPreferencesRepository.addWithKey("Background", userPreferences);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

