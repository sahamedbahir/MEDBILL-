package com.example.projectmedbill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductList extends AppCompatActivity {
    private List<Product> productList = new ArrayList<>();
    private ListView listView;
    private ProductAdapter adapter;
    private ProgressBar loadingIndicator;
    private DatabaseReference productDatabase;
    private String selectedCategory;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        listView = findViewById(R.id.productListView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        // Initialize the adapter
        adapter = new ProductAdapter(this, productList, (product, quantity) -> {
            if (quantity <= 0) {
                Toast.makeText(ProductList.this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            CartItem cartItem = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity,
                    product.getDescription(),
                    product.getExpiryDate()
            );

            CartManager.getInstance().addToCart(cartItem);
            Toast.makeText(ProductList.this, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        });

        listView.setAdapter(adapter);
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");
        selectedCategory = getIntent().getStringExtra("category");

        // Load products from Firebase
        loadProductsFromFirebase();

        // Set up cart button listener
        ImageButton cartButton = findViewById(R.id.cartButton);
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductList.this, CartActivity.class);
            startActivity(intent);
        });

        // Set up home button listener
        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> finish()); // Go back to the previous screen
    }

    private void loadProductsFromFirebase() {
        loadingIndicator.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);

        productDatabase.orderByChild("type").equalTo(selectedCategory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                if (productList.isEmpty()) {
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    emptyStateTextView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                loadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(ProductList.this, "Failed to load products: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
