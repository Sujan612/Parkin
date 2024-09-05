package com.example.parkin1;

import com.example.parkin1.LocationAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends BaseActivity{


    BottomNavigationView bottomNavigationView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        bottomNavigationView=findViewById(R.id.bottomNavigationView3);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId= item.getItemId();
                if(itemId==R.id.home)
                {
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId==R.id.navigation)
                {
                    startActivity(new Intent(getApplicationContext(),NavigationActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId==R.id.settings) {
                    startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                    overridePendingTransition(0,0);
                    return true;


                }

                return true;
            }

        });


        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CustomLocation> locationList = new ArrayList<>();
        locationList.add(new CustomLocation(getString(R.string._1),"Cosmos College of Management and Technology", "Tutepani, Lalitpur, Nepal", "2.7 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._2),"Bhatbhateni", "Tutepani, Lalitpur, Nepal", "2.8 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._3),"National Engineering College", "Tutepani, Lalitpur, Nepal", "2.9 km away", "Rs. 50/hr"));
        locationList.add(new CustomLocation(getString(R.string._4),"Ullens College", "Tutepani, Lalitpur, Nepal", "2.12 km away", "Rs. 50/hr"));

        // Add more locations as needed

        LocationAdapter adapter = new LocationAdapter(locationList);
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

}