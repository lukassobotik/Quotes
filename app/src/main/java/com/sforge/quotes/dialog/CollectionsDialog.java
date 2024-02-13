package com.sforge.quotes.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.activity.MainActivity;
import com.sforge.quotes.adapter.BookmarksAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.repository.UserBookmarksRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionsDialog extends AppCompatDialogFragment {

    private RecyclerView recyclerView;
    private CollectionsDialogListener listener;
    private Quote quote;

    public CollectionsDialog(Quote quote) {
        this.quote = quote;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_to_collection, null);

        recyclerView = view.findViewById(R.id.dialogCollectionsList);

        BookmarksAdapter bookmarksAdapter = new BookmarksAdapter(getActivity());
        bookmarksAdapter.setQuote(quote);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserBookmarksRepository bookmarksRepository = new UserBookmarksRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
            List<String> items = new ArrayList<>();
            bookmarksRepository.getAll().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    items.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String text = data.getKey();
                        items.add(text);
                    }
                    bookmarksAdapter.setItems(items);
                    bookmarksAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(bookmarksAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Cannot Access the Database Right Now. " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }

        LinearLayoutManager collectionsManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(collectionsManager);

        builder.setView(view).setTitle("Choose a Collection");

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CollectionsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " Must implement SecondsToLoadDialogListener");
        }
    }

    public interface CollectionsDialogListener{
        void getData(int option);
    }
}
