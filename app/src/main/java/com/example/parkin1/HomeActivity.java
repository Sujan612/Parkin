package com.example.parkin1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements OnItemClickListener {

    BottomNavigationView bottomNavigationView;
    private List<CustomLocation> locationList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView3);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                // Avoid starting the same activity
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
            return false;
        });

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the locationList and populate with data
        locationList = new ArrayList<>();
        locationList.add(new CustomLocation(getString(R.string._1), "Cosmos College of Management and Technology", "Tutepani, Lalitpur, Nepal", "2.7 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._2), "Bhatbhateni", "Tutepani, Lalitpur, Nepal", "2.8 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._3), "National Engineering College", "Tutepani, Lalitpur, Nepal", "2.9 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._4), "Ullens College", "Tutepani, Lalitpur, Nepal", "2.12 km away", "Rs. 50/hr"));

        // Pass the OnItemClickListener (this) to the adapter
        LocationAdapter adapter = new LocationAdapter(locationList, this);
        recyclerView.setAdapter(adapter);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onItemClick(int position) {
        try {
            // Get the location name
            String locationName = locationList.get(position).getName();
            Log.d("HomeActivity", "Item clicked: " + locationName);

            // Determine which activity to start based on the location name
            Intent intent;
            switch (locationName) {
                case "Cosmos College of Management and Technology":
                    intent = new Intent(HomeActivity.this, cosmos_model.class);
                    break;
                case "Bhatbhateni":
                    intent = new Intent(HomeActivity.this, Bhatbheteni_model.class);
                    break;
                default:
                    Toast.makeText(this, "No activity found for this location", Toast.LENGTH_SHORT).show();
                    return;
            }

            // Pass data to the new activity
            intent.putExtra("location_name", locationList.get(position).getName());
            intent.putExtra("location_address", locationList.get(position).getAddress());
            intent.putExtra("location_distance", locationList.get(position).getDistance());
            intent.putExtra("location_price", locationList.get(position).getPrice());
            startActivity(intent);
        } catch (Exception e) {
            Log.e("HomeActivity", "Error starting activity", e);
        }
    }

}
