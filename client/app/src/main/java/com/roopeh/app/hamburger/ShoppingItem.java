package com.roopeh.app.hamburger;

// This is the chosen meal or product inserted into shopping cart (ShoppingEvent)
public class ShoppingItem {
    final private Product _product;
    private int _mealDrink = 0;
    private int _mealExtra = 0;

    public ShoppingItem(Product product) {
        _product = product;
    }

    final public Product getProduct() {
        return _product;
    }

    public void setMealDrink(int drink) {
        if (!_product.isMeal())
            return;

        _mealDrink = drink;
    }

    final public int getMealDrink() {
        return _mealDrink;
    }

    public void setMealExtra(int extra) {
        if (!_product.isMeal())
            return;

        _mealExtra = extra;
    }

    final public int getMealExtra() {
        return _mealExtra;
    }
}
