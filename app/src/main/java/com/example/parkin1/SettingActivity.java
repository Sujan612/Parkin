package com.example.parkin1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends BaseActivity {
    BottomNavigationView bottomNavigationView;
    ImageView img1, img2, img3, img4;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        Button languageButton = findViewById(R.id.button2);
        languageButton.setOnClickListener(v -> showLanguageDialog());

        // PERMISSION Button
        Button permissionButton = findViewById(R.id.button3);
        permissionButton.setOnClickListener(v -> openAppPermissionSettings());

        // Find the back button
        ImageButton backButton = findViewById(R.id.btn1);
        backButton.setOnClickListener(v -> finish());

        // Promos button
        Button promosButton = findViewById(R.id.button7);
        promosButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, PromosActivity.class);
            startActivity(intent);
        });

        // Discount button
        Button discountButton = findViewById(R.id.button8);
        discountButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, DiscountActivity.class);
            startActivity(intent);
        });

        // Initialize Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView3);
        bottomNavigationView.setSelectedItemId(R.id.settings);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation) {
                startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.settings) {
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

        // Social Media ImageViews
        img1 = findViewById(R.id.insta);
        img2 = findViewById(R.id.facebook);
        img3 = findViewById(R.id.inn);
        img4 = findViewById(R.id.youtube);

        img1.setOnClickListener(v -> gotoUrl("https://www.instagram.com/sujankhatri612/"));
        img2.setOnClickListener(v -> gotoUrl("https://www.facebook.com/sujan.khatri.923724"));
        img3.setOnClickListener(v -> gotoUrl("https://www.youtube.com/watch?v=i96UO8-GFvw&list=RDi96UO8-GFvw&start_radio=1&ab_channel=TheLocalTrain"));
        img4.setOnClickListener(v -> gotoUrl("https://www.youtube.com/@peacesounds612"));

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Add Logout Button and its functionality
        Button logoutButton = findViewById(R.id.button4);  // Assuming you add a button with id "button_logout" in your layout
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    // Permissions handler
    private void openAppPermissionSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    // Logout user
    private void logoutUser() {
        mAuth.signOut(); // Firebase sign out
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close the current activity to prevent back navigation
    }
}
