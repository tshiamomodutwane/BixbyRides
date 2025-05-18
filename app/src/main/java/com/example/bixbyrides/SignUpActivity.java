package com.example.bixbyrides;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private TextView signInTextView;

    // DatabaseHelper instance
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initializing DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // linking UI elements to the backend
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        signInTextView = findViewById(R.id.signInTextView);

        // Sign Up Button Click Listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();  // Get phone number
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (validateInputs(fullName, email, phone, password, confirmPassword)) {
                    registerUser(fullName, email, phone, password);
                } else {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign In Text Click Listener
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(signInIntent); // Start the SignInActivity
                finish(); // This will close the SignUpActivity and go back to the previous one
            }
        });
    }

    private boolean validateInputs(String fullName, String email, String phone, String password, String confirmPassword) {
        // Basic validation
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Email format validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Phone number format validation (10 digits)
        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Password matching validation
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Password strength validation
        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Password must be at least 8 characters long, contain uppercase, lowercase, numbers, and special characters", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    // Password strength check
    private boolean isPasswordStrong(String password) {
        // Check password length
        if (password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check for at least one number
        if (!password.matches(".*[0-9].*")) {
            return false;
        }

        // Check for at least one special character
        if (!password.matches(".*[!@#\\$%\\^&\\*\\(\\)_\\+\\-=\\[\\]{}|;':,\\./<>?].*")) {
            return false;
        }

        return true;
    }



    private void registerUser(String fullName, String email, String phone, String password) {
        // Check if the user already exists in the database by email
        if (dbHelper.getUserByEmail(email).getCount() > 0) {
            Toast.makeText(SignUpActivity.this, "User already exists with this email", Toast.LENGTH_SHORT).show();
        } else {
            // Add user to the SQLite database
            long result = dbHelper.addUser(fullName, email, phone, password);
            if (result != -1) {
                Toast.makeText(SignUpActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                // Navigate to HomePageActivity after successful registration
                Intent intent = new Intent(SignUpActivity.this, HomePageActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
