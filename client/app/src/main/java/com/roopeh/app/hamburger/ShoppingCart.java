package com.roopeh.app.hamburger;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    final private List<ShoppingItem> _items;

    public ShoppingCart() {
        _items = new ArrayList<>();
    }

    final public List<ShoppingItem> getItems() {
        return _items;
    }

    final public boolean isCartEmpty() {
        return _items.isEmpty();
    }

    public void addToCart(ShoppingItem item) {
        _items.add(item);
    }

    public void removeFromCart(ShoppingItem item) {
        _items.remove(item);
    }

    public void emptyCart() {
        _items.clear();
    }
}
