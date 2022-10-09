package com.example.bluetooth_print;

public class CartModel {
    String name;
    double price;
    int quantity;

    public CartModel(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
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

    public double getTotal() {
        return quantity * price;
    }

    public String forReceipt() {
        String format = "%-15s%5s%6s%6s";
        return String.format(format, name, price, "x" + quantity, getTotal());
    }
}
