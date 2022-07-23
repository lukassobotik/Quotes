package com.sforge.quotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.activity.UserProfile;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.view.UserQuoteVH;

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

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

        if (context.getClass().getSimpleName().equals(UserProfile.class.getSimpleName()) && auth != null) {
            holder.itemView.setOnLongClickListener(view -> {
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Delete " + itemQuote + "?");
                                builder.setMessage("Are you sure you want to delete \"" + itemQuote + "\"? "  + context.getResources().getString(R.string.delete_quote_disclaimer));
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
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
