package com.example.hamburger;

public class Product {
    final private String _name;
    private double _price = 0.0f;

    public Product(String name) {
        _name = name;
    }

    final public String getName() {
        return _name;
    }

    public void setPrice(double price) {
        _price = price;
    }

    final public double getPrice() {
        return _price;
    }

    public boolean isMeal() {
        return false;
    }
}
