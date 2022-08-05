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
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.view.CollectionActivityVH;
import com.sforge.quotes.view.QuoteVH;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchAdapter extends FirebaseRecyclerAdapter<Quote, QuoteVH> {

    Context context;
    List<Quote> list = new ArrayList<>();
    /**
     * @param context calling Activity
     * @param options FirebaseRecyclerOptions to determine what quotes to show
     */
    public SearchAdapter(Context context, @NonNull FirebaseRecyclerOptions<Quote> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull QuoteVH holder, int position, @NonNull Quote model) {
        holder.textQuote.setText(model.getQuote());
        holder.textAuthor.setText(model.getAuthor());
        list.add(model);
    }

    @NonNull
    @Override
    public QuoteVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quote_item, parent, false);
        return new QuoteVH(view);
    }
}
