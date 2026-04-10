package com.example.projectmedbill;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BillActivity extends AppCompatActivity {

    private TextView totalTextView;
    private TableLayout billTableLayout;
    private Button downloadBillButton;
    private Button sendBillButton;
    private ImageView backButton;
    private ArrayList<CartItem> cartItems;

    private String adminEmail = "ahamedbahir534@gmail.com"; // Hardcoded admin email or fetch from Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        // Initialize views
        billTableLayout = findViewById(R.id.billTableLayout);
        totalTextView = findViewById(R.id.totalTextView);
        downloadBillButton = findViewById(R.id.downloadBillButton);
        sendBillButton = findViewById(R.id.sendEmailButton);
        backButton = findViewById(R.id.backButton);

        // Set up back button functionality
        backButton.setOnClickListener(v -> finish());

        // Set up download button functionality
        downloadBillButton.setOnClickListener(v -> downloadBill());

        // Set up send bill button functionality
        sendBillButton.setOnClickListener(v -> sendBill());

        // Retrieve cart items from the intent
        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "No items in the cart.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate the TableLayout with bill items
        populateBillTable(cartItems);

        // Calculate total and display
        double totalAmount = calculateTotal(cartItems);
        totalTextView.setText(String.format("Total: ₹%.2f", totalAmount));
    }

    private void populateBillTable(ArrayList<CartItem> items) {
        for (CartItem item : items) {
            TableRow row = new TableRow(this);

            // Item name column
            TextView itemName = new TextView(this);
            itemName.setText(item.getName());
            itemName.setPadding(8, 8, 8, 8);

            // Quantity column
            TextView itemQuantity = new TextView(this);
            itemQuantity.setText(String.valueOf(item.getQuantity()));
            itemQuantity.setPadding(8, 8, 8, 8);

            // Rate column
            TextView itemRate = new TextView(this);
            itemRate.setText("₹" + String.format("%.2f", item.getTotalPrice() / item.getQuantity()));
            itemRate.setPadding(8, 8, 8, 8);

            // Add columns to the row
            row.addView(itemName);
            row.addView(itemQuantity);
            row.addView(itemRate);

            // Add the row to the table layout
            billTableLayout.addView(row);
        }
    }

    private double calculateTotal(ArrayList<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    private void sendBill() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = user.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                File pdfFile = generatePdf(cartItems);
                if (pdfFile != null) {
                    Log.d("BillActivity", "Generating and sending bill...");
                    EmailSender.sendEmailWithAttachment(userEmail, "Your Bill from MEDBILL", "Please find your bill attached.", pdfFile.getAbsolutePath());
                    EmailSender.sendEmailWithAttachment(adminEmail, "A Bill from MEDBILL", "A new bill has been generated. Please find the details attached.", pdfFile.getAbsolutePath());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(BillActivity.this, "Bill sent via email to both customer and admin!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private File generatePdf(ArrayList<CartItem> cartItems) {
        // Implement your PDF generation logic here
        File pdfFile = new File(getExternalFilesDir(null), "bill.pdf");
        return pdfFile;
    }

    private void downloadBill() {
        File pdfFile = generatePdf(cartItems);

        if (pdfFile == null) {
            Toast.makeText(this, "Failed to generate bill PDF.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore to save PDF in the Downloads directory on Android 10+
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "MEDBILL_Bill_" + System.currentTimeMillis() + ".pdf");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            try {
                OutputStream outputStream = getContentResolver().openOutputStream(getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues));
                if (outputStream != null) {
                    FileInputStream inputStream = new FileInputStream(pdfFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    outputStream.close();
                    Toast.makeText(this, "Bill downloaded to Downloads folder", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving bill to Downloads", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Fallback for Android 9 and below
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File downloadedPdfFile = new File(downloadsDir, "MEDBILL_Bill_" + System.currentTimeMillis() + ".pdf");

            try {
                FileInputStream inputStream = new FileInputStream(pdfFile);
                FileOutputStream outputStream = new FileOutputStream(downloadedPdfFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();
                Toast.makeText(this, "Bill downloaded to Downloads folder", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving bill to Downloads", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
