package com.example.projectmedbill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private ImageView backButton;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        mAuth = FirebaseAuth.getInstance();
        cartListView = findViewById(R.id.cartListView);
        totalTextView = findViewById(R.id.totalTextView);
        generateBillButton = findViewById(R.id.generateBillButton);
        backButton = findViewById(R.id.backButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

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
                intent.putExtra("cartItems", (ArrayList<CartItem>) cartItems);
                startActivity(intent);
            } else {
                Toast.makeText(CartActivity.this, "Cart is empty. Cannot generate bill.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartItems() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);  // Show the loading indicator
            cartManager.loadCartFromFirebase(new CartManager.CartLoadCallback() {
                @Override
                public void onCartLoaded(List<CartItem> loadedCartItems) {
                    loadingProgressBar.setVisibility(View.GONE);  // Hide the loading indicator
                    cartItems.clear();
                    cartItems.addAll(loadedCartItems);
                    updateCartDisplay();
                }

                @Override
                public void onCartLoadFailed(Exception e) {
                    loadingProgressBar.setVisibility(View.GONE);  // Hide the loading indicator
                    Toast.makeText(CartActivity.this, "Failed to load cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    cartItems.clear();
                    updateCartDisplay();
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
            cartListView.setAdapter(null);
        } else {
            if (cartAdapter == null) {
                cartAdapter = new CartAdapter(this, cartItems, new CartAdapter.DeleteItemListener() {
                    @Override
                    public void onItemDeleted(CartItem item) {
                        deleteItemFromCart(item);
                    }
                });
                cartListView.setAdapter(cartAdapter);
            } else {
                cartAdapter.updateCartItems(cartItems);
            }

            double totalAmount = cartManager.calculateTotal(cartItems);
            totalTextView.setText("Total: ₹" + String.format("%.2f", totalAmount));
        }
    }

    private void deleteItemFromCart(CartItem item) {
        cartManager.removeItemFromCart(item, new CartManager.CartUpdateCallback() {
            @Override
            public void onCartUpdated() {
                cartItems.remove(item);
                updateCartDisplay();
                Toast.makeText(CartActivity.this, "Item removed from cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCartUpdateFailed(Exception e) {
                Toast.makeText(CartActivity.this, "Failed to remove item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
