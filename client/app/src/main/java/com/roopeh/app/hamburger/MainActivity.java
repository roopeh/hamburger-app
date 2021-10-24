package com.roopeh.app.hamburger;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private CountDownTimer _orderTimer;

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
        final NavigationView mDrawerList = findViewById(R.id.navigationView);
        mTitle = getTitle();
        mDrawerLayout = findViewById(R.id.drawer);
        final ImageButton menuCloseButton = mDrawerList.getHeaderView(0).findViewById(R.id.menuCloseButton);

        initMenu();
        initMenuToggle();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set menu listeners
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        menuCloseButton.setOnClickListener(v -> mDrawerLayout.closeDrawers());
        mDrawerList.setNavigationItemSelectedListener(this);

        // Load home fragment on startup
        loadFragment(new HomeFragment(), true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null || !intent.hasExtra("order_id"))
            return;

        final int orderId = intent.getIntExtra("order_id", -1);
        if (orderId == -1)
            return;

        if (Helper.getInstance().getUser() != null) {
            final Order order = Helper.getInstance().getUser().getOrderById(orderId);
            if (order != null)
                loadFragment(new HistoryInfoFragment(order), false);
        }
    }

    private void initMenu() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void initMenuToggle() {
        mDrawerToggle = new androidx.appcompat.app.ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
    }

    public void loadFragment(Fragment fragment, boolean onStartUp) {
        if (fragment == null) {
            Log.d("DEBUG_TAG", "Error while creating a fragment");
            return;
        }

        if (onStartUp) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.toolbar_content_frame, fragment).commit();
        } else {
            // Send fragment in 200ms to allow the menu to close
            // Cannot be done in onDrawerClosed method because i.e. shopping cart is opened from outside the menu
            // and that method would never trigger then
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                final FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.toolbar_content_frame, fragment).addToBackStack(null).commit();
            }, 200);
        }

        mDrawerLayout.closeDrawers();
    }

    final public boolean returnToPreviousFragment(boolean usingDefaultButton) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0 ) {
            final Handler handler = new Handler();
            handler.post(fragmentManager::popBackStackImmediate);
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
                if (Helper.getInstance().getUser() == null)
                    fragment = new LoginFragment();
                else
                    fragment = new UserFragment();
                break;
            default:
                break;
        }

        loadFragment(fragment, false);
        return false;
    }

    public void createOrderTimer() {
        final User user = Helper.getInstance().getUser();
        if (user == null || user.getCurrentOrder() == null)
            return;

        if (_orderTimer != null) {
            _orderTimer.cancel();
            _orderTimer = null;
        }

        final long timeDiff = user.getCurrentOrder().getPickupDate() - (System.currentTimeMillis() / 1000);
        _orderTimer = new CountDownTimer(timeDiff * 1000, 10 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
                // local timer updates remaining time in CurrentOrderFragment
            }

            @Override
            public void onFinish() {
                createNotification();
                user.setCurrentOrder(null);
            }
        }.start();
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void createNotification() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.putExtra("order_id", Helper.getInstance().getUser().getCurrentOrder().getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        final PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            pendingIntent = PendingIntent.getActivity(this, -1, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        else
            pendingIntent = PendingIntent.getActivity(this, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final String _channelId = "123456";
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, _channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                // TODO: strings resource
                .setContentTitle("Tilauksesi on noudettavissa!")
                .setContentText("Tarkastale tilaustasi...")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create notification channel for SDK 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int importance = NotificationManager.IMPORTANCE_DEFAULT;
            final NotificationChannel channel = new NotificationChannel(_channelId, "Notification_channel_name", importance);
            channel.enableLights(true);
            builder.setChannelId(_channelId);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, builder.build());
    }
}
