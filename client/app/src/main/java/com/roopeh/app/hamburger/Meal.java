package com.roopeh.app.hamburger;

public class Meal extends Product {

    public Meal(int id, String name) {
        super(id, name);
    }

    @Override
    public boolean isMeal() {
        return true;
    }
}
