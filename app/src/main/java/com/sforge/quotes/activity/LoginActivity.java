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
import com.google.firebase.auth.FirebaseUser;
import com.sforge.quotes.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView register;
    private TextView forgotPassword;

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        defineViews();

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
        forgotPassword.setOnClickListener(view -> startActivity(new Intent(this, ForgottenPassword.class)));

        loginButton.setOnClickListener(view -> userLogin());
    }

    private void userLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()){
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please Enter a Valid Email!");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()){
            passwordEditText.setError("Password is required!");
            passwordEditText.requestFocus();
            return;
        }
        if(password.length() < 6){
            passwordEditText.setError("Password has to Have at Least 6 Characters!");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (Objects.requireNonNull(user).isEmailVerified()){
                    finish();
                    startActivity(new Intent(this, UserProfile.class));
                }
                else {
                    user.sendEmailVerification();
                    Toast.makeText(this, "An Email Link has Been sent. Please Verify your Email Address.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    progressBar.setVisibility(View.GONE);
                }
            }
            else {
                Toast.makeText(this, "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void defineViews() {
        register = findViewById(R.id.loginRegister);
        forgotPassword = findViewById(R.id.loginForgotPassword);
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.loginProgressBar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}