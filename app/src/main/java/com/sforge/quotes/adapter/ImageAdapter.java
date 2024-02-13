package com.sforge.quotes.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
        AtomicReference<String> bgId = new AtomicReference<>("");

        // Long Click To Set Lower Image Quality
        holder.itemView.setOnLongClickListener(view -> {
            bgId.set(getId(position, true));
            UserPreferences userPreferences = new UserPreferences(bgId.get(), "low");
            userPreferencesRepository.addWithKey("Background", userPreferences).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context.getApplicationContext(), "Successfully changed the background in lower quality", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        });

        holder.itemView.setOnClickListener(view -> {
            bgId.set(getId(position, false));
            UserPreferences userPreferences = new UserPreferences(bgId.get(), "high");
            userPreferencesRepository.addWithKey("Background", userPreferences).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context.getApplicationContext(), "Successfully changed the background in higher quality", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private static String getId(final int position, final Boolean lowerQuality) {
        Background background = new Background();
        String bgId = "";
        if (position == 0) {
            bgId = (lowerQuality ? background.RSZ_BLUE_SKY_1 : background.BLUE_SKY_1);
        } else if (position == 1) {
            bgId = (lowerQuality ? background.RSZ_BRIDGE_IN_FOREST_1 : background.BRIDGE_IN_FOREST_1);
        } else if (position == 2) {
            bgId = (lowerQuality ? background.RSZ_NIGHT_CITY_1 : background.NIGHT_CITY_1);
        } else if (position == 3) {
            bgId = (lowerQuality ? background.RSZ_DARK_MOUNTAINS_1 : background.DARK_MOUNTAINS_1);
        } else if (position == 4) {
            bgId = (lowerQuality ? background.RSZ_BRIDGE_IN_FOREST_2 : background.BRIDGE_IN_FOREST_2);
        } else if (position == 5){
            bgId = (lowerQuality ? background.RSZ_FOREST_1 : background.FOREST_1);
        } else if (position == 6) {
            bgId = (lowerQuality ? background.RSZ_WHITE_GRADIENT : background.WHITE_GRADIENT);
        } else if (position == 7) {
            bgId = (lowerQuality ? background.RSZ_GREY : background.GREY);
        } else if (position == 8){
            bgId = (lowerQuality ? background.RSZ_ORANGE_PURPLE_GRADIENT : background.ORANGE_PURPLE_GRADIENT);
        }
        return bgId;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

