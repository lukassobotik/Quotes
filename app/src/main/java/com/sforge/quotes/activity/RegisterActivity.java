package com.sforge.quotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.sforge.quotes.R;
import com.sforge.quotes.entity.User;
import com.sforge.quotes.repository.UserRepository;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView banner;
    private EditText username, email, password;
    private Button register;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        defineViews();

        mAuth = FirebaseAuth.getInstance();

        banner.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        register.setOnClickListener(view -> registerUser());
    }

    public void registerUser() {
        String sUsername = username.getText().toString().trim().toLowerCase();
        String sEmail = email.getText().toString().trim();
        String sPassword = password.getText().toString().trim();

        if (sUsername.isEmpty()) {
            username.setError("Username is Required!");
            username.requestFocus();
            return;
        }
        if (sEmail.isEmpty()) {
            email.setError("Email is Required!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Enter a Valid Email Address!");
            email.requestFocus();
            return;
        }
        if (sPassword.isEmpty()) {
            password.setError("Password is Required!");
            password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            password.setError("Password has to Have at Least 6 Characters!");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = new User(sUsername, sEmail);

                new UserRepository().getDatabaseReference()
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .setValue(user)
                        .addOnCompleteListener(task1 -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Something went Wrong.", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        });
            } else {
                Toast.makeText(this, "Something went Wrong. Couldn't Create The User.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void defineViews() {
        banner = findViewById(R.id.registerQuotesTitle);
        username = findViewById(R.id.registerUsername);
        email = findViewById(R.id.registerEmailEditText);
        password = findViewById(R.id.registerPasswordEditText);
        register = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.registerProgressBar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}