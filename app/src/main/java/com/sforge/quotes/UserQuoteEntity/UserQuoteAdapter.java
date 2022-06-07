package com.sforge.quotes.UserQuoteEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sforge.quotes.QuoteEntity.Quote;
import com.sforge.quotes.R;

import java.util.ArrayList;
import java.util.List;

public class UserQuoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<Quote> list = new ArrayList<>();
    public UserQuoteAdapter(Context context){
        this.context = context;
    }
    public void setItems(List<Quote> quote){
        list.clear();
        list.addAll(quote);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usr_quotes_item, parent, false);
        return new UserQuoteVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserQuoteVH vh = (UserQuoteVH) holder;
        Quote quote = list.get(position);
        vh.textQuote.setText(quote.getQuote());
        vh.textAuthor.setText(quote.getAuthor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
