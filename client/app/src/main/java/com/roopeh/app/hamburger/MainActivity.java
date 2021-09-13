package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
    // Menu related
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private CharSequence mTitle;
    private androidx.appcompat.app.ActionBarDrawerToggle mDrawerToggle;

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
        loadFragment(new HomeFragment(), true);
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

    void loadFragment(Fragment fragment, boolean onStartUp) {
        // TODO: handle fragment transition in background thread
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (!onStartUp)
                fragmentManager.beginTransaction().replace(R.id.toolbar_content_frame, fragment).addToBackStack(null).commit();
            else
                fragmentManager.beginTransaction().replace(R.id.toolbar_content_frame, fragment).commit();

            mDrawerLayout.closeDrawers();
        } else {
            Log.d("DEBUG_TAG", "Error while creating a fragment");
        }
    }

    final public boolean returnToPreviousFragment(boolean usingDefaultButton) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0 ) {
            fragmentManager.popBackStackImmediate();
            return true;
        }

        // First fragment, return to home by default
        // but if built-in back button is pressed, do nothing
        if (!usingDefaultButton) {
            loadFragment(new HomeFragment(), false);
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (!returnToPreviousFragment(true)) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.shopping_cart_button) {
            if (Helper.getInstance().getUser() == null) {
                Toast.makeText(this, "Sinun täytyy olla kirjautunut sisään", Toast.LENGTH_LONG).show();
                loadFragment(new LoginFragment(), false);
            } else {
                loadFragment(new CartFragment(), false);
            }
        }

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
                if (Helper.getInstance().getUser() == null) {
                    Toast.makeText(this, "Sinun täytyy olla kirjautunut sisään", Toast.LENGTH_LONG).show();
                    fragment = new LoginFragment();
                } else {
                    fragment = new CouponFragment();
                }
                break;
            case R.id.menuHistory:
                if (Helper.getInstance().getUser() == null) {
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

        loadFragment(fragment, false);
        return false;
    }
}
