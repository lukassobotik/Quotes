package com.sforge.quotes.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sforge.quotes.R;

public class CollectionsVH extends RecyclerView.ViewHolder {
    public TextView name;
    public CollectionsVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.collection_item_name);
    }
}
