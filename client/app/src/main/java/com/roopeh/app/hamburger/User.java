package com.roopeh.app.hamburger;

public class User {
    final private String _user;

    private String _pass = "";

    final private ShoppingEvent _cart;

    public User(String user, String pass) {
        _user = user;
        _pass = pass;

        _cart = new ShoppingEvent();
    }

    final public ShoppingEvent getCart() {
        return _cart;
    }
}
