package com.sforge.quotes.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.entity.User;
import com.sforge.quotes.repository.UserRepository;

public class UserProfile extends AppCompatActivity {

    boolean isSettingMenuOpen = false;

    private TextView emailTV, usernameTV;

    private Button profileSettings, showEmail;

    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        emailTV = findViewById(R.id.profileEmail);
        usernameTV = findViewById(R.id.profileUsername);
        profileSettings = findViewById(R.id.profileSettingsButton);
        showEmail = findViewById(R.id.profileShowEmail);
        showEmail.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userID = user.getUid();
            new UserRepository().getDatabaseReference()
                    .child(userID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
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
                    });
        }

        profileSettings.setOnClickListener(view -> {
            if (!isSettingMenuOpen) {
                showEmail.setVisibility(View.VISIBLE);
                isSettingMenuOpen = true;
            } else {
                showEmail.setVisibility(View.GONE);
                isSettingMenuOpen = false;
            }
        });

        final boolean[] isEmailShown = {false};
        showEmail.setOnClickListener(view -> {
            if (!isEmailShown[0]) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Email: ").append(email);
                showEmail.setText(stringBuilder);
                isEmailShown[0] = true;
            } else {
                String s = "Show Email";
                showEmail.setText(s);
                isEmailShown[0] = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}