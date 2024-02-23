package com.sforge.quotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.sforge.quotes.R;
import com.sforge.quotes.repository.UserBookmarksRepository;
import com.sforge.quotes.view.CollectionActivityVH;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionActivityAdapter extends FirebaseRecyclerAdapter<DataSnapshot, CollectionActivityVH> {

    public interface OnItemClickListener {
        void onItemClick(String itemName);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(String itemName);
    }
    public interface OnFavoriteClickListener {
        void onFavoriteClick(String itemName, int position);
    }

    Context context;
    List<DataSnapshot> list = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener longClickListener;
    private OnFavoriteClickListener favoriteClickListener;
    public CollectionActivityAdapter(Context context ,@NonNull FirebaseRecyclerOptions<DataSnapshot> options) {
        super(options);
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }
    public void setOnPinClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }
    public List<DataSnapshot> getList() {
        return list;
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectionActivityVH holder, int position, @NonNull DataSnapshot model) {
        holder.name.setText(model.getKey());
        list.add(model);

        holder.itemView.setOnClickListener(view -> {
            String itemName = holder.name.getText().toString();
            if (itemClickListener != null) {
                itemClickListener.onItemClick(itemName);
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            String itemName = holder.name.getText().toString();
            if (longClickListener != null) {
                longClickListener.onItemLongClick(itemName);
            }
            return true;
        });

        holder.favorite.setOnClickListener(view -> {
            String itemName = holder.name.getText().toString();
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(itemName, position);
            }
            if (holder.favorite.getForeground().equals(AppCompatResources.getDrawable(context, R.drawable.ic_favorite)) || holder.favorite.isChecked()) {
                holder.favorite.setForeground(AppCompatResources.getDrawable(context, R.drawable.ic_star_outline));
                holder.favorite.setChecked(false);
            } else if (holder.favorite.getForeground().equals(AppCompatResources.getDrawable(context, R.drawable.ic_star_outline)) || !holder.favorite.isChecked()) {
                holder.favorite.setForeground(AppCompatResources.getDrawable(context, R.drawable.ic_favorite));
                holder.favorite.setChecked(true);
            }
        });

        boolean pinned = Boolean.TRUE.equals(model.child("favorite").getValue(Boolean.class));
        favoriteItem(holder, pinned);
    }

    public void favoriteItem(CollectionActivityVH holder, boolean pinned) {
        holder.favorite.setForeground(pinned ? AppCompatResources.getDrawable(context, R.drawable.ic_favorite)
                                             : AppCompatResources.getDrawable(context, R.drawable.ic_star_outline));
        holder.favorite.setChecked(pinned);
    }

    @NonNull
    @Override
    public CollectionActivityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.collection_activity_item, parent, false);
        return new CollectionActivityVH(view);
    }
}
