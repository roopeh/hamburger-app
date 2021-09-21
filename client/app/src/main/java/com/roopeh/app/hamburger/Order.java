package com.roopeh.app.hamburger;

import java.util.ArrayList;
import java.util.List;

public class Order {
    final private List<ShoppingItem> _items;
    private long _orderDate;
    private Restaurant _restaurant = null;
    private boolean _isPaid = false;

    private double _originalPrice = 0.0;
    private double _discountPrice = 0.0;
    private double _totalPrice = 0.0;

    public Order(List<ShoppingItem> items) {
        _items = new ArrayList<>();
        _items.addAll(items);
    }

    final public List<ShoppingItem> getItems() {
        return _items;
    }

    public void setDate(long date) {
        _orderDate = date;
    }

    final public long getDate() {
        return _orderDate;
    }

    public void setRestaurant(Restaurant res) {
        _restaurant = res;
    }

    final public Restaurant getRestaurant() {
        return _restaurant;
    }

    public void setPaid(boolean paid) {
        _isPaid = paid;
    }

    final public boolean isPaid() {
        return _isPaid;
    }

    public void setPrices(double originalPrice, double discountPrice, double totalPrice) {
        _originalPrice = originalPrice;
        _discountPrice = discountPrice;
        _totalPrice = totalPrice;
    }

    final public double getOriginalPrice() {
        return _originalPrice;
    }

    final public double getDiscountPrice() {
        return _discountPrice;
    }

    final public double getTotalPrice() {
        return _totalPrice;
    }
}
