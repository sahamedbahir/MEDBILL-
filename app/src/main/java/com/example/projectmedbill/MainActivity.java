package com.example.projectmedbill;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem; // Add this import statement

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.util.List;
import androidx.annotation.NonNull;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Button pillsButton, syrupButton, medicalCareButton, viewCartButton, generatePdfButton;
    private FirebaseAuth mAuth;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        cartManager = CartManager.getInstance();

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer Layout and Navigation View setup
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ActionBarDrawerToggle setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        initializeUI();

        // Set user info in the navigation header
        setUserInfoInNavigationHeader(navigationView);

        // Button click listeners
        pillsButton.setOnClickListener(v -> openProductList("Pills"));
        syrupButton.setOnClickListener(v -> openProductList("Syrup"));
        medicalCareButton.setOnClickListener(v -> openProductList("Medical Care"));
        viewCartButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        generatePdfButton.setOnClickListener(v -> generatePDF());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to login if no user is signed in
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeUI() {
        pillsButton = findViewById(R.id.pillsbtn);
        syrupButton = findViewById(R.id.syrupbtn);
        medicalCareButton = findViewById(R.id.medicalcarebtn);
        viewCartButton = findViewById(R.id.cartbtn);
        generatePdfButton = findViewById(R.id.generatePdfBtn); // Add this line for the generate PDF button
    }

    private void setUserInfoInNavigationHeader(NavigationView navigationView) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User";
            String userEmail = currentUser.getEmail();

            // Find the TextViews in the navigation header
            TextView userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.user_name);
            TextView userEmailTextView = navigationView.getHeaderView(0).findViewById(R.id.user_email);

            // Set the user info
            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);
        }
    }

    private void openProductList(String category) {
        Intent intent = new Intent(MainActivity.this, ProductList.class);
        intent.putExtra("category", category);  // Pass the category to ProductList
        startActivity(intent);
    }

    private void logout() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_Terms) {
            Intent intent = new Intent(MainActivity.this, TermsandconditionsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Generate PDF when the button is clicked
    private void generatePDF() {
        // Load cart items from Firebase
        cartManager.loadCartFromFirebase(new CartManager.CartLoadCallback() {
            @Override
            public void onCartLoaded(List<CartItem> loadedCartItems) {
                // File path for PDF
                String fileName = getExternalFilesDir(null) + "/Bill.pdf";

                // Create a PdfWriter object
                try {
                    PdfWriter writer = new PdfWriter(fileName);
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf);

                    // Add content to the PDF
                    document.add(new Paragraph("Bill for Order"));
                    document.add(new Paragraph("Date: " + System.currentTimeMillis()));

                    // Loop through the cart items and add to the PDF
                    double totalAmount = 0.0;
                    for (CartItem item : loadedCartItems) {
                        document.add(new Paragraph(item.getName() + " - ₹" + item.getTotalPrice()));

                        totalAmount += item.getTotalPrice();
                    }

                    // Add total
                    document.add(new Paragraph("Total: ₹" + totalAmount));

                    // Close the document
                    document.close();

                    Toast.makeText(MainActivity.this, "PDF Generated at " + fileName, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCartLoadFailed(Exception e) {
                Toast.makeText(MainActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
