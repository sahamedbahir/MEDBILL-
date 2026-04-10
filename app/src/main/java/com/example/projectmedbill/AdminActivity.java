package com.example.projectmedbill;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    private EditText productNameEditText, productPriceEditText, productDescriptionEditText, manufactureDateEditText, expiryDateEditText;
    private RadioGroup productTypeGroup;
    private Button addProductButton, backButton;
    private DatabaseReference productDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize Firebase database reference
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");

        // Get references to UI elements
        productNameEditText = findViewById(R.id.productNameEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        productDescriptionEditText = findViewById(R.id.productDescriptionEditText);
        manufactureDateEditText = findViewById(R.id.manufactureDateEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        productTypeGroup = findViewById(R.id.productTypeGroup);
        addProductButton = findViewById(R.id.addProductButton);
        backButton = findViewById(R.id.backButton); // Back button reference

        // Set click listener for the "Add Product" button
        addProductButton.setOnClickListener(v -> addProduct());

        // Set click listener for the "Back" button
        backButton.setOnClickListener(v -> {
            // Navigate back to the login page
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Optional, if you want to close the AdminActivity
        });
    }

    private void addProduct() {
        // Get the input values
        String name = productNameEditText.getText().toString().trim();
        String priceText = productPriceEditText.getText().toString().trim();
        String description = productDescriptionEditText.getText().toString().trim();
        String manufactureDate = manufactureDateEditText.getText().toString().trim();
        String expiryDate = expiryDateEditText.getText().toString().trim();
        int selectedTypeId = productTypeGroup.getCheckedRadioButtonId();

        // Validate input fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceText) || TextUtils.isEmpty(description) ||
                TextUtils.isEmpty(manufactureDate) || TextUtils.isEmpty(expiryDate) || selectedTypeId == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse price to double
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected product type from RadioGroup
        RadioButton selectedTypeButton = findViewById(selectedTypeId);
        String type = selectedTypeButton.getText().toString();

        // Generate a unique product ID for Firebase
        String productId = productDatabase.push().getKey();
        if (productId != null) {
            // Create a Product object with type and additional fields
            Product product = new Product(productId, name, price, type,description, manufactureDate, expiryDate);

            // Save the product to Firebase under "Products" node
            productDatabase.child(productId).setValue(product).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Success message
                    Toast.makeText(this, "Product added successfully", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Product added to Firebase: " + product);
                } else {
                    // Failure message
                    Toast.makeText(this, "Failed to add product", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to add product: " + task.getException().getMessage());
                }
            });
        }
    }
}