package com.sforge.quotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.sforge.quotes.R;

import java.util.Objects;

public class ForgottenPassword extends AppCompatActivity {

    private EditText emailEditText;
    private Button sendButton;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        emailEditText = findViewById(R.id.forgotPasswordEmailEditText);
        sendButton = findViewById(R.id.forgotPasswordButton);
        progressBar = findViewById(R.id.forgotPasswordProgressBar);

        auth = FirebaseAuth.getInstance();

        sendButton.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword(){
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()){
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please enter a valid Email Address!");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(this, "An Email to Reset Your Password has Been Sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something went Wrong. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}