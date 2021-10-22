package com.roopeh.app.hamburger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Order implements Comparable<Order> {
    private int _id;
    final private List<ShoppingItem> _items;
    private long _orderDate;
    private long _pickupDate;
    private Restaurant _restaurant = null;
    private boolean _isPaid = false;

    private double _originalPrice = 0.0;
    private double _discountPrice = 0.0;
    private double _totalPrice = 0.0;

    public Order(int id, List<ShoppingItem> items) {
        _id = id;
        _items = new ArrayList<>();
        _items.addAll(items);
    }

    public void setId(int id) {
        _id = id;
    }

    final public int getId() {
        return _id;
    }

    final public List<ShoppingItem> getItems() {
        return _items;
    }

    public void setOrderDate(long date) {
        _orderDate = date;
    }

    final public long getOrderDate() {
        return _orderDate;
    }

    public void setPickupDate() {
        // Calculates random pickup date (10-25min)
        //final int randomMinute = new Random().nextInt(16) + 10;
        // TODO: temporary 20 sec test timer
        final double randomMinute = 20 / 60f;
        _pickupDate = (System.currentTimeMillis() / 1000) + (long)(randomMinute * 60);
    }

    // Used when loaded from db
    public void setPickupDate(long date) {
        _pickupDate = date;
    }

    final public long getPickupDate() {
        return _pickupDate;
    }

    final public boolean isOrderReady() {
        return (getPickupDate() - (System.currentTimeMillis() / 1000)) <= 0;
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

    @Override
    public int compareTo(Order o) {
        return Long.compare(o.getOrderDate(), getOrderDate());
    }
}
