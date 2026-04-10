package com.example.projectmedbill;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText signUpEmail, signUpPassword;
    private Spinner roleSpinner;
    private Button signUpButton;
    private TextView loginRedirectText;

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);

        signUpEmail = findViewById(R.id.signup_email);
        signUpPassword = findViewById(R.id.signup_password);
        roleSpinner = findViewById(R.id.spinnerRole);
        signUpButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");

        // Setting up the role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        signUpButton.setOnClickListener(v -> registerUser());

        loginRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String email = signUpEmail.getText().toString().trim();
        String password = signUpPassword.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(email)) {
            signUpEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            signUpPassword.setError("Password is required");
            return;
        }

        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getSignInMethods().isEmpty()) {
                    createUser(email, password, role);
                } else {
                    Toast.makeText(SignupActivity.this, "Email already registered. Please log in.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignupActivity.this, "Error checking email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUser(String email, String password, String role) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            Map<String, Object> userDetails = new HashMap<>();
                            userDetails.put("email", email);
                            userDetails.put("role", role);

                            database.child(userId).setValue(userDetails)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            initializeUserCartAndBills(userId);
                                            Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SignupActivity.this, "Failed to save user data: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializeUserCartAndBills(String userId) {
        DatabaseReference cartRef = database.child(userId).child("cartItems");
        DatabaseReference billsRef = database.child(userId).child("bills");

        cartRef.setValue(new HashMap<>()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this, "Cart initialized.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignupActivity.this, "Failed to initialize cart: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        billsRef.setValue(new HashMap<>()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this, "Bills initialized.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignupActivity.this, "Failed to initialize bills: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
