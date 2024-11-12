package com.example.projectmedbill;

import java.io.Serializable;
import java.util.ArrayList;

public class BillClass implements Serializable {
    private ArrayList<CartItem> items;
    private double totalAmount;

    // Constructor
    public BillClass(ArrayList<CartItem> items) {
        this.items = items;
        this.totalAmount = calculateTotal();
    }

    // Calculate total amount
    private double calculateTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // Getters
    public ArrayList<CartItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
