package com.example.parkin1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class forgot_password extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView forgot_pass_back = findViewById(R.id.forgotpass_back);
        Button update_pass = findViewById(R.id.forgot_pass_update);
        emailField = findViewById(R.id.forgot_pass_email);  // Ensure this EditText exists in your layout

        forgot_pass_back.setOnClickListener(v -> {
            Intent intent = new Intent(forgot_password.this, MainActivity.class);
            startActivity(intent);
        });

        update_pass.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(forgot_password.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                sendPasswordResetEmail(email);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Email sent successfully, log timestamp in Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(getUserIdFromEmail(email));
                        userRef.child("lastPasswordReset").setValue(System.currentTimeMillis());

                        // Show a message and redirect to MainActivity
                        Toast.makeText(forgot_password.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(forgot_password.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // Failed to send email, log the error and show a message to the user
                        Log.e("ForgotPassword", "Error sending password reset email", task.getException());
                        Toast.makeText(forgot_password.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getUserIdFromEmail(String email) {
        // This is a helper function to convert email to a valid Firebase key (optional)
        return email.replace(".", "_").replace("@", "_");
    }
}
