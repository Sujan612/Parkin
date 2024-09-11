package com.example.parkin1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, address;
    private ImageView nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Find views
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        nextButton = findViewById(R.id.frontarrow_signup1);

        // Set up the next button to pass data and navigate to SecondsignupActivity
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String firstNameText = firstName.getText().toString();
                String lastNameText = lastName.getText().toString();
                String emailText = email.getText().toString();
                String addressText = address.getText().toString();

                // Create an Intent to pass data to SecondsignupActivity
                Intent intent = new Intent(SignupActivity.this, SecondsignupActivity.class);
                intent.putExtra("firstName", firstNameText);
                intent.putExtra("lastName", lastNameText);
                intent.putExtra("email", emailText);
                intent.putExtra("address", addressText);

                // Start the next activity
                startActivity(intent);
            }
        });

        // Handle window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle back arrow functionality
        ImageView bck_arrow = findViewById(R.id.backarrow_signup1);
        bck_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to MainActivity
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
