package com.example.parkin1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class settingoperatorActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ImageView img1, img2, img3, img4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Apply the saved language preference
        applyLanguagePreference();
        setContentView(R.layout.activity_settingoperator);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settingoperator);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        Button languageButton = findViewById(R.id.button2);
        Button permissionButton = findViewById(R.id.button3);
        ImageButton backButton = findViewById(R.id.btn1);
        Button promosButton = findViewById(R.id.button7);
        Button discountButton = findViewById(R.id.button8);
        Button logoutButton = findViewById(R.id.button4); // Assuming you have a button with this id in your layout

        // Social Media ImageViews
        img1 = findViewById(R.id.insta);
        img2 = findViewById(R.id.facebook);
        img3 = findViewById(R.id.inn);
        img4 = findViewById(R.id.youtube);

        // Set onClick listeners
        languageButton.setOnClickListener(v -> showLanguageDialog());
        permissionButton.setOnClickListener(v -> openAppPermissionSettings());
        backButton.setOnClickListener(v -> finish());
        promosButton.setOnClickListener(v -> {
            Intent intent = new Intent(settingoperatorActivity.this, PromosActivity.class);
            startActivity(intent);
        });
        discountButton.setOnClickListener(v -> {
            Intent intent = new Intent(settingoperatorActivity.this, DiscountActivity.class);
            startActivity(intent);
        });
        logoutButton.setOnClickListener(v -> logoutUser());

        // Social Media Links
        img1.setOnClickListener(v -> gotoUrl("https://www.instagram.com/sujankhatri612/"));
        img2.setOnClickListener(v -> gotoUrl("https://www.facebook.com/sujan.khatri.923724"));
        img3.setOnClickListener(v -> gotoUrl("https://www.youtube.com/watch?v=i96UO8-GFvw&list=RDi96UO8-GFvw&start_radio=1&ab_channel=TheLocalTrain"));
        img4.setOnClickListener(v -> gotoUrl("https://www.youtube.com/@peacesounds612"));

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Show language change dialog (assuming this method exists)
    private void showLanguageDialog() {
        // Create an array of language options
        final String[] languages = {"English", "Nepali"};

        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language")
                .setItems(languages, (dialog, which) -> {
                    // Handle the language selection
                    String selectedLanguage = languages[which];
                    // Save the selected language to SharedPreferences
                    saveLanguagePreference(selectedLanguage);
                    // Restart the activity to apply the language change
                    recreate(); // Or you can restart the application if needed
                });

        // Show the dialog
        builder.create().show();
    }

    private void saveLanguagePreference(String language) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();
        // You may want to reload the resources or restart the app to apply changes
    }
    private void applyLanguagePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "English");
        Locale locale = new Locale(language.equals("Nepali") ? "ne" : "en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


    // Open application permission settings
    private void openAppPermissionSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    // Open URL in browser
    private void gotoUrl(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    // Logout user and redirect to MainActivity
    private void logoutUser() {
        mAuth.signOut(); // Firebase sign out
        Intent intent = new Intent(settingoperatorActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close the current activity to prevent back navigation
    }
}
