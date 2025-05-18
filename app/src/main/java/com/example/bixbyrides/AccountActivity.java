package com.example.bixbyrides;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText phoneNumberEditText;
    private EditText emailEditText;
    private TextView fullNameTextView;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        databaseHelper = new DatabaseHelper(this);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        fullNameTextView = findViewById(R.id.fullNameTextView);

        // Assuming user ID is passed through Intent
        userId = getIntent().getIntExtra("USER_ID", -1);

        loadUserData(userId);

        Button updateInfoButton = findViewById(R.id.updateInfoButton);
        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        Button deleteAccountButton = findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteUserAccount(); // Call the confirmation method
            }
        });

        Button viewRideHistoryButton = findViewById(R.id.viewRideHistoryButton);
        viewRideHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewRideHistory();
            }
        });
    }

    private void loadUserData(int userId) {
        Cursor cursor = databaseHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            // Safely retrieve each field
            int fullNameIndex = cursor.getColumnIndex("fullName");
            int emailIndex = cursor.getColumnIndex("email");
            int phoneNumberIndex = cursor.getColumnIndex("phoneNumber");

            fullNameTextView.setText(fullNameIndex != -1 ? cursor.getString(fullNameIndex) : "");
            emailEditText.setText(emailIndex != -1 ? cursor.getString(emailIndex) : "");
            phoneNumberEditText.setText(phoneNumberIndex != -1 ? cursor.getString(phoneNumberIndex) : "");

            cursor.close();
        } else {
            Toast.makeText(this, cursor == null ? "Cursor is null" : "No user found for this ID", Toast.LENGTH_SHORT).show();
        }
    }



    private void updateUserInfo() {
        String email = emailEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        boolean isUpdated = databaseHelper.updateUser(userId, email, phoneNumber);
        if (isUpdated) {
            Toast.makeText(this, "User information updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update user information", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteUserAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserAccount(); // Call the method to delete the account
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Dismiss the dialog if the user selects "No"
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show(); // Show the dialog
    }

    private void deleteUserAccount() {
        boolean isDeleted = databaseHelper.deleteUser(userId);
        if (isDeleted) {
            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccountActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewRideHistory() {
        Intent intent = new Intent(AccountActivity.this, RideHistoryActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }
}
