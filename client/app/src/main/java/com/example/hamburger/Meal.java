package com.example.hamburger;

public class Meal extends Product {

    public Meal(String name) {
        super(name);
    }

    @Override
    public boolean isMeal() {
        return true;
    }
}
