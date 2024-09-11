package com.example.parkin1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {

    private static final String PREFS_NAME = "language_prefs";
    private static final String KEY_LANGUAGE = "selected_language";
    private boolean doubleBackToExitPressedOnce = false;
    private static final int TIME_INTERVAL = 2000;
    private final Handler doubleBackHandler = new Handler(Looper.getMainLooper());

    private FirebaseAuth mAuth;
    private EditText loginEmail, loginPassword;
    private Button loginBtn;
    private TextView signupBtn, forgotPassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        loginEmail = findViewById(R.id.username);
        loginPassword = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_btn);
        forgotPassBtn = findViewById(R.id.forgotpass_btn);
        signupBtn = findViewById(R.id.signup_btn);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Login button click listener
        loginBtn.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginUser(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Forgot password button click listener
        forgotPassBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GetotpActivity.class));
        });

        // Signup button click listener
        signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        });

        // Handle double back press to exit
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    // Exit the app
                    finishAndRemoveTask();
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(MainActivity.this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, TIME_INTERVAL);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User is logged in, check their role
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            userRef.child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.getValue(String.class);

                    if (role != null) {
                        if (role.equals("operator")) {
                            // Redirect to OperatorActivity
                            startActivity(new Intent(MainActivity.this, OperatorActivity.class));
                        } else {
                            // Redirect to HomeActivity for regular users
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }
                        finish(); // Close the current activity
                    } else {
                        Toast.makeText(MainActivity.this, "Role not found. Please contact support.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Fetch user role from Firebase Realtime Database
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                            userRef.child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String role = dataSnapshot.getValue(String.class);

                                    if (role != null) {
                                        if (role.equals("operator")) {
                                            // Redirect to OperatorActivity
                                            startActivity(new Intent(MainActivity.this, OperatorActivity.class));
                                        } else {
                                            // Redirect to HomeActivity
                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        }
                                        finish(); // Close the current activity
                                    } else {
                                        // Role not found, contact support
                                        Toast.makeText(MainActivity.this, "Role not found. Please contact support.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle possible errors.
                                    Log.e("MainActivity", "Database error: " + databaseError.getMessage());
                                    Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // If login fails, display an error message
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        Toast.makeText(MainActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "Login failed: " + errorMessage);
                    }
                });
    }

}
