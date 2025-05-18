package com.example.bixbyrides;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrackerPageActivity extends AppCompatActivity {

    private WebView mapWebView;
    private ImageButton menuIcon;
    private Button shareLocationButton;

    private static final String ROUTING_API_KEY = "ca69539cfcb64c7483657381579e340b";  // Your API Key
    private static final String MAP_TILES_API_KEY = "bfe245372bf148f6998650c34d7cbf30"; // Replace with your Map Tiles API key

    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_page);

        mapWebView = findViewById(R.id.mapWebView);
        menuIcon = findViewById(R.id.menuIcon);
        shareLocationButton = findViewById(R.id.shareLocationButton);

        // Retrieve coordinates from the Intent
        Intent intent = getIntent();
        startLatitude = intent.getDoubleExtra("fromLat", 0.0);
        startLongitude = intent.getDoubleExtra("fromLon", 0.0);
        endLatitude = intent.getDoubleExtra("toLat", 0.0);
        endLongitude = intent.getDoubleExtra("toLon", 0.0);

        // WebView setup
        mapWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load map tiles and route
        loadMap();

        // Set up menu icon click listener
        menuIcon.setOnClickListener(this::showPopupMenu);

        // Set up shareLocationButton click listener
        shareLocationButton.setOnClickListener(v -> shareLocation());
    }

    private void loadMap() {
        // URL for Geoapify Routing API
        String routingUrl = "https://api.geoapify.com/v1/routing?waypoints=" +
                startLatitude + "," + startLongitude + "%7C" + endLatitude + "," + endLongitude +
                "&mode=drive&apiKey=" + ROUTING_API_KEY;

        // Use ExecutorService to fetch route data asynchronously
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String result = fetchRouteData(routingUrl);
            runOnUiThread(() -> {
                if (result != null) {
                    // Load the route onto the map with map tiles
                    String mapUrl = "https://maps.geoapify.com/v1/staticmap?style=osm-bright" +
                            "&width=600&height=400&zoom=14" +
                            "&center=lonlat:" + startLongitude + "," + startLatitude +
                            "&apiKey=" + MAP_TILES_API_KEY;
                    mapWebView.loadUrl(mapUrl);
                } else {
                    Log.e("TrackerPageActivity", "Failed to fetch route data.");
                }
            });
        });
    }

    private String fetchRouteData(String url) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body() != null ? response.body().string() : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(TrackerPageActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.account) {
                startActivity(new Intent(TrackerPageActivity.this, AccountActivity.class));
                return true;
            } else if (item.getItemId() == R.id.about_us) {
                startActivity(new Intent(TrackerPageActivity.this, AboutUsActivity.class));
                return true;
            } else if (item.getItemId() == R.id.contact_us) {
                startActivity(new Intent(TrackerPageActivity.this, ContactUsActivity.class));
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void shareLocation() {
        String message = "Hey, I'm currently at this location: https://maps.google.com/?q=" + startLatitude + "," + startLongitude;
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(sendIntent, "Share location using:"));
    }
}
