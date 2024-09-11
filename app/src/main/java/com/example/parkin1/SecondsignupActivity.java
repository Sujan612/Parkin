package com.example.parkin1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SecondsignupActivity extends BaseActivity {

    private EditText phoneNumber, password, confirmPassword;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup2);

        phoneNumber = findViewById(R.id.phonenumber);
        password = findViewById(R.id.choose_password);
        confirmPassword = findViewById(R.id.confirm_password);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        findViewById(R.id.signup_btn_signup2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignup();
            }
        });

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void handleSignup() {
        // Retrieve data from Intent
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String email = getIntent().getStringExtra("email");
        String address = getIntent().getStringExtra("address");

        // Retrieve data from input fields
        String phone = phoneNumber.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || address.isEmpty() ||
                phone.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for unique phone number
        checkUniquePhoneNumber(phone, new OnUniqueCheckListener() {
            @Override
            public void onCheckComplete(boolean isUnique) {
                if (!isUnique) {
                    Toast.makeText(SecondsignupActivity.this, "Phone number already in use", Toast.LENGTH_SHORT).show();
                } else {
                    // If phone is unique, check for unique password
                    checkUniquePassword(pass, new OnUniqueCheckListener() {
                        @Override
                        public void onCheckComplete(boolean isUnique) {
                            if (!isUnique) {
                                Toast.makeText(SecondsignupActivity.this, "Password already in use. Please choose another password.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Both phone and password are unique, proceed to create user
                                registerWithEmailAndPassword(firstName, lastName, email, address, phone, pass);
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkUniquePhoneNumber(String phone, OnUniqueCheckListener listener) {
        Query phoneQuery = databaseReference.orderByChild("phone").equalTo(phone);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onCheckComplete(!dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SecondsignupActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                listener.onCheckComplete(false);
            }
        });
    }

    private void checkUniquePassword(String password, OnUniqueCheckListener listener) {
        Query passQuery = databaseReference.orderByChild("password").equalTo(password);
        passQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onCheckComplete(!dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SecondsignupActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                listener.onCheckComplete(false);
            }
        });
    }

    private void registerWithEmailAndPassword(String firstName, String lastName, String email, String address, String phone, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Store additional user details in Realtime Database
                            String userId = user.getUid();
                            User newUser = new User(firstName, lastName, email, address, phone, password, "user"); // Set default role as "user"

                            databaseReference.child(userId).setValue(newUser).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(SecondsignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                    // Redirect to MainActivity after successful registration
                                    Intent intent = new Intent(SecondsignupActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // Close the current activity to prevent going back
                                } else {
                                    Toast.makeText(SecondsignupActivity.this, "Failed to store user details: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // If registration fails
                        Toast.makeText(SecondsignupActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Listener interface for uniqueness checks
    interface OnUniqueCheckListener {
        void onCheckComplete(boolean isUnique);
    }

    // User model class
    public static class User {
        public String firstName, lastName, email, address, phone, password, role; // Add role field

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String firstName, String lastName, String email, String address, String phone, String password, String role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.address = address;
            this.phone = phone;
            this.password = password;
            this.role = role; // Set the user's role
        }
    }
}
