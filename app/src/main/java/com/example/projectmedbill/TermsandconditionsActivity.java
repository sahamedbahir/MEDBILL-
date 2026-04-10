package com.example.projectmedbill;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TermsandconditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions); // Make sure this matches your XML filename

        // Setup the Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Back button handling
        findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to the previous activity
                onBackPressed();
            }
        });

        // Handle Accept Button Click
        Button acceptButton = findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle acceptance logic, for example, show a success message or navigate to another activity
                Toast.makeText(TermsandconditionsActivity.this, "Terms Accepted", Toast.LENGTH_SHORT).show();
                // Navigate to next screen (e.g., MainActivity)
                startActivity(new Intent(TermsandconditionsActivity.this, MainActivity.class));
                finish(); // Optionally close the current activity
            }
        });

        // Handle Decline Button Click
        Button declineButton = findViewById(R.id.decline_button);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle decline logic, for example, close the app or navigate to another screen
                Toast.makeText(TermsandconditionsActivity.this, "You have declined the terms", Toast.LENGTH_SHORT).show();
                finish(); // Optionally close the current activity
            }
        });
    }
}
