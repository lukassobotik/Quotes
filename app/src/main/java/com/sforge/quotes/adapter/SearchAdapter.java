package com.sforge.quotes.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sforge.quotes.R;
import com.sforge.quotes.dialog.CollectionsDialog;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.view.SearchVH;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<Quote> list = new ArrayList<>();

    /**
     * Constructor of this Adapter
     * @param context the Activity data is displayed in
     */
    public SearchAdapter(Context context){
        this.context = context;
    }

    /**
     * Common Method used to change the displayed list
     * @param quote is the list of quotes that is supposed to be displayed
     */
    public void setItems(List<Quote> quote){
        list.clear();
        list.addAll(quote);
    }

    /**
     * Common Method for adding more items to the displayed list
     * @param quotes is the list of quotes where you add more items to
     */
    public void addItems(List<Quote> quotes) {
        list.addAll(quotes);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new SearchVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchVH vh = (SearchVH) holder;
        Quote quote = list.get(position);
        if (quote != null) {
            vh.textQuote.setText(quote.getQuote());
            vh.textAuthor.setText(quote.getAuthor());

            vh.bookmark.setOnClickListener(view -> {
                Log.d("cab", "bookmark: " + quote.getQuote() + "    " + quote.getAuthor());
                CollectionsDialog collectionsDialog = new CollectionsDialog(quote);
                collectionsDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "collectionsDialog");
            });
            vh.share.setOnClickListener(view -> {
                Intent myIntent = new Intent (Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                myIntent.putExtra(Intent.EXTRA_TEXT, quote.getQuote() + " - By " + quote.getAuthor());
                context.startActivity(Intent.createChooser(myIntent, "Share using"));
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Method used to load more data from the database
     * @return key of the last quote in the list
     */
    public String getLastItemId() {
        return list.get(list.size() - 1).getKey();
    }
}
