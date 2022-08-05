package com.sforge.quotes.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.ImageAdapter;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.entity.User;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

public class UserProfile extends AppCompatActivity {

    private Button profileSettings, showEmail, changeBackground, backButton, layoutBackButton;

    private String email = "";

    List<Quote> usrQuotes = new ArrayList<>();

    QuoteRepository quoteRepository;
    UserQuoteAdapter usrAdapter;
    private RecyclerView usrQuotesRV, changeBackgroundRV;
    LinearLayout backgroundRVLinearLayout;

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

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            for (UserInfo profile : user.getProviderData()) {
                                String uEmail = profile.getEmail();
                                emailTV.setText(uEmail);
                            }
                        }
                        usernameTV.setText(stringBuilder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error){
                    Toast.makeText(UserProfile.this, "Couldn't Retrieve the User Info. " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        LinearLayoutManager usrManager = new LinearLayoutManager(this);
        usrAdapter = new UserQuoteAdapter(this);
        usrQuotesRV = findViewById(R.id.usrQuotes);
        usrQuotesRV.setAdapter(usrAdapter);
        usrQuotesRV.setLayoutManager(usrManager);
        quoteRepository = new QuoteRepository();
        backButton = findViewById(R.id.profileBackButton);
        backButton.setOnClickListener(view -> finish());
        layoutBackButton = findViewById(R.id.mainBackButton);
        layoutBackButton.setVisibility(View.GONE);
        changeBackground = findViewById(R.id.profileChangeBackground);
        changeBackground.setVisibility(View.GONE);
        changeBackgroundRV = findViewById(R.id.changeBackgroundRecyclerView);
        changeBackgroundRV.setVisibility(View.GONE);
        backgroundRVLinearLayout = findViewById(R.id.changeBackgroundRVLinearLayout);
        backgroundRVLinearLayout.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userID = user.getUid();
            new UserRepository().getDatabaseReference()
                    .child(userID)
                    .addListenerForSingleValueEvent(onDataChangeListener.apply(emailTV, usernameTV));
        }

        createSettingsMenu();

        getUserQuotes();
    }

    public void getUserQuotes() {
        UserQuoteRepository userQuotesReference = new UserQuoteRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userQuotesReference
                .getAll()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usrQuotes = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Quote quote = data.getValue(Quote.class);
                            usrQuotes.add(quote);
                        }
                        usrAdapter.setItems(usrQuotes);
                        usrAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserProfile.this, "Couldn't Retrieve the Quotes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void createSettingsMenu() {
        final AtomicBoolean isSettingMenuOpen = new AtomicBoolean(false);
        final AtomicBoolean isEmailShown = new AtomicBoolean(false);
        final AtomicBoolean isBackgroundSettingShown = new AtomicBoolean(false);
        profileSettings.setOnClickListener(view -> {
            if (!isSettingMenuOpen.get()) {
//                showEmail.setVisibility(View.VISIBLE);
//                showEmail.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
                changeBackground.setVisibility(View.VISIBLE);
                changeBackground.startAnimation(
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
                isSettingMenuOpen.set(true);
            } else {
//                showEmail.setVisibility(View.GONE);
//                showEmail.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                changeBackground.setVisibility(View.GONE);
                changeBackground.startAnimation(
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                backgroundRVLinearLayout.setVisibility(View.GONE);
                backgroundRVLinearLayout.startAnimation(
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                changeBackgroundRV.setVisibility(View.GONE);
                changeBackgroundRV.startAnimation(
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                isBackgroundSettingShown.set(false);
                isSettingMenuOpen.set(false);
            }
        });

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

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        ImageAdapter adapter = new ImageAdapter(this);
        List<Drawable> images = new ArrayList<>();
        images.add(getResources().getDrawable(R.drawable.rsz_blue_sky_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_bridge_in_forest_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_night_city_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_dark_mountains_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_bridge_in_forest_2, null));
        images.add(getResources().getDrawable(R.drawable.rsz_forest_1, null));
        adapter.setItems(images);
        changeBackgroundRV.setLayoutManager(manager);
        changeBackgroundRV.setAdapter(adapter);

        changeBackground.setOnClickListener(view -> {
            if (!isBackgroundSettingShown.get()) {
                backgroundRVLinearLayout.setVisibility(View.VISIBLE);
                changeBackgroundRV.setVisibility(View.VISIBLE);
                isBackgroundSettingShown.set(true);
            } else {
                backgroundRVLinearLayout.setVisibility(View.GONE);
                changeBackgroundRV.setVisibility(View.GONE);
                isBackgroundSettingShown.set(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
