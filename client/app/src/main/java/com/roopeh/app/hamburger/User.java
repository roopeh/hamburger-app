package com.roopeh.app.hamburger;

import java.util.ArrayList;
import java.util.List;

public class User {
    final private String _userName;

    private String _pass = "";
    private String _firstName = "";
    private String _lastName = "";
    private String _email = "";
    private String _phone = "";

    final private ShoppingCart _cart;
    final private List<Coupon> _coupons;
    private Order _currentOrder = null;
    final private List<Order> _allOrders;

    public User(String user, String pass) {
        _userName = user;
        _pass = pass;

        _cart = new ShoppingCart();
        _coupons = new ArrayList<>();
        _allOrders = new ArrayList<>();
    }

    final public String getUserName() {
        return _userName;
    }

    public void setFirstName(String firstName) {
        _firstName = firstName;
    }

    final public String getFirstName() {
        return _firstName;
    }

    public void setLastName(String lastName) {
        _lastName = lastName;
    }

    final public String getLastName() {
        return _lastName;
    }

    public void setEmail(String email) {
        _email = email;
    }

    final public String getEmail() {
        return _email;
    }

    public void setPhoneNumber(String number) {
        _phone = number;
    }

    final public String getPhoneNumber() {
        return _phone;
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
            _allOrders.add(0, order);
    }

    final public Order getCurrentOrder() {
        return _currentOrder;
    }

    final public List<Order> getAllOrders() {
        return _allOrders;
    }
}
