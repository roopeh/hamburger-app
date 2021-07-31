package com.example.hamburger;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    // Menu related
    private String[] mToolbarItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mTitle;
    androidx.appcompat.app.ActionBarDrawerToggle mDrawerToggle;
    private ImageButton menuCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise menu
        mTitle = getTitle();
        mToolbarItemTitles = getResources().getStringArray(R.array.menu_items_array);
        mDrawerLayout = findViewById(R.id.drawer);
        mDrawerList = findViewById(R.id.menuContent);
        menuCloseButton = findViewById(R.id.menuCloseButton);
        initMenu();

        // Create menu content
        String[] menuItem = new String[mToolbarItemTitles.length];
        System.arraycopy(mToolbarItemTitles, 0, menuItem, 0, mToolbarItemTitles.length);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MenuAdapter adapter = new MenuAdapter(this, R.layout.menu_item_row, menuItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new MenuItemClickListener());
        mDrawerLayout = findViewById(R.id.drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        menuCloseButton.setOnClickListener(v -> mDrawerLayout.closeDrawers());
        initMenuToggle();
    }

    private class MenuItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case 0:
                // TODO
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.toolbar_content_frame, fragment).commit();

            mDrawerList.setItemChecked(pos, true);
            mDrawerList.setSelection(pos);
            setTitle(mToolbarItemTitles[pos]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.d("DEBUG_TAG", "Error while creating a fragment");
        }
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
}
