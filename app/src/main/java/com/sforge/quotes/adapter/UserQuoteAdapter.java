package com.sforge.quotes.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.activity.MainActivity;
import com.sforge.quotes.fragment.UserProfileFragment;
import com.sforge.quotes.dialog.CollectionsDialog;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.view.UserQuoteVH;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserQuoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<Quote> list = new ArrayList<>();

    /**
     * Constructor of this Adapter
     * @param context the Activity data is displayed in
     */
    public UserQuoteAdapter(Context context){
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
        View view = LayoutInflater.from(context).inflate(R.layout.usr_quotes_item, parent, false);
        return new UserQuoteVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserQuoteVH vh = (UserQuoteVH) holder;
        Quote quote = list.get(position);
        vh.textQuote.setText(quote.getQuote());
        vh.textAuthor.setText(quote.getAuthor());

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

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
        vh.delete.setOnClickListener(view -> {
            deleteItem(holder, Objects.requireNonNull(auth));
        });

        if (context instanceof MainActivity) {
            Fragment currentFragment = ((MainActivity) context).getCurrentFragment();
            if (currentFragment instanceof UserProfileFragment && auth != null) {
                vh.delete.setVisibility(View.VISIBLE);
                holder.itemView.setOnLongClickListener(view -> {
                    deleteItem(holder, auth);
                    return false;
                });
            }
        } else {
            vh.delete.setVisibility(View.GONE);
        }
    }

    private void deleteItem(RecyclerView.ViewHolder holder, FirebaseUser auth) {
        String itemQuote = ((UserQuoteVH) holder).textQuote.getText().toString();
        String itemAuthor = ((UserQuoteVH) holder).textAuthor.getText().toString();
        QuoteRepository quoteRepository = new QuoteRepository();
        UserQuoteRepository userQuoteRepository = new UserQuoteRepository(auth.getUid());

        //get and delete the Quote ID
        quoteRepository.getDatabaseReference().orderByChild("quote").equalTo(itemQuote).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String quoteKey = child.getKey();
                    Quote childQuote = child.getValue(Quote.class);

                    if (childQuote != null && childQuote.getAuthor().equals(itemAuthor) && childQuote.getUser().equals(auth.getUid())) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setTitle("Delete " + itemQuote + "?");
                        builder.setMessage("Are you sure you want to delete \"" + itemQuote + "\"? \n \n"  + context.getResources().getString(R.string.delete_quote_disclaimer));
                        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                            //delete the quote from "/Quotes/" path
                            quoteRepository.remove(quoteKey);
                            //delete the quote from "/Users/uid/User Quotes/" path
                            userQuoteRepository.getDatabaseReference().orderByChild("quote").equalTo(itemQuote).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        String userQuotesKey = child.getKey();
                                        userQuoteRepository.remove(userQuotesKey);
                                        Toast.makeText(context, "Quote has been successfully deleted.", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Failed To Retrieve Data. " + error, Toast.LENGTH_LONG).show();
                                }
                            });
                        });
                        builder.setNegativeButton("No", (dialogInterface, i) -> {
                        });
                        builder.create().show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed To Retrieve Data. " + error, Toast.LENGTH_LONG).show();
            }
        });
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
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1).getKey();
    }
}
