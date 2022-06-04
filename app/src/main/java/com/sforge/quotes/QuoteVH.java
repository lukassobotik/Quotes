package com.sforge.quotes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuoteVH extends RecyclerView.ViewHolder {
    public TextView textQuote, textAuthor;
    public QuoteVH(@NonNull View itemView) {
        super(itemView);
        textQuote = itemView.findViewById(R.id.textQuote);
        textAuthor = itemView.findViewById(R.id.textAuthor);
    }
}
