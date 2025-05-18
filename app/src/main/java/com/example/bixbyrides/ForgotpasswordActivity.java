package com.example.bixbyrides;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotpasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button cancelButton, resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        cancelButton = findViewById(R.id.cancelButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and go back
            }
        });

        // Set click listener for the Reset Password button
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotpasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    //we have not implemented a password reset code
                    Toast.makeText(ForgotpasswordActivity.this, "Password reset link sent to: " + email, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
