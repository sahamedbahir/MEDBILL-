package com.example.projectmedbill;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String id; // Unique identifier for the item
    private String name; // Name of the product
    private double price; // Price per unit of the product
    private int quantity; // Quantity of the product in the cart
    private String description; // Description of the product
    private String expiryDate; // Expiry date of the product

    // Default constructor for Firebase
    public CartItem() {
        this("", "", 0.0, 0, "", ""); // Initialize with default values
    }

    public CartItem(String id, String name, double price, int quantity, String description, String expiryDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        setQuantity(quantity); // Use setter for quantity to ensure validation
        this.description = description;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public double getTotalPrice() {
        return price * quantity; // Calculate total price
    }

    // Increase quantity by a positive amount
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    // Decrease quantity, ensuring it doesn't go below 0
    public void decreaseQuantity(int amount) {
        if (amount > 0 && quantity > amount) {
            this.quantity -= amount;
        } else if (quantity == amount) {
            this.quantity = 0; // Set to 0 if the full quantity is removed
        }
    }

    // Setter for quantity with validation
    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                '}';
    }

    // Override equals method to compare CartItems based on ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CartItem)) return false;
        CartItem other = (CartItem) obj;
        return id.equals(other.id);
    }

    // Override hashCode to generate a hash code based on ID
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
