package com.roopeh.app.hamburger;

public class Product {
    final private int _id;
    final private String _name;
    private double _price = 0.0f;
    private String _koostumus;
    private String _ravinto;

    public Product(int id, String name) {
        _id = id;
        _name = name;
    }

    final public int getId() {
        return _id;
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

    public void setRavinto(String str) {
        _ravinto = str;
    }

    final public String getRavinto() {
        return _ravinto;
    }

    public void setKoostumus(String str) {
        _koostumus = str;
    }

    final public String getKoostumus() {
        return _koostumus;
    }

    public boolean isMeal() {
        return false;
    }
}
