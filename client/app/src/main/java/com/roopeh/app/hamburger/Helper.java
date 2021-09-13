package com.roopeh.app.hamburger;

// Singleton helper class
public class Helper {
    private static Helper mInstance = null;

    private User _user = null;

    private Helper() {}

    public static Helper getInstance() {
        if (mInstance == null)
            mInstance = new Helper();

        return mInstance;
    }

    public void setUser(String name, String pass) {
        if (_user != null)
            return;

        _user = new User(name, pass);
    }

    final public User getUser() {
        return _user;
    }

    public void logoutUser() {
        if (_user == null)
            return;

        _user = null;
    }
}
