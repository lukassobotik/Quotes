package com.sforge.quotes.view;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sforge.quotes.R;

public class ImageVH extends RecyclerView.ViewHolder {
    public ImageView image;
    public ImageVH(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.change_background_item_image);
    }
}