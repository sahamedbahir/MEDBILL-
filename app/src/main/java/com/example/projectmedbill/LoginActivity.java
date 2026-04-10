package com.example.projectmedbill;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView forgotPasswordText, signUpRedirectText;
    private Spinner categorySpinner;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        // Initialize UI components
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordText = findViewById(R.id.forgot_password);
        signUpRedirectText = findViewById(R.id.signUpRedirectText);
        categorySpinner = findViewById(R.id.CategorySpinner);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");

        // Setup category dropdown spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set onClick listeners
        loginButton.setOnClickListener(v -> loginUser());
        forgotPasswordText.setOnClickListener(v -> showForgotPasswordDialog());
        signUpRedirectText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();

        // Validate email and password
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

        // Authenticate user
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    String userId = auth.getCurrentUser().getUid();
                    // Retrieve user role from Firebase Database
                    database.child(userId).child("role").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String role = task.getResult().getValue(String.class);
                            if ("admin".equalsIgnoreCase(role)) {
                                // Redirect to AdminActivity for Admins
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                            } else if ("customer".equalsIgnoreCase(role)) {
                                // Redirect to MainActivity for Customers
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("category", selectedCategory); // Pass selected category
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Role not recognized", Toast.LENGTH_SHORT).show();
                            }
                            finish(); // Close login activity
                        } else {
                            Toast.makeText(LoginActivity.this, "Error retrieving role", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

        passwordResetDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        passwordResetDialog.create().show();
    }
}
