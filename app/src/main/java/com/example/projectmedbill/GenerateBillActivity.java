package com.example.projectmedbill;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import android.graphics.pdf.PdfDocument;


import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class GenerateBillActivity extends AppCompatActivity {

    private List<CartItem> cartItems; // Your cart items
    private double totalAmount; // Total amount for the bill

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill);

        // Fetch cart items from Firebase
        CartManager.getInstance().loadCartFromFirebase(new CartManager.CartLoadCallback() {
            @Override
            public void onCartLoaded(List<CartItem> loadedCartItems) {
                cartItems = loadedCartItems; // Update cartItems with the loaded data
                totalAmount = CartManager.getInstance().calculateTotal(cartItems); // Calculate total amount
                generatePDF(); // Generate PDF after loading cart items
            }

            @Override
            public void onCartLoadFailed(Exception e) {
                // Handle the error, maybe show a Toast
                Log.e("GenerateBillActivity", "Error loading cart: ", e);
                Toast.makeText(GenerateBillActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        // Title
        canvas.drawText("MEDBILL Invoice", 200, 40, paint);

        // Date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        canvas.drawText("Date: " + currentDate, 20, 60, paint);

        // Add cart items
        float yPosition = 80; // Starting position for cart items
        for (CartItem item : cartItems) {
            String line = item.getName() + " - " + item.getQuantity() + " x " + item.getPrice() + " = " + (item.getQuantity() * item.getPrice());
            canvas.drawText(line, 20, yPosition, paint);
            yPosition += 20; // Move down the line
        }

        // Total Amount
        canvas.drawText("Total: " + totalAmount, 20, yPosition + 20, paint);

        pdfDocument.finishPage(page);

        // Save the PDF to a file
        File file = new File(getExternalFilesDir(null), "Bill_" + currentDate + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Bill generated successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("PDF", "Error generating PDF", e);
            Toast.makeText(this, "Error generating bill", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }
}

