package com.example.projectmedbill;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Button pillsButton, syrupButton, medicalCareButton, viewCartButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

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
        } else if (id == R.id.nav_bill) {
            Intent intent = new Intent(MainActivity.this, BillActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // Corrected the indentation here
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
}
