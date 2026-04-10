package com.example.projectmedbill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private List<CartItem> cartItems;
    private DeleteItemListener deleteItemListener;

    public CartAdapter(Context context, List<CartItem> cartItems, DeleteItemListener deleteItemListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.deleteItemListener = deleteItemListener;
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
            holder.productDescription = convertView.findViewById(R.id.productDescription);
            holder.productExpiryDate = convertView.findViewById(R.id.productExpiryDate);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = (CartItem) getItem(position);

        // Set values for cart item
        holder.productName.setText(cartItem.getName() != null ? cartItem.getName() : "Unknown Product");
        holder.productPrice.setText(cartItem.getPrice() > 0 ? String.format("₹%.2f", cartItem.getPrice()) : "₹0.00");
        holder.productQuantity.setText(context.getString(R.string.quantity_label, cartItem.getQuantity()));
        holder.productDescription.setText(cartItem.getDescription() != null ? cartItem.getDescription() : "No description");
        holder.productExpiryDate.setText(cartItem.getExpiryDate() != null ? cartItem.getExpiryDate() : "No expiry date");

        // Set up the delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteItemListener != null) {
                deleteItemListener.onItemDeleted(cartItem); // Notify the listener to delete the item
            }
        });

        return convertView;
    }

    // Update cart items
    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newCartItems);
        notifyDataSetChanged();
    }

    // ViewHolder class to hold the views
    static class ViewHolder {
        TextView productName;
        TextView productPrice;
        TextView productQuantity;
        TextView productDescription;
        TextView productExpiryDate;
        Button deleteButton;
    }

    // Listener interface for delete button click
    public interface DeleteItemListener {
        void onItemDeleted(CartItem item);
    }
}
