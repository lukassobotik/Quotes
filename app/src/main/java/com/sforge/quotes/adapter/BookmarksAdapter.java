package com.sforge.quotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.UserCollectionRepository;
import com.sforge.quotes.view.CollectionsVH;

import java.util.ArrayList;
import java.util.List;

public class BookmarksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<String> list = new ArrayList<>();
    Quote quote = new Quote();
    String shortQuote;

    public BookmarksAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<String> collectionNames) {
        list.clear();
        list.addAll(collectionNames);
    }

    /**
     * @param quoteToAdd is the quote to add to the collection
     */
    public void setQuote(Quote quoteToAdd) {
        quote = quoteToAdd;
    }

    public List<String> getList() {
        return list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.collection_item, parent, false);
        return new CollectionsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CollectionsVH vh = (CollectionsVH) holder;
        String collectionName = list.get(position);
        vh.name.setText(collectionName);

        holder.itemView.setOnClickListener(view -> {
            String collection = vh.name.getText().toString().trim();
            shortQuote = quote.getQuote();

            if (quote.getQuote().length() > 15) {
                shortQuote = quote.getQuote().substring(0, 15);
            }

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                UserCollectionRepository collectionRepository = new UserCollectionRepository(FirebaseAuth.getInstance().getCurrentUser().getUid(), collection);

                collectionRepository.getDatabaseReference()
                        .orderByChild("quote")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean doesTheQuoteExist = false;
                            for (DataSnapshot child : snapshot.getChildren()) {
                                String quoteKey = child.getKey();
                                Quote childQuote = child.getValue(Quote.class);

                                if (childQuote != null
                                        && childQuote.getQuote().equals(quote.getQuote())
                                        && childQuote.getAuthor().equals(quote.getAuthor())) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("\"" + shortQuote + "\"" + " Already Exists in " + "\"" + collection + "\"");
                                    builder.setMessage("Do you want to delete \"" + quote.getQuote() + "\" from " + "\"" + collection + "\"" + "?");
                                    builder.setPositiveButton("Yes", (dialogInterface, i) -> collectionRepository.remove(quoteKey));
                                    builder.setNegativeButton("No", (dialogInterface, i) -> {});
                                    builder.create().show();
                                    doesTheQuoteExist = true;
                                }
                            }
                            if (!doesTheQuoteExist) {
                                collectionRepository.add(quote);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}