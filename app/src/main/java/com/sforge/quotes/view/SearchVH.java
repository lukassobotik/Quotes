package com.sforge.quotes.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sforge.quotes.R;

public class SearchVH extends RecyclerView.ViewHolder {
    public TextView textQuote, textAuthor;
    public Button bookmark, share;

    public SearchVH(@NonNull View itemView) {
        super(itemView);
        textQuote = itemView.findViewById(R.id.searchTextQuote);
        textAuthor = itemView.findViewById(R.id.searchTextAuthor);
        bookmark = itemView.findViewById(R.id.searchBookmarkButton);
        share = itemView.findViewById(R.id.searchShareButton);
    }
}
