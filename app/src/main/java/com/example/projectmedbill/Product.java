package com.example.projectmedbill;

public class Product {
    private String id; // Product ID
    private String name;
    private double price;
    private String type; // Use 'type' or 'category' based on your database structure
    private String description; // Product description
    private String manufactureDate; // New field for manufacture date
    private String expiryDate; // New field for expiry date

    // Default constructor required for calls to DataSnapshot.getValue(Product.class)
    public Product() {}

    // Constructor with all fields
    public Product(String id, String name, double price, String type, String description, String manufactureDate, String expiryDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
        this.manufactureDate = manufactureDate; // Initialize manufacture date
        this.expiryDate = expiryDate; // Initialize expiry date
    }

    // Constructor without description and dates
    public Product(String id, String name, double price, String type) {
        this(id, name, price, type, "", "", ""); // Default to empty strings if not provided
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type; // Adjust to 'category' if needed
    }

    public String getDescription() {
        return description;
    }

    public String getManufactureDate() {
        return manufactureDate; // Getter for manufacture date
    }

    public String getExpiryDate() {
        return expiryDate; // Getter for expiry date
    }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', price=" + price + ", type='" + type + "', manufactureDate='" + manufactureDate + "', expiryDate='" + expiryDate + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id.equals(product.id); // Compare by ID or add more fields if necessary
    }

    @Override
    public int hashCode() {
        return id.hashCode(); // Use ID for hash code
    }
}
