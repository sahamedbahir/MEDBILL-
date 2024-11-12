package com.example.projectmedbill;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutUsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView userNameTextView, userEmailTextView, totalBillTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize TextViews
        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        totalBillTextView = findViewById(R.id.total_bill);

        // Display user details
        displayUserDetails();

        // Fetch and display total bill
        fetchTotalBill();
    }

    private void displayUserDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userName = user.getDisplayName() != null ? user.getDisplayName() : "User";
            String userEmail = user.getEmail();

            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);
        }
    }

    private void fetchTotalBill() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Get the user's cart from Firebase
            DatabaseReference userCartRef = mDatabase.child("users").child(userId).child("cart");

            userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    double totalBill = 0.0;

                    // Check if cart is empty
                    if (!dataSnapshot.exists()) {
                        totalBillTextView.setText("Total Bill: ₹0.00");
                        return;
                    }

                    // Iterate through cart items and sum the total price
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // Log to debug
                        String productId = productSnapshot.getKey();
                        Double productPrice = productSnapshot.child("price").getValue(Double.class);
                        Integer quantity = productSnapshot.child("quantity").getValue(Integer.class);

                        // Log the retrieved values for debugging
                        if (productPrice == null || quantity == null) {
                            Log.d("AboutUsActivity", "Missing data for product: " + productId);
                        } else {
                            Log.d("AboutUsActivity", "Product: " + productId + ", Price: " + productPrice + ", Quantity: " + quantity);
                            totalBill += productPrice * quantity;
                        }
                    }

                    // Update the total bill TextView
                    totalBillTextView.setText(String.format("Total Bill: ₹%.2f", totalBill));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(AboutUsActivity.this, "Failed to load total bill", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
