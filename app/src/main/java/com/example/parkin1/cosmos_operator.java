package com.example.parkin1;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class cosmos_operator extends AppCompatActivity {

    private Button selectedButton = null;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosmos_operator);

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

        Button occupiedButton = findViewById(R.id.occupied_cosmos);
        Button availableButton = findViewById(R.id.available_cosmos);
        ImageButton settingsButton = findViewById(R.id.imageButton);

        // Listen to Firebase database changes and update the button states accordingly
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String buttonId = snapshot.getKey();
                    String color = snapshot.child("color").getValue(String.class);
                    updateButtonColor(buttonId, color);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(cosmos_operator.this, "Failed to sync data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle button clicks to select parking spaces
        View.OnClickListener buttonClickListener = view -> selectedButton = (Button) view;

        // Assign click listeners to all parking space buttons
        for (Button button : buttons) {
            button.setOnClickListener(buttonClickListener);
        }

        // Handle occupied space updates
        occupiedButton.setOnClickListener(v -> {
            if (selectedButton != null) {
                selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED)); // Mark as occupied
                selectedButton.setTag("red");
                updateFirebaseState(selectedButton.getId(), "red");
            } else {
                Toast.makeText(cosmos_operator.this, "No space selected!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle available space updates
        availableButton.setOnClickListener(v -> {
            if (selectedButton != null) {
                selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN)); // Mark as available
                selectedButton.setTag("green");
                updateFirebaseState(selectedButton.getId(), "green");
            } else {
                Toast.makeText(cosmos_operator.this, "No space selected!", Toast.LENGTH_SHORT).show();
            }
        });

        // Settings button click listener
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(cosmos_operator.this, settingoperatorActivity.class));
        });
    }

    private void updateFirebaseState(int buttonId, String color) {
        String buttonIdStr = getResources().getResourceEntryName(buttonId); // Convert button ID to string
        databaseReference.child(buttonIdStr).child("color").setValue(color); // Update color in Firebase
    }

    private void updateButtonColor(String buttonId, String color) {
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
}
