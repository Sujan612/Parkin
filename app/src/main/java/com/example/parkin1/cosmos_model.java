package com.example.parkin1;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class cosmos_model extends AppCompatActivity {

    private static final String LOCATION = "27.65618593181801, 85.3215688432409"; // Cosmos location coordinates
    private Button selectedButton = null;
    private boolean isColorChanged = false;  // Flag to prevent multiple reservations
    private DatabaseReference databaseReference;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosmos_model);

        // Enable edge-to-edge display and handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Reference Firebase database where button states (parking space availability) are stored
        databaseReference = FirebaseDatabase.getInstance().getReference("parkingSpaces");

        // Define parking space buttons (both B and C sections)
        Button[] buttons = new Button[] {
                findViewById(R.id.b_cosmos_1), findViewById(R.id.b_cosmos_2), findViewById(R.id.b_cosmos_3),
                findViewById(R.id.b_cosmos_4), findViewById(R.id.b_cosmos_5), findViewById(R.id.b_cosmos_6),
                findViewById(R.id.b_cosmos_7), findViewById(R.id.b_cosmos_8), findViewById(R.id.b_cosmos_9),
                findViewById(R.id.c_cosmos_1), findViewById(R.id.c_cosmos_2), findViewById(R.id.c_cosmos_3),
                findViewById(R.id.c_cosmos_4)
        };

        Button reserveButton = findViewById(R.id.reserve_cosmos);
        Button navigateButton = findViewById(R.id.navigate_cosmsos);

        // Listen to Firebase database changes and update the button states accordingly
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String buttonId = snapshot.getKey(); // Firebase key corresponds to button ID
                    String color = snapshot.child("color").getValue(String.class);
                    Log.d("FirebaseData", "Button ID: " + buttonId + ", Color: " + color);
                    updateButtonState(buttonId, color);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(cosmos_model.this, "Failed to sync data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle button clicks to select parking spaces
        View.OnClickListener buttonClickListener = view -> selectedButton = (Button) view;

        // Assign click listeners to all parking space buttons
        for (Button button : buttons) {
            button.setOnClickListener(buttonClickListener);
        }

        // Handle reservation updates
        reserveButton.setOnClickListener(v -> {
            if (selectedButton != null && !isColorChanged) {
                selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW)); // Mark as reserved
                selectedButton.setTag("yellow");
                updateFirebaseState(selectedButton.getId(), "yellow");
                isColorChanged = true; // Prevent multiple reservations
                resetColorChangedFlag();
            } else {
                Toast.makeText(cosmos_model.this, "No space selected or already reserved!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle navigation button to open Google Maps with directions to Cosmos College
        navigateButton.setOnClickListener(v -> {
            String destination = "Cosmos College of Management and Technology, Tutepani, Lalitpur";
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(destination));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // If Google Maps is not installed, fallback to web URL
                Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + Uri.encode(destination));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                startActivity(webIntent);
            }
        });
    }

    private void updateFirebaseState(int buttonId, String color) {
        String buttonIdStr = getResources().getResourceEntryName(buttonId); // Convert button ID to string
        databaseReference.child(buttonIdStr).child("color").setValue(color); // Update color in Firebase
    }

    private void updateButtonState(String buttonId, String color) {
        int resId = getResources().getIdentifier(buttonId, "id", getPackageName());
        Button button = findViewById(resId);

        if (button != null) {
            int colorValue = Color.TRANSPARENT; // Default color
            if ("green".equals(color)) {
                colorValue = Color.GREEN;
            } else if ("red".equals(color)) {
                colorValue = Color.RED;
            } else if ("yellow".equals(color)) {
                colorValue = Color.YELLOW;
            }

            button.setBackgroundTintList(ColorStateList.valueOf(colorValue)); // Apply color tint
            button.setTag(color);
        } else {
            Log.e("UpdateButtonColor", "Button with ID " + buttonId + " not found");
        }
    }

    private void resetColorChangedFlag() {
        handler.postDelayed(() -> isColorChanged = false, 5000); // Reset flag after 5 seconds
    }
}
