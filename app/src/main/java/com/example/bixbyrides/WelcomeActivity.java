package com.example.bixbyrides;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button signInButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the buttons
        signInButton = findViewById(R.id.signInButtonOutline);
        signUpButton = findViewById(R.id.signUpButtonFilled);  // Initialize the Sign Up button

        // Set OnClickListener for Sign In Button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SignInActivity when clicked
                Intent signInIntent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(signInIntent); // Start the SignInActivity
            }
        });

        // Set OnClickListener for Sign Up Button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SignUpActivity whe  clickdd
                Intent signUpIntent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(signUpIntent); // Start the SignUpActivity
            }
        });
    }
}
