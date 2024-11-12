package com.example.projectmedbill;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Serializable {
    private String userId; // Associate the cart with a user
    private Product product;
    private int quantity;

    // Default constructor required for calls to DataSnapshot.getValue(Cart.class)
    public Cart() {
        // Default constructor for Firebase
    }

    public Cart(String userId, Product product, int quantity) {
        this.userId = userId;
        this.product = product;
        setQuantity(quantity); // Use setter for validation
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity; // Assuming Product has a getPrice() method
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("product", product); // Ensure Product class is serializable and compatible
        result.put("quantity", quantity);
        return result;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "userId='" + userId + '\'' +
                ", product=" + product.getName() +
                ", quantity=" + quantity +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cart)) return false;

        Cart cart = (Cart) obj;
        return quantity == cart.quantity && product.equals(cart.product);
    }

    @Override
    public int hashCode() {
        return 31 * product.hashCode() + quantity;
    }
}
