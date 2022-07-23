package com.sforge.quotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.activity.CollectionsActivity;
import com.sforge.quotes.activity.MainActivity;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserCollectionRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.view.CollectionActivityVH;
import com.sforge.quotes.view.CollectionsVH;
import com.sforge.quotes.view.UserQuoteVH;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivityAdapter extends FirebaseRecyclerAdapter<DataSnapshot, CollectionActivityVH> {
    Context context;
    List<DataSnapshot> list = new ArrayList<>();
    /**
     * Initialize an adapter that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CollectionActivityAdapter(Context context ,@NonNull FirebaseRecyclerOptions<DataSnapshot> options) {
        super(options);
        this.context = context;
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

            ((CollectionsActivity) context).onClickCalled(itemName);
        });
    }

    @NonNull
    @Override
    public CollectionActivityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.collection_activity_item, parent, false);
        return new CollectionActivityVH(view);
    }
}
