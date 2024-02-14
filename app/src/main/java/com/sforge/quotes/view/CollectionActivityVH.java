package com.sforge.quotes.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sforge.quotes.R;

public class CollectionActivityVH extends RecyclerView.ViewHolder {
    public TextView name;
    public Button pin;
    public CollectionActivityVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.collection_activity_item_name);
        pin = itemView.findViewById(R.id.pinCollectionButton);
    }
}
