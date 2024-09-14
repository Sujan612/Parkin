package com.example.parkin1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class cosmos_model extends AppCompatActivity {

    private static final String location = "27.65618593181801, 85.3215688432409";
    private Button selectedButton = null;
    private boolean isColorChanged = false;
    private DatabaseReference databaseReference;
    private Runnable resetColorChangedTask;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cosmos_model);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("buttonState");
        //button
        Button button1 = findViewById(R.id.b_cosmos_1);
        Button button2 = findViewById(R.id.b_cosmos_2);
        Button button3 = findViewById(R.id.b_cosmos_3);
        Button button4 = findViewById(R.id.b_cosmos_4);
        Button button5 = findViewById(R.id.b_cosmos_5);
        Button button6 = findViewById(R.id.b_cosmos_6);
        Button button7 = findViewById(R.id.b_cosmos_7);
        Button button8 = findViewById(R.id.b_cosmos_8);
        Button button9 = findViewById(R.id.b_cosmos_9);
        Button button10 = findViewById(R.id.c_cosmos_1);
        Button button11 = findViewById(R.id.c_cosmos_2);
        Button button12 = findViewById(R.id.c_cosmos_3);
        Button button13 = findViewById(R.id.c_cosmos_4);
        Button reserve = findViewById(R.id.reserve_cosmos);
        Button navigate = findViewById(R.id.navigate_cosmsos);
        //database sync
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the button state from Firebase
                String selectedButtonId = dataSnapshot.child("selectedButton").getValue(String.class);
                String color = dataSnapshot.child("color").getValue(String.class);
                Boolean isChanged = dataSnapshot.child("isColorChanged").getValue(Boolean.class);

                if (selectedButtonId != null && color != null && isChanged != null) {
                    // Restore the selected button and its state
                    restoreButtonState(selectedButtonId, color, isChanged);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(cosmos_model.this, "Failed to sync data.", Toast.LENGTH_SHORT).show();
            }
        });
        //button border
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isColorChanged) {
                    selectedButton = (Button) view;
                    selectedButton.setBackgroundResource(R.drawable.yes_border);
                } else if (isColorChanged) {
                    Toast.makeText(cosmos_model.this, "Already reserved one space!", Toast.LENGTH_SHORT).show();
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
        button10.setOnClickListener(buttonClickListener);
        button11.setOnClickListener(buttonClickListener);
        button12.setOnClickListener(buttonClickListener);
        button13.setOnClickListener(buttonClickListener);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMapsNavigation(location);
            }
        });


        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isColorChanged && selectedButton != null) {
                    selectedButton.setBackgroundColor(Color.YELLOW);
                    isColorChanged = true;
                    saveButtonState();
                    scheduleColorChangeReset();
                }
            }
        });

    }

    private void saveButtonState() {
        if (selectedButton != null) {
            String selectedButtonId = getResources().getResourceEntryName(selectedButton.getId());
            databaseReference.child("selectedButton").setValue(selectedButtonId);
            databaseReference.child("color").setValue("YELLOW");
            databaseReference.child("isColorChanged").setValue(true);
        }
    }

    private void restoreButtonState (String selectedButtonId, String color,boolean isChanged){
        // Reset borders for all buttons
        deselectPreviousButton();
        @SuppressLint("DiscouragedApi")int resId = getResources().getIdentifier(selectedButtonId, "id", getPackageName());
        selectedButton = findViewById(resId);
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.yes_border);
            if (color.equals("YELLOW")) {
                selectedButton.setBackgroundColor(Color.YELLOW);
            }
        }
        isColorChanged = isChanged;
    }
    private void scheduleColorChangeReset() {
        if (resetColorChangedTask != null) {
            handler.removeCallbacks(resetColorChangedTask);
        }

        // to reset the flag and allow selection
        resetColorChangedTask = new Runnable() {
            @Override
            public void run() {
                isColorChanged = false; // Reset the flag
                deselectPreviousButton(); // Deselect the button
                databaseReference.child("isColorChanged").setValue(false);
            }
        };
        handler.postDelayed(resetColorChangedTask, 15000);//15sec
    }
    private void deselectPreviousButton() {
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.no_border);
            selectedButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    //location
    private void openGoogleMapsNavigation(String location) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        PackageManager packageManager = getPackageManager();
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent);
        } else {
            // redirect to Google Play Store
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
            if (playStoreIntent.resolveActivity(packageManager) != null) {
                startActivity(playStoreIntent);
            } else {
                Toast.makeText(cosmos_model.this, "Unable to open Play Store", Toast.LENGTH_SHORT).show();
            }
        }
    }
}