package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private User _user = null;

    // Menu related
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private CharSequence mTitle;
    private androidx.appcompat.app.ActionBarDrawerToggle mDrawerToggle;

    public void setUser(String name, String pass) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise menu
        NavigationView mDrawerList = findViewById(R.id.navigationView);
        mTitle = getTitle();
        mDrawerLayout = findViewById(R.id.drawer);
        ImageButton menuCloseButton = mDrawerList.getHeaderView(0).findViewById(R.id.menuCloseButton);
        initMenu();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set menu listeners
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        menuCloseButton.setOnClickListener(v -> mDrawerLayout.closeDrawers());
        mDrawerList.setNavigationItemSelectedListener(this);
        initMenuToggle();

        // Load home fragment on startup
        loadFragment(new HomeFragment());
    }

    void initMenu() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    void initMenuToggle() {
        mDrawerToggle = new androidx.appcompat.app.ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
    }

    void loadFragment(Fragment fragment) {
        // TODO: handle fragment transition in background thread
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.toolbar_content_frame, fragment).commit();

            mDrawerLayout.closeDrawers();
        } else {
            Log.d("DEBUG_TAG", "Error while creating a fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        Objects.requireNonNull(getSupportActionBar()).setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.menuHome:
                fragment = new HomeFragment();
                break;
            case R.id.menuProducts:
                fragment = new ProductsFragment();
                break;
            case R.id.menuRestaurants:
                fragment = new RestaurantFragment();
                break;
            case R.id.menuCoupons:
                if (_user == null) {
                    Toast.makeText(this, "Sinun täytyy olla kirjautunut sisään", Toast.LENGTH_LONG).show();
                    fragment = new LoginFragment();
                } else {
                    fragment = new CouponFragment();
                }
                break;
            case R.id.menuHistory:
                if (_user == null) {
                    Toast.makeText(this, "Sinun täytyy olla kirjautunut sisään", Toast.LENGTH_LONG).show();
                    fragment = new LoginFragment();
                } else {
                    fragment = new HistoryFragment();
                }
                break;
            case R.id.menuAccount:
                fragment = new LoginFragment();
                break;
            default:
                break;
        }

        loadFragment(fragment);
        return false;
    }
}
