package com.sforge.quotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    List<Quote> list = new ArrayList<>();
    public QuoteAdapter(Context ctx){
        this.context = ctx;
    }
    public void setItems(List<Quote> quote){
        list.clear();
        list.addAll(quote);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quote_item, parent, false);
        return new QuoteVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QuoteVH vh = (QuoteVH) holder;
        Quote quote = list.get(position);
        vh.textQuote.setText(quote.getQuote());
        vh.textAuthor.setText(quote.getAuthor());
    }

    @Override
    public int getItemCount() {
        int i = 0;
        for (Quote q : list){
            i++;
        }
        return i;
    }
}
