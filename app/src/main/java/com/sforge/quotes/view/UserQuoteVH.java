package com.sforge.quotes.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sforge.quotes.R;

public class UserQuoteVH extends RecyclerView.ViewHolder {
    public TextView textQuote, textAuthor;
    public UserQuoteVH(@NonNull View itemView) {
        super(itemView);
        textQuote = itemView.findViewById(R.id.usrTextQuote);
        textAuthor = itemView.findViewById(R.id.usrTextAuthor);
    }
}
