package com.sforge.quotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
    public interface OnPinClickListener {
        void onPinClick(String itemName, int position);
    }

    Context context;
    List <CollectionActivityVH> viewHolders = new ArrayList<>();
    List<DataSnapshot> list = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener longClickListener;
    private OnPinClickListener pinClickListener;
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
    public void setOnPinClickListener(OnPinClickListener listener) {
        this.pinClickListener = listener;
    }
    public List<DataSnapshot> getList() {
        return list;
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectionActivityVH holder, int position, @NonNull DataSnapshot model) {
        viewHolders.add(holder);
        holder.name.setText(model.getKey());
        list.add(model);
        UserBookmarksRepository userBookmarksRepository = new UserBookmarksRepository(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

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

        holder.pin.setOnClickListener(view -> {
            String itemName = holder.name.getText().toString();
            if (pinClickListener != null) {
                pinClickListener.onPinClick(itemName, position);
            }
        });

        boolean pinned = Boolean.TRUE.equals(model.child("pinned").getValue(Boolean.class));
        pinItem(position, pinned);
    }

    public void pinItem(int position, boolean pinned) {
        viewHolders.get(position).pin.setForeground(pinned ? AppCompatResources.getDrawable(context, R.drawable.ic_pin)
                                 : AppCompatResources.getDrawable(context, R.drawable.ic_pin_outline));
    }

    public void clearViewHolders() {
        viewHolders.clear();
    }

    @NonNull
    @Override
    public CollectionActivityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.collection_activity_item, parent, false);
        return new CollectionActivityVH(view);
    }
}
