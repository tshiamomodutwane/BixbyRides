package com.example.bixbyrides;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RideSelector extends AppCompatActivity {

    private static final String TAG = "RideSelector";
    private Spinner rideTypeSpinner;
    private Button selectRideButton;

    // Coordinates and distance
    private double fromLat, fromLon, toLat, toLon;
    private double distanceInKm;
    private int userID = 1;
    private String fromLocation, toLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_selector);

        // Initialize views
        rideTypeSpinner = findViewById(R.id.rideTypeSpinner);
        selectRideButton = findViewById(R.id.selectRideButton);

        // Set adapter for Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ride_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rideTypeSpinner.setAdapter(adapter);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            fromLat = intent.getDoubleExtra("fromLat", Double.NaN);
            fromLon = intent.getDoubleExtra("fromLon", Double.NaN);
            toLat = intent.getDoubleExtra("toLat", Double.NaN);
            toLon = intent.getDoubleExtra("toLon", Double.NaN);
            fromLocation = intent.getStringExtra("fromLocation");
            toLocation = intent.getStringExtra("toLocation");

            if (Double.isNaN(fromLat) || Double.isNaN(fromLon) || Double.isNaN(toLat) || Double.isNaN(toLon)) {
                Toast.makeText(this, "Invalid location data received.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Received invalid coordinates from Intent");
                finish();  // Exit activity if coordinates are invalid
                return;
            } else {
                // Calculate the distance in kilometers
                distanceInKm = calculateDistance(fromLat, fromLon, toLat, toLon);
                Log.d(TAG, "Distance calculated: " + distanceInKm + " km");
            }
        } else {
            Log.e(TAG, "No Intent data received");
        }

        // Set button click listener
        selectRideButton.setOnClickListener(v -> {
            String rideType = rideTypeSpinner.getSelectedItem().toString();
            if (distanceInKm == 0) {
                Toast.makeText(RideSelector.this, "Please enter a valid distance.", Toast.LENGTH_SHORT).show();
            } else {
                double price = calculatePrice(rideType, distanceInKm);
                showPaymentDialog(rideType, price);
            }
        });
    }

    private double calculateDistance(double fromLat, double fromLon, double toLat, double toLon) {
        int EARTH_RADIUS = 6371;
        double latDistance = Math.toRadians(toLat - fromLat);
        double lonDistance = Math.toRadians(toLon - fromLon);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(fromLat)) * Math.cos(Math.toRadians(toLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private double calculatePrice(String rideType, double distance) {
        double ratePerKm;
        switch (rideType) {
            case "BixbyStandard":
                ratePerKm = 5.0;
                break;
            case "BixbyPremium":
                ratePerKm = 8.0;
                break;
            case "BixbyLuxury":
                ratePerKm = 12.0;
                break;
            default:
                ratePerKm = 5.0;
        }
        return ratePerKm * distance;
    }

    private void showPaymentDialog(String rideType, double price) {
        String roundedPrice = String.format("%.2f", price);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Ride");
        builder.setMessage("Ride Type: " + rideType + "\nPrice: " + roundedPrice + " ZAR");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            saveRideDetails(rideType, price);
            Intent intent = new Intent(RideSelector.this, TrackerPageActivity.class);
            intent.putExtra("fromLat", fromLat);
            intent.putExtra("fromLon", fromLon);
            intent.putExtra("toLat", toLat);
            intent.putExtra("toLon", toLon);
            intent.putExtra("fromLocation", fromLocation);
            intent.putExtra("toLocation", toLocation);
            intent.putExtra("distanceInKm", distanceInKm);
            intent.putExtra("price", price);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveRideDetails(String rideType, double price) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long result = dbHelper.addRide(userID, fromLocation, toLocation, distanceInKm, price);

        if (result != -1) {
            Log.d(TAG, "Ride saved successfully. Ride ID: " + result);
            Toast.makeText(this, "Ride saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Failed to save ride");
            Toast.makeText(this, "Failed to save ride", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(RideSelector.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.account) {
                    startActivity(new Intent(RideSelector.this, AccountActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.about_us) {
                    startActivity(new Intent(RideSelector.this, AboutUsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.contact_us) {
                    startActivity(new Intent(RideSelector.this, ContactUsActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }
}
