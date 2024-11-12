package com.example.projectmedbill;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class BillActivity extends AppCompatActivity {
    private ListView billListView;
    private TextView totalTextView;
    private ArrayList<CartItem> cartItems;
    private Button downloadBillButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill); // Ensure this layout exists

        // Initialize views
        billListView = findViewById(R.id.billListView);
        totalTextView = findViewById(R.id.totalTextView);
        downloadBillButton = findViewById(R.id.downloadBillButton);
        backButton = findViewById(R.id.backButton);

        // Set up back button functionality
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity
            }
        });

        // Set up download button functionality
        downloadBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBill();
            }
        });

        // Get cart items from intent
        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");

        // Check if cart items are empty
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "No items in the cart.", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if no items
            return;
        }

        // Populate the ListView with bill details
        populateBillList(cartItems);

        // Calculate total and display
        double totalAmount = calculateTotal(cartItems);
        totalTextView.setText(String.format("Total: ₹%.2f", totalAmount));
    }

    private void populateBillList(ArrayList<CartItem> items) {
        ArrayList<String> billDisplayList = new ArrayList<>();

        for (CartItem item : items) {
            String displayString = item.getName() + " (x" + item.getQuantity() + ") - ₹" + String.format("%.2f", item.getTotalPrice());
            billDisplayList.add(displayString);
        }

        // Use ArrayAdapter to display items in ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, billDisplayList);
        billListView.setAdapter(adapter);
    }

    private double calculateTotal(ArrayList<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    private void downloadBill() {
        // Implement the logic to download the bill as a file (e.g., PDF generation or text file)
        Toast.makeText(this, "Bill downloaded successfully", Toast.LENGTH_SHORT).show();
    }
}
