package com.example.projectmedbill;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> implements Filterable {

    private final List<Product> originalProducts; // Original list for restoring data
    private List<Product> filteredProducts; // Filtered list to display
    private final OnProductClickListener listener;

    // Define the interface for product click events
    public interface OnProductClickListener {
        void onAddToCartClick(Product product, int quantity);
    }

    public ProductAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        super(context, 0, products);
        this.originalProducts = new ArrayList<>(products); // Save original product list
        this.filteredProducts = products; // Initial display list
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return filteredProducts.size();
    }

    @Override
    public Product getItem(int position) {
        return filteredProducts.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_product_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Product product = getItem(position);
        if (product != null) {
            bindProductData(viewHolder, product);
            viewHolder.addToCartButton.setOnClickListener(v -> listener.onAddToCartClick(product, 1));
        }

        return convertView;
    }

    private void bindProductData(ViewHolder viewHolder, Product product) {
        viewHolder.nameTextView.setText(product.getName());
        viewHolder.priceTextView.setText(String.format("Price: ₹%.2f", product.getPrice()));

        String description = getNonNullString(product.getDescription(), "No description available");
        String manufactureDate = getNonNullString(product.getManufactureDate(), "Not provided");
        String expiryDate = getNonNullString(product.getExpiryDate(), "Not provided");

        viewHolder.descriptionTextView.setText("Description: " + description);
        viewHolder.manufactureDateTextView.setText("Manufacture Date: " + manufactureDate);
        viewHolder.expiryDateTextView.setText("Expiry Date: " + expiryDate);

        Log.d("ProductAdapter", "Displaying product: " + product.toString());
    }

    // Helper method for null or empty string checks
    private String getNonNullString(String input, String defaultValue) {
        return (input == null || input.isEmpty()) ? defaultValue : input;
    }

    // ViewHolder pattern for better performance
    private static class ViewHolder {
        final TextView nameTextView;
        final TextView priceTextView;
        final TextView descriptionTextView;
        final TextView manufactureDateTextView;
        final TextView expiryDateTextView;
        final Button addToCartButton;

        ViewHolder(View view) {
            nameTextView = view.findViewById(R.id.productName);
            priceTextView = view.findViewById(R.id.productPrice);
            descriptionTextView = view.findViewById(R.id.productDescription);
            manufactureDateTextView = view.findViewById(R.id.productManufactureDate);
            expiryDateTextView = view.findViewById(R.id.productExpiryDate);
            addToCartButton = view.findViewById(R.id.addToCartButton);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Product> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalProducts); // No filter applied
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Product product : originalProducts) {
                        if (product.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(product);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredProducts = (List<Product>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
