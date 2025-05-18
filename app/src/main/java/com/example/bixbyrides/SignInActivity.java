package com.example.bixbyrides;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;

public class SignInActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton;
    private TextView forgotPasswordText;

    // DatabaseHelper instance
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Linking UI elements to backend
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Sign In Button Click Listener
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (validateInputs(email, password)) {
                    signInUser(email, password);
                } else {
                    Toast.makeText(SignInActivity.this, "Please enter valid email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Forgot Password Click Listener
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ForgotPasswordActivity
                Intent intent = new Intent(SignInActivity.this, ForgotpasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        return !email.isEmpty() && !password.isEmpty(); // Basic validation
    }

    private void signInUser(String email, String password) {
        // Query the database for the user by email and password
        Cursor cursor = dbHelper.getUserByEmailAndPassword(email, password);

        if (cursor != null && cursor.getCount() > 0) {
            // User exists, sign-in successful
            Toast.makeText(SignInActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();

            // Navigate to HomePageActivity after successful login
            Intent intent = new Intent(SignInActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish(); // Close SignInActivity
        } else {
            // Sign-in failed, user not found
            Toast.makeText(SignInActivity.this, "Sign In Failed: Invalid email or password", Toast.LENGTH_SHORT).show();
        }

        // Close the cursor to prevent memory leaks
        if (cursor != null) {
            cursor.close();
        }
    }
}
