package com.example.parkin1;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends BaseActivity implements OnItemClickListener {

    BottomNavigationView bottomNavigationView;
    private List<CustomLocation> locationList;
    private TextView homeTextLocation;

    // Location services
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeTextLocation = findViewById(R.id.home_text_location);

        // Initialize FusedLocationProviderClient for location updates
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check location permission and start location updates
        if (checkLocationPermission()) {
            startLocationUpdates();
        } else {
            requestLocationPermission();
        }

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
        locationList.add(new CustomLocation(getString(R.string._1), "Cosmos College of Management and Technology", "Tutepani, Lalitpur, Nepal", "0 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._2), "Bhatbhateni", "Tutepani, Lalitpur, Nepal", "0.12 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._3), "National Engineering College", "Tutepani, Lalitpur, Nepal", "0.05 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._4), "Ullens College", "Tutepani, Lalitpur, Nepal", "0.14 km away", "Rs. 50/hr"));

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

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update every 10 seconds
        locationRequest.setFastestInterval(5000); // At least every 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with current location
                    updateLocationUI(location);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocationUI(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0); // Get full address
                homeTextLocation.setText(addressText);
                Log.d("HomeActivity", "Location updated: " + addressText);
            } else {
                homeTextLocation.setText("Unable to get location name");
            }
        } catch (IOException e) {
            Log.e("HomeActivity", "Error getting location name", e);
            homeTextLocation.setText("Error getting location name");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
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
                    intent = new Intent(HomeActivity.this, bhatbhateni_model.class);
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
