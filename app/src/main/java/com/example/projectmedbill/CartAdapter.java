package com.example.projectmedbill;

import android.content.Context;
import android.util.Log; // Import Log for debugging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_cart_item, parent, false);
            holder = new ViewHolder();
            holder.productName = convertView.findViewById(R.id.productName);
            holder.productPrice = convertView.findViewById(R.id.productPrice);
            holder.productQuantity = convertView.findViewById(R.id.productQuantity);
            holder.productDescription = convertView.findViewById(R.id.productDescription); // Description TextView
            holder.productExpiryDate = convertView.findViewById(R.id.productExpiryDate); // Expiry Date TextView
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = (CartItem) getItem(position);

        // Debugging logs
        Log.d("CartAdapter", "Item Position: " + position + ", Name: " + cartItem.getName() +
                ", Price: " + cartItem.getPrice() + ", Quantity: " + cartItem.getQuantity() +
                ", Description: " + cartItem.getDescription() + ", Expiry Date: " + cartItem.getExpiryDate());

        // Set text safely to avoid null values
        holder.productName.setText(cartItem.getName() != null ? cartItem.getName() : "Unknown Product");
        holder.productPrice.setText(cartItem.getPrice() > 0 ? String.format("₹%.2f", cartItem.getPrice()) : "₹0.00");
        holder.productQuantity.setText(context.getString(R.string.quantity_label, cartItem.getQuantity()));
        holder.productDescription.setText(cartItem.getDescription() != null ? cartItem.getDescription() : "No description");
        holder.productExpiryDate.setText(cartItem.getExpiryDate() != null ? cartItem.getExpiryDate() : "No expiry date");

        return convertView;
    }

    static class ViewHolder {
        TextView productName;
        TextView productPrice;
        TextView productQuantity;
        TextView productDescription; // Add description TextView
        TextView productExpiryDate; // Add expiry date TextView
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newCartItems);
        notifyDataSetChanged();
    }
}
