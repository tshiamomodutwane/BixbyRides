package com.example.bixbyrides;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Find the menuIcon
        ImageButton menuIcon = findViewById(R.id.menuIcon);

        // Set a click listener on the menuIcon to show the popup menu
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(AboutUsActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.account) {
                    // Open Account Activity
                    Intent accountIntent = new Intent(AboutUsActivity.this, AccountActivity.class);
                    startActivity(accountIntent);
                    return true;
                } else if (item.getItemId() == R.id.about_us) {
                    // Open About Us Activity (you're already here)
                    return true;
                } else if (item.getItemId() == R.id.contact_us) {
                    // Open Contact Us Activity
                    Intent contactIntent = new Intent(AboutUsActivity.this, ContactUsActivity.class);
                    startActivity(contactIntent);
                    return true;
                }
                return false;
            }
        });

        // Show the popup menu
        popupMenu.show();
    }
}
