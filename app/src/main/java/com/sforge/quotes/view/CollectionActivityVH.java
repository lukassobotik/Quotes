package com.sforge.quotes.view;

import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sforge.quotes.R;

public class CollectionActivityVH extends RecyclerView.ViewHolder {
    public TextView name;
    public ToggleButton favorite;
    public CollectionActivityVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.collection_activity_item_name);
        favorite = itemView.findViewById(R.id.favoriteCollectionButton);
    }
}
