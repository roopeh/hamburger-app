package com.roopeh.app.hamburger;

import java.util.ArrayList;
import java.util.List;

public class User {
    final private String _user;

    private String _pass = "";

    final private ShoppingCart _cart;
    final private List<Coupon> _coupons;
    private Order _currentOrder = null;
    final private List<Order> _allOrders;

    public User(String user, String pass) {
        _user = user;
        _pass = pass;

        _cart = new ShoppingCart();
        _coupons = new ArrayList<>();
        _allOrders = new ArrayList<>();
    }

    final public ShoppingCart getCart() {
        return _cart;
    }

    public void addCoupon(Coupon coupon) {
        _coupons.add(coupon);
    }

    public void removeCoupon(Coupon coupon) {
        _coupons.remove(coupon);
    }

    final public List<Coupon> getCoupons() {
        return _coupons;
    }

    public void setCurrentOrder(Order order) {
        _currentOrder = order;
        if (order != null)
            _allOrders.add(order);
    }

    final public Order getCurrentOrder() {
        return _currentOrder;
    }

    final public List<Order> getAllOrders() {
        return _allOrders;
    }
}
