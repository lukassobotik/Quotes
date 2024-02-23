package com.sforge.quotes.view;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sforge.quotes.R;

public class QuoteVH extends RecyclerView.ViewHolder {
    public TextView textQuote, textAuthor;
    public Button bookmark;
    public ConstraintLayout itemView;
    public QuoteVH(@NonNull View itemView) {
        super(itemView);
        textQuote = itemView.findViewById(R.id.textQuote);
        textAuthor = itemView.findViewById(R.id.textAuthor);
        bookmark = itemView.findViewById(R.id.quoteBookmarkButton);
        this.itemView = itemView.findViewById(R.id.quoteItemBackground);
    }
}
