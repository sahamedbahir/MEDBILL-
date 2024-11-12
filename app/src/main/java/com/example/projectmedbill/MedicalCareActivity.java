package com.example.projectmedbill;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MedicalCareActivity extends AppCompatActivity {
    private LinearLayout productContainer;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_care);

        productContainer = findViewById(R.id.productContainer);
        productList = new ArrayList<>();

        fetchProductsFromFirebase();
    }

    private void fetchProductsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products/MedicalCare");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productContainer.removeAllViews();
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                    addProductView(product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void addProductView(Product product) {
        View productView = LayoutInflater.from(this).inflate(R.layout.activity_product_item, null);

        TextView productName = productView.findViewById(R.id.productName);
        TextView productPrice = productView.findViewById(R.id.productPrice);
        Button addToCartButton = productView.findViewById(R.id.addToCartButton);

        productName.setText(product.getName());
        productPrice.setText(String.valueOf(product.getPrice()));

        addToCartButton.setOnClickListener(v -> addToCart(product));

        productContainer.addView(productView);
    }

    private void addToCart(Product product) {
        // Logic to add product to cart (e.g., via a CartManager class or Firebase)
    }
}
