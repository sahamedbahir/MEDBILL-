package com.example.projectmedbill;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PillsActivity extends AppCompatActivity {

    private LinearLayout pillsLayout;
    private DatabaseReference productDatabase;
    private List<Product> pillsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pills);

        // Initialize Firebase database reference
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");

        // Get reference to the layout where products will be displayed
        pillsLayout = findViewById(R.id.pillsLayout);

        // Load products
        loadPills();
    }

    private void loadPills() {
        productDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pillsLayout.removeAllViews(); // Clear previous views
                pillsList.clear(); // Clear the list

                if (!snapshot.exists()) {
                    Log.d("PillsActivity", "No data found in Products.");
                    return;
                }

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null && "Pills".equals(product.getType())) {
                        pillsList.add(product);
                        addProductView(product);
                    }
                }

                if (pillsList.isEmpty()) {
                    Log.d("PillsActivity", "No Pills found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PillsActivity.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
                Log.e("PillsActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void addProductView(Product product) {
        // Create a new LinearLayout for each product
        LinearLayout productLayout = new LinearLayout(this);
        productLayout.setOrientation(LinearLayout.VERTICAL);
        productLayout.setPadding(16, 16, 16, 16);

        // Create TextView for product name
        TextView productName = new TextView(this);
        productName.setText(product.getName());
        productName.setTextSize(18);

        // Create TextView for product price
        TextView productPrice = new TextView(this);
        productPrice.setText("Price: ₹" + product.getPrice());
        productPrice.setTextSize(16);

        // Create Button to add product to cart
        Button addToCartButton = new Button(this);
        addToCartButton.setText("Add to Cart");
        addToCartButton.setOnClickListener(v -> addToCart(product));

        // Add TextViews and Button to the productLayout
        productLayout.addView(productName);
        productLayout.addView(productPrice);
        productLayout.addView(addToCartButton);

        // Add productLayout to the main pillsLayout
        pillsLayout.addView(productLayout);
    }

    private void addToCart(Product product) {
        // Implement your logic to add the product to the cart
        Toast.makeText(this, product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        // Add actual cart logic here
    }
}
