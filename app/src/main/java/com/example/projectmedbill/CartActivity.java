package com.example.projectmedbill;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ListView cartListView;
    private TextView totalTextView;
    private Button generateBillButton;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private FirebaseAuth mAuth;
    private CartManager cartManager;
    private ImageView backButton;  // Back button ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        mAuth = FirebaseAuth.getInstance();
        cartListView = findViewById(R.id.cartListView);
        totalTextView = findViewById(R.id.totalTextView);
        generateBillButton = findViewById(R.id.generateBillButton);
        backButton = findViewById(R.id.backButton);  // Find back button by ID

        cartItems = new ArrayList<>();
        cartManager = CartManager.getInstance();

        // Load cart items from Firebase
        loadCartItems();

        // Set up the back button functionality
        backButton.setOnClickListener(v -> finish());

        // Button to generate the bill
        generateBillButton.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                Intent intent = new Intent(CartActivity.this, BillActivity.class);
                intent.putExtra("cartItems", (ArrayList<CartItem>) cartItems); // Passing the cart items to BillActivity
                startActivity(intent);
            } else {
                Toast.makeText(CartActivity.this, "Cart is empty. Cannot generate bill.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartItems() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            cartManager.loadCartFromFirebase(new CartManager.CartLoadCallback() {
                @Override
                public void onCartLoaded(List<CartItem> loadedCartItems) {
                    cartItems.clear(); // Clear existing items before adding new ones
                    cartItems.addAll(loadedCartItems);
                    updateCartDisplay(); // Update display to reflect new cart items
                }

                @Override
                public void onCartLoadFailed(Exception e) {
                    Toast.makeText(CartActivity.this, "Failed to load cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    cartItems.clear(); // Clear the cart items on load failure
                    updateCartDisplay(); // Update display to reflect the empty cart
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateCartDisplay() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            totalTextView.setText("Total: ₹0.00");
            cartListView.setAdapter(null); // Clear adapter if empty
        } else {
            // Initialize the adapter only once
            if (cartAdapter == null) {
                cartAdapter = new CartAdapter(this, cartItems);
                cartListView.setAdapter(cartAdapter);
            } else {
                cartAdapter.updateCartItems(cartItems); // Update the existing adapter with new items
            }

            double totalAmount = cartManager.calculateTotal(cartItems); // Calculate total amount using CartManager
            totalTextView.setText("Total: ₹" + String.format("%.2f", totalAmount));
        }
    }
}
