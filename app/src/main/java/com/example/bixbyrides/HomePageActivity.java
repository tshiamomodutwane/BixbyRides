// HomePageActivity.java

package com.example.bixbyrides;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePageActivity";
    private AutoCompleteTextView fromEditText;
    private AutoCompleteTextView toEditText;
    private Button searchButton;
    private OkHttpClient client;
    private ImageButton menuIcon;

    private static final String API_KEY = "bfe245372bf148f6998650c34d7cbf30";
    private double fromLat, fromLon, toLat, toLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        fromEditText = findViewById(R.id.fromEditText);
        toEditText = findViewById(R.id.whereEditText);
        searchButton = findViewById(R.id.searchButton);
        menuIcon = findViewById(R.id.menuIcon);

        client = new OkHttpClient().newBuilder().build();

        setupAutocomplete(fromEditText);
        setupAutocomplete(toEditText);

        searchButton.setOnClickListener(v -> {
            String fromLocation = fromEditText.getText().toString();
            String toLocation = toEditText.getText().toString();

            if (fromLocation.isEmpty()) {
                fromEditText.setError("Please enter the starting location.");
                return;
            }
            if (toLocation.isEmpty()) {
                toEditText.setError("Please enter the destination.");
                return;
            }

            fetchCoordinates(fromLocation, true, () ->
                    fetchCoordinates(toLocation, false, () -> {
                        Intent intent = new Intent(HomePageActivity.this, RideSelector.class);
                        intent.putExtra("fromLat", fromLat);
                        intent.putExtra("fromLon", fromLon);
                        intent.putExtra("toLat", toLat);
                        intent.putExtra("toLon", toLon);
                        intent.putExtra("fromLocation", fromEditText.getText().toString());  // Add From Location name
                        intent.putExtra("toLocation", toEditText.getText().toString());  // Add To Location name
                        startActivity(intent);
                    })
            );
        });

        menuIcon.setOnClickListener(this::showPopupMenu);
    }

    private void setupAutocomplete(AutoCompleteTextView editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    fetchAutocompleteSuggestions(s.toString(), editText);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchAutocompleteSuggestions(String query, AutoCompleteTextView editText) {
        String url = "https://api.geoapify.com/v1/geocode/autocomplete?text=" + query + "&apiKey=" + API_KEY;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray features = jsonObject.getJSONArray("features");

                        List<String> suggestions = new ArrayList<>();
                        for (int i = 0; i < features.length(); i++) {
                            JSONObject feature = features.getJSONObject(i);
                            String name = feature.getJSONObject("properties").getString("formatted");
                            suggestions.add(name);
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(HomePageActivity.this, android.R.layout.simple_dropdown_item_1line, suggestions);
                            editText.setAdapter(adapter);
                            editText.showDropDown();
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse autocomplete response", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Failed to fetch autocomplete suggestions", e);
            }
        });
    }

    private void fetchCoordinates(String location, boolean isFromLocation, Runnable callback) {
        String url = "https://api.geoapify.com/v1/geocode/search?text=" + location + "&apiKey=" + API_KEY;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject feature = jsonObject.getJSONArray("features").getJSONObject(0);
                        double lat = feature.getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);
                        double lon = feature.getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);

                        if (isFromLocation) {
                            fromLat = lat;
                            fromLon = lon;
                        } else {
                            toLat = lat;
                            toLon = lon;
                        }

                        runOnUiThread(callback);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse coordinates", e);
                    }
                } else {
                    Log.e(TAG, "Failed to fetch coordinates. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Failed to fetch coordinates", e);
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(HomePageActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.account) {
                    startActivity(new Intent(HomePageActivity.this, AccountActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.about_us) {
                    startActivity(new Intent(HomePageActivity.this, AboutUsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.contact_us) {
                    startActivity(new Intent(HomePageActivity.this, ContactUsActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }
}
