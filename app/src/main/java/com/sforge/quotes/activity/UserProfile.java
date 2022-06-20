package com.sforge.quotes.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.entity.User;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserRepository;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

public class UserProfile extends AppCompatActivity {

    private Button profileSettings, showEmail;

    private String email = "";

    QuoteRepository quoteRepository;
    UserQuoteAdapter usrAdapter;
    private RecyclerView usrQuotesRV;

    private final BiFunction<TextView, TextView, ValueEventListener> onDataChangeListener =
            (emailTV, usernameTV) -> new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        String userEmail = userProfile.getEmail();
                        String username = userProfile.getUsername();
                        email = userEmail;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@").append(username);

                        emailTV.setText(userEmail);
                        usernameTV.setText(stringBuilder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserProfile.this, "Something went Wrong.", Toast.LENGTH_SHORT).show();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        TextView emailTV = findViewById(R.id.profileEmail);
        TextView usernameTV = findViewById(R.id.username);

        profileSettings = findViewById(R.id.profileSettingsButton);
        showEmail = findViewById(R.id.profileShowEmail);
        showEmail.setVisibility(View.GONE);
        usrQuotesRV = findViewById(R.id.usrQuotes);
        quoteRepository = new QuoteRepository();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userID = user.getUid();
            new UserRepository().getDatabaseReference()
                    .child(userID)
                    .addListenerForSingleValueEvent(onDataChangeListener.apply(emailTV, usernameTV));
        }

        createSettingsMenu();

        usrQuotesRV.setAdapter(usrAdapter);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void createSettingsMenu() {
        final AtomicBoolean isSettingMenuOpen = new AtomicBoolean(false);
        profileSettings.setOnClickListener(view -> {
            if (!isSettingMenuOpen.get()) {
                showEmail.setVisibility(View.VISIBLE);
                showEmail.startAnimation(
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
                isSettingMenuOpen.set(true);
            } else {
                showEmail.setVisibility(View.GONE);
                showEmail.startAnimation(
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                isSettingMenuOpen.set(false);
            }
        });

        final AtomicBoolean isEmailShown = new AtomicBoolean(false);
        showEmail.setOnClickListener(view -> {
            if (!isEmailShown.get()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Email: ").append(email);
                showEmail.setText(stringBuilder);
                isEmailShown.set(true);
            } else {
                String s = "Show Email";
                showEmail.setText(s);
                isEmailShown.set(false);
            }
        });
        usrQuotesRV.setAdapter(usrAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
