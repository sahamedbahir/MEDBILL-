package com.example.projectmedbill;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class PdfGenerator {

    // Generate the PDF file
    public static Uri generateBillPdf(Context context, List<CartItem> cartItems, double totalAmount) {
        // Check Android version for storage access
        Uri pdfUri = null;

        // Create a unique filename (e.g., based on timestamp)
        String fileName = "generated_bill_" + System.currentTimeMillis() + ".pdf";

        // For Android 10 (API level 29) and above, use MediaStore for saving PDFs to public storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS); // Save to Downloads folder

            // Insert into the MediaStore and get the Uri
            Uri pdfCollection = MediaStore.Files.getContentUri("external");
            pdfUri = context.getContentResolver().insert(pdfCollection, values);

            try (OutputStream outputStream = context.getContentResolver().openOutputStream(pdfUri)) {
                if (outputStream != null) {
                    // Initialize the PDF writer and document
                    PdfWriter writer = new PdfWriter(outputStream);
                    PdfDocument pdfDoc = new PdfDocument(writer);
                    Document document = new Document(pdfDoc);

                    // Add the title of the bill
                    document.add(new Paragraph("MEDBILL - Invoice")
                            .setBold().setFontSize(18));
                    document.add(new Paragraph("******************************"));

                    // Create a table for the cart items (for better formatting)
                    float[] columnWidths = {3, 1, 2};  // Name, Quantity, Price
                    Table table = new Table(columnWidths);
                    table.addCell(new Cell().add(new Paragraph("Product Name")));
                    table.addCell(new Cell().add(new Paragraph("Qty")));
                    table.addCell(new Cell().add(new Paragraph("Price")));

                    // Add cart items to the table
                    for (CartItem item : cartItems) {
                        table.addCell(new Cell().add(new Paragraph(item.getName())));
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                        table.addCell(new Cell().add(new Paragraph("₹" + item.getTotalPrice())));
                    }

                    // Add the table to the document
                    document.add(table);

                    // Add total amount to the PDF
                    document.add(new Paragraph("******************************"));
                    document.add(new Paragraph("Total: ₹" + totalAmount)
                            .setBold().setFontSize(14));

                    // Close the document to save the file
                    document.close();

                    // Inform the user that the PDF has been saved
                    Toast.makeText(context, "PDF saved to Downloads folder", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Fallback for older versions: save to legacy external storage (before Android 10)
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File pdfFile = new File(directory, fileName);

            try {
                PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                // Add the title of the bill
                document.add(new Paragraph("MEDBILL - Invoice")
                        .setBold().setFontSize(18));
                document.add(new Paragraph("******************************"));

                // Create a table for the cart items (for better formatting)
                float[] columnWidths = {3, 1, 2};  // Name, Quantity, Price
                Table table = new Table(columnWidths);
                table.addCell(new Cell().add(new Paragraph("Product Name")));
                table.addCell(new Cell().add(new Paragraph("Qty")));
                table.addCell(new Cell().add(new Paragraph("Price")));

                // Add cart items to the table
                for (CartItem item : cartItems) {
                    table.addCell(new Cell().add(new Paragraph(item.getName())));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                    table.addCell(new Cell().add(new Paragraph("₹" + item.getTotalPrice())));
                }

                // Add the table to the document
                document.add(table);

                // Add total amount to the PDF
                document.add(new Paragraph("******************************"));
                document.add(new Paragraph("Total: ₹" + totalAmount)
                        .setBold().setFontSize(14));

                // Close the document to save the file
                document.close();

                // Inform the user that the PDF has been saved
                Toast.makeText(context, "PDF saved to Downloads folder", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show();
            }
        }

        return pdfUri;
    }
}
