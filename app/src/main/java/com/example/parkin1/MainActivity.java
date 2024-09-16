package com.example.parkin1;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
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

import java.util.Locale;

public class MainActivity extends BaseActivity {

    private static final String PREFS_NAME = "language_prefs";
    private static final String KEY_LANGUAGE = "selected_language";
    private boolean doubleBackToExitPressedOnce = false;
    private static final int TIME_INTERVAL = 2000;
    private final Handler doubleBackHandler = new Handler(Looper.getMainLooper());

    private FirebaseAuth mAuth;
    private EditText loginEmail, loginPassword;
    private Button loginBtn;
    private TextView signupBtn, forgotPassBtn, changeLanguageBtn;

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
        changeLanguageBtn = findViewById(R.id.change_language_text);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set language based on saved preference
        applySavedLanguage();

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
            startActivity(new Intent(MainActivity.this, forgot_password.class));
        });

        // Signup button click listener
        signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        });

        // Change language button click listener
        changeLanguageBtn.setOnClickListener(v -> showChangeLanguageDialog());

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

    private void showChangeLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language")
                .setItems(new CharSequence[]{"English", "Nepali"}, (dialog, which) -> {
                    String languageCode = which == 0 ? "en" : "ne";
                    setLanguage(languageCode);
                });
        builder.create().show();
    }

    private void setLanguage(String languageCode) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        // Apply configuration changes without recreating the activity
        getResources().getConfiguration().setLocale(locale);
        getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());

        // Restart activity to apply the new language
        recreate();
    }

    private void applySavedLanguage() {
        String languageCode = getSavedLanguage();
        if (!languageCode.equals(Locale.getDefault().getLanguage())) {
            setLanguage(languageCode);
        }
    }

    private String getSavedLanguage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "en"); // Default to English
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Run database fetch in background to avoid blocking UI
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String role = dataSnapshot.child("role").getValue(String.class);
                        String work = dataSnapshot.child("work").getValue(String.class);

                        if (role != null && work != null) {
                            if (role.equals("operator")) {
                                if (work.equals("cosmos")) {
                                    // Redirect to CosmosOperatorActivity
                                    startActivity(new Intent(MainActivity.this, cosmos_operator.class));
                                } else if (work.equals("bhatbheteni")) {
                                    // Redirect to BhatbhateniActivity
                                    startActivity(new Intent(MainActivity.this, bhatbhateni_operator.class));
                                } else {
                                    // Handle other work values or default behavior
                                    Toast.makeText(MainActivity.this, "Unknown work value. Please contact support.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Redirect to HomeActivity for regular users
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            }
                            finish(); // Close the current activity
                        } else {
                            Toast.makeText(MainActivity.this, "Role or work not found. Please contact support.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }, 1000);  // Delay for smooth transition
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
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String role = dataSnapshot.child("role").getValue(String.class);
                                    String work = dataSnapshot.child("work").getValue(String.class);

                                    if (role != null && work != null) {
                                        if (role.equals("operator")) {
                                            if (work.equals("cosmos")) {
                                                startActivity(new Intent(MainActivity.this, cosmos_operator.class));
                                            } else if (work.equals("bhatbheteni")) {
                                                startActivity(new Intent(MainActivity.this, bhatbhateni_operator.class));
                                            } else {
                                                Toast.makeText(MainActivity.this, "Unknown work value. Please contact support.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        }
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Role or work not found. Please contact support.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
