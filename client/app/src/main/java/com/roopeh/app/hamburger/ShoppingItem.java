package com.roopeh.app.hamburger;

// This is the chosen meal or product inserted into shopping cart (ShoppingCart)
public class ShoppingItem {
    final private Product _product;
    private double _price;
    private int _mealDrink = 0;
    private int _mealExtra = 0;
    private boolean _largeDrink = false;
    private boolean _largeExtra = false;

    public ShoppingItem(Product product) {
        _product = product;
        _price = _product.getPrice();
    }

    final public Product getProduct() {
        return _product;
    }

    public void setPrice(double price) {
        _price = price;
    }

    final public double getPrice() {
        return _price;
    }

    public void setMealDrink(int drink, boolean large) {
        if (!_product.isMeal())
            return;

        _mealDrink = drink;
        _largeDrink = large;
    }

    final public int getMealDrink() {
        return _mealDrink;
    }

    final public boolean isLargeDrink() {
        return _largeDrink;
    }

    public void setMealExtra(int extra, boolean large) {
        if (!_product.isMeal())
            return;

        _mealExtra = extra;
        _largeExtra = large;
    }

    final public int getMealExtra() {
        return _mealExtra;
    }

    final public boolean isLargeExtra() {
        return _largeExtra;
    }
}
