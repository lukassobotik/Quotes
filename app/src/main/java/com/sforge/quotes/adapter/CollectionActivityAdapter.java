package com.sforge.quotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.sforge.quotes.R;
import com.sforge.quotes.view.CollectionActivityVH;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivityAdapter extends FirebaseRecyclerAdapter<DataSnapshot, CollectionActivityVH> {

    public interface OnItemClickListener {
        void onItemClick(String itemName);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(String itemName);
    }

    Context context;
    List<DataSnapshot> list = new ArrayList<>();

    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener longClickListener;
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
    }

    @NonNull
    @Override
    public CollectionActivityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.collection_activity_item, parent, false);
        return new CollectionActivityVH(view);
    }
}
