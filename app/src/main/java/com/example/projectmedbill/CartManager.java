package com.example.projectmedbill;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private DatabaseReference cartRef;

    private CartManager() {
        // Private constructor to prevent instantiation
        cartRef = FirebaseDatabase.getInstance().getReference("carts");
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Method to add an item to the cart in Firebase
    public void addToCart(CartItem cartItem) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            cartRef.child(currentUser.getUid()).child(cartItem.getId()).setValue(cartItem);
        }
    }

    // Method to load cart items from Firebase
    public void loadCartFromFirebase(CartLoadCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            cartRef.child(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<CartItem> cartItems = new ArrayList<>();
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CartItem cartItem = snapshot.getValue(CartItem.class);
                            if (cartItem != null) {
                                cartItems.add(cartItem);
                            }
                        }
                    }
                    callback.onCartLoaded(cartItems);
                } else {
                    callback.onCartLoadFailed(task.getException());
                }
            });
        }
    }

    // Method to calculate the total amount of the cart
    public double calculateTotal(List<CartItem> cartItems) {
        double total = 0.0;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getTotalPrice(); // Calculate total price for each item (price * quantity)
        }
        return total;
    }

    // Interface to handle the callback for loading cart items
    public interface CartLoadCallback {
        void onCartLoaded(List<CartItem> loadedCartItems);
        void onCartLoadFailed(Exception e);
    }
}
