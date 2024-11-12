package com.example.projectmedbill;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView forgotPasswordText, signUpRedirectText;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordText = findViewById(R.id.forgot_password);
        signUpRedirectText = findViewById(R.id.signUpRedirectText);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");

        loginButton.setOnClickListener(v -> loginUser());

        forgotPasswordText.setOnClickListener(v -> showForgotPasswordDialog());

        signUpRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Please enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            loginPassword.setError("Password is required");
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    String userId = auth.getCurrentUser().getUid();

                    // Retrieve user role from Firebase Database
                    database.child(userId).child("role")
                            .get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String role = task.getResult().getValue(String.class);
                                    if (role != null) {
                                        if ("admin".equalsIgnoreCase(role)) {
                                            // Redirect to AdminActivity for Admins
                                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                        } else if ("customer".equalsIgnoreCase(role)) {
                                            // Redirect to MainActivity for Customers
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            // Load user's products, cart, and bill
                                            loadUserData(userId);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Role not recognized", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Role is not set for this user", Toast.LENGTH_SHORT).show();
                                    }
                                    finish(); // Close the login activity
                                } else {
                                    Toast.makeText(LoginActivity.this, "Error retrieving role", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserData(String userId) {
        // Reference to user's products, cart, and bills
        DatabaseReference productsRef = database.child(userId).child("products");
        DatabaseReference cartRef = database.child(userId).child("cart");
        DatabaseReference billsRef = database.child(userId).child("bills");

        // Load user products
        productsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Handle user products data
                // For example, store it in a local variable or update UI
                // List<Product> userProducts = task.getResult().getValue(new GenericTypeIndicator<List<Product>>() {});
            } else {
                Toast.makeText(LoginActivity.this, "Error loading products: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load user cart
        cartRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Handle user cart data
                // List<CartItem> userCart = task.getResult().getValue(new GenericTypeIndicator<List<CartItem>>() {});
            } else {
                Toast.makeText(LoginActivity.this, "Error loading cart: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load user bills
        billsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Handle user bill data
                // List<Bill> userBills = task.getResult().getValue(new GenericTypeIndicator<List<Bill>>() {});
            } else {
                Toast.makeText(LoginActivity.this, "Error loading bills: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        final EditText resetEmail = new EditText(this);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter your email to receive the reset link");
        passwordResetDialog.setView(resetEmail);

        passwordResetDialog.setPositiveButton("Send", (dialog, which) -> {
            String email = resetEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        passwordResetDialog.setNegativeButton("Cancel", (dialog, which) -> {
            // Dismiss dialog
        });

        passwordResetDialog.create().show();
    }
}
