package com.example.projectmedbill;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderManager {

    private DatabaseReference orderDatabase;

    public OrderManager() {
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
    }

    // Create a new order with the provided BillClass and an optional orderId
    public void createOrder(BillClass bill, String orderId, OrderCallback callback) {
        // Validate the bill object
        if (bill == null) {
            Log.e("OrderManager", "Cannot create order: BillClass is null.");
            callback.onOrderFailed(new Exception("BillClass cannot be null"));
            return;
        }

        // Generate a unique order ID using push() if the provided orderId is null or empty
        final String finalOrderId; // Declare final variable for inner class access
        if (orderId == null || orderId.isEmpty()) {
            finalOrderId = orderDatabase.push().getKey();
        } else {
            finalOrderId = orderId; // Assign existing orderId if provided
        }

        // Ensure the order ID is valid
        if (finalOrderId != null) {
            orderDatabase.child(finalOrderId).setValue(bill)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("OrderManager", "Order created successfully: " + finalOrderId);
                                callback.onOrderCreated(finalOrderId);
                            } else {
                                Log.e("OrderManager", "Failed to create order: " + task.getException());
                                callback.onOrderFailed(task.getException());
                            }
                        }
                    });
        } else {
            Log.e("OrderManager", "Failed to generate a valid order ID.");
            callback.onOrderFailed(new Exception("Failed to generate order ID"));
        }
    }

    // Callback interface for order creation result
    public interface OrderCallback {
        void onOrderCreated(String orderId);
        void onOrderFailed(Exception e);
    }
}
