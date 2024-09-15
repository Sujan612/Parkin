package com.example.parkin1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
    private boolean isColorChanged = false;
    private DatabaseReference databaseReference;
    private boolean isOperator = true;  // Set this to false if it's a regular user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosmos_operator);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("buttonState");

        // Define your buttons
        Button button1 = findViewById(R.id.b_cosmos_1);
        Button button2 = findViewById(R.id.b_cosmos_2);
        Button button3 = findViewById(R.id.b_cosmos_3);
        Button button4 = findViewById(R.id.b_cosmos_4);
        Button button5 = findViewById(R.id.b_cosmos_5);
        Button button6 = findViewById(R.id.b_cosmos_6);
        Button button7 = findViewById(R.id.b_cosmos_7);
        Button button8 = findViewById(R.id.b_cosmos_8);
        Button button9 = findViewById(R.id.b_cosmos_9);

        // Occupied and Available buttons
        Button occupied = findViewById(R.id.occupied_cosmos);
        Button available = findViewById(R.id.available_cosmos);

        // Settings button
        ImageButton settingsButton = findViewById(R.id.imageButton);

        // Firebase sync
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String selectedButtonId = dataSnapshot.child("selectedButton").getValue(String.class);
                String color = dataSnapshot.child("color").getValue(String.class);
                Boolean isChanged = dataSnapshot.child("isColorChanged").getValue(Boolean.class);

                if (selectedButtonId != null && color != null && isChanged != null) {
                    restoreButtonState(selectedButtonId, color, isChanged);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(cosmos_operator.this, "Failed to sync data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Button click listener for selection
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isColorChanged || (isOperator && selectedButton.getTag().equals("red")&&!selectedButton.getTag().equals("yellow"))) {
                    selectedButton = (Button) view;
                    selectedButton.setBackgroundResource(R.drawable.yes_border);
                } else {
                    Toast.makeText(cosmos_operator.this, "A space is already selected!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        button1.setOnClickListener(buttonClickListener);
        button2.setOnClickListener(buttonClickListener);
        button3.setOnClickListener(buttonClickListener);
        button4.setOnClickListener(buttonClickListener);
        button5.setOnClickListener(buttonClickListener);
        button6.setOnClickListener(buttonClickListener);
        button7.setOnClickListener(buttonClickListener);
        button8.setOnClickListener(buttonClickListener);
        button9.setOnClickListener(buttonClickListener);

        // Occupied button logic (can be changed by operator, not by user)
        occupied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedButton != null && (!selectedButton.getTag().equals("red") || isOperator ||!selectedButton.getTag().equals("yellow"))) {
                    selectedButton.setBackgroundColor(Color.RED);
                    selectedButton.setTag("red"); // Mark button as occupied
                    isColorChanged = true;
                    saveButtonState("RED");
                } else {
                    Toast.makeText(cosmos_operator.this, "Cannot change an occupied space!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Available button logic (operator can change a red button, user cannot)
        available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedButton != null && (!selectedButton.getTag().equals("red") || isOperator ||!selectedButton.getTag().equals("yellow"))) {
                    selectedButton.setBackgroundColor(Color.TRANSPARENT);
                    selectedButton.setTag(""); // Clear color
                    isColorChanged = false;
                    saveButtonState("TRANSPARENT");
                } else {
                    Toast.makeText(cosmos_operator.this, "Cannot change an occupied space!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Settings button click listener
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(cosmos_operator.this, SettingActivity.class));
        });
    }

    private void saveButtonState(String color) {
        if (selectedButton != null) {
            String selectedButtonId = getResources().getResourceEntryName(selectedButton.getId());
            databaseReference.child("selectedButton").setValue(selectedButtonId);
            databaseReference.child("color").setValue(color);
            databaseReference.child("isColorChanged").setValue(true);
        }
    }

    private void restoreButtonState(String selectedButtonId, String color, boolean isChanged) {
        deselectPreviousButton();
        int resId = getResources().getIdentifier(selectedButtonId, "id", getPackageName());
        selectedButton = findViewById(resId);
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.yes_border);
            if(color.equals("RED")) {
                selectedButton.setBackgroundColor(Color.RED);
                selectedButton.setTag("red"); // Mark as occupied
            } else if (color.equals("YELLOW")) {
                selectedButton.setBackgroundColor(Color.YELLOW);
                selectedButton.setTag("yellow");
            } else if(color.equals("TRANSPARENT")) {
                selectedButton.setBackgroundColor(Color.TRANSPARENT);
                selectedButton.setTag(""); // Clear tag
            }
        }
        isColorChanged = isChanged;
    }

    private void deselectPreviousButton() {
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.no_border);
            selectedButton.setBackgroundColor(Color.TRANSPARENT);
            selectedButton.setTag(""); // Reset tag
        }
    }
}
