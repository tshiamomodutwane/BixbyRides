package com.example.bixbyrides;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.ComponentActivity; // Changed to ComponentActivity for modern usage
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RideHistoryActivity extends AppCompatActivity {

    private RecyclerView rideHistoryRecyclerView;
    private RideHistoryAdapter rideHistoryAdapter;
    private List<RideHistoryEntry> rideHistoryList;
    private DatabaseHelper databaseHelper;
    private ImageButton menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge layout
        EdgeToEdge.enable(this);

        // Set the content view
        setContentView(R.layout.activity_ride_history);

        // Initialize RecyclerView and DatabaseHelper
        rideHistoryRecyclerView = findViewById(R.id.rideHistoryRecyclerView);
        databaseHelper = new DatabaseHelper(this);

        // Prepare ride history data
        loadRideHistory();

        // Set up RecyclerView
        rideHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rideHistoryAdapter = new RideHistoryAdapter(rideHistoryList);
        rideHistoryRecyclerView.setAdapter(rideHistoryAdapter);

        // Apply window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(rideHistoryRecyclerView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadRideHistory() {
        // Retrieve rides for the logged-in user
        int userId = 1;
        rideHistoryList = new ArrayList<>();
        Cursor cursor = databaseHelper.getRidesByUserID(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String startLocation = cursor.getString(cursor.getColumnIndex("startLocation"));
                String endLocation = cursor.getString(cursor.getColumnIndex("endLocation"));
                double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
                double price = cursor.getDouble(cursor.getColumnIndex("price"));
                String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));

                RideHistoryEntry entry = new RideHistoryEntry(startLocation, endLocation, distance, price, timestamp);
                rideHistoryList.add(entry);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(RideHistoryActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.account) {
                startActivity(new Intent(RideHistoryActivity.this, AccountActivity.class));
                return true;
            } else if (id == R.id.about_us) {
                startActivity(new Intent(RideHistoryActivity.this, AboutUsActivity.class));
                return true;
            } else if (id == R.id.contact_us) {
                startActivity(new Intent(RideHistoryActivity.this, ContactUsActivity.class));
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }
}
