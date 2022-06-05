package com.sforge.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    boolean isSettingMenuOpen = false;

    private String userID;

    private TextView emailTV, usernameTV;

    private Button profileSettings, showEmail;

    private String fullEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        emailTV = findViewById(R.id.profileEmail);
        usernameTV = findViewById(R.id.profileUsername);
        profileSettings = findViewById(R.id.profileSettingsButton);
        showEmail = findViewById(R.id.profileShowEmail);
        showEmail.setVisibility(View.GONE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://quotes-30510-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        if (user != null){
            userID = user.getUid();
            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null){
                        String email = userProfile.email;
                        String username = userProfile.username;
                        fullEmail = email;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@").append(username);

                        emailTV.setText(email);
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
            if (!isSettingMenuOpen){
                showEmail.setVisibility(View.VISIBLE);
                isSettingMenuOpen = true;
            } else {
                showEmail.setVisibility(View.GONE);
                isSettingMenuOpen = false;
            }
        });

        final boolean[] isEmailShown = {false};
        showEmail.setOnClickListener(view -> {
            if (!isEmailShown[0]){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Email: ").append(fullEmail);
                showEmail.setText(stringBuilder);
                isEmailShown[0] = true;
            } else {
                showEmail.setText("Show Email");
                isEmailShown[0] = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(UserProfile.this, MainActivity.class));
    }
}