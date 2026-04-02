package com.example.quickbite;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Toolbar toolbar = findViewById(R.id.adminToolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Chronometer chronometer = findViewById(R.id.dashboardChronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        // Quick Action Cards
        findViewById(R.id.cardManageItems).setOnClickListener(v -> startActivity(new Intent(this, ManageItemsActivity.class)));
        findViewById(R.id.cardManageUsers).setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        findViewById(R.id.cardManageOrders).setOnClickListener(v -> startActivity(new Intent(this, ManageOrdersActivity.class)));
        findViewById(R.id.cardReports).setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
        
        // Notifications action (Card and Image)
        View.OnClickListener notificationListener = v -> Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show();
        findViewById(R.id.cardNotifications).setOnClickListener(notificationListener);
        findViewById(R.id.notification).setOnClickListener(notificationListener);

        // Settings action (Card and Image)
        View.OnClickListener settingsListener = v -> startActivity(new Intent(this, SettingsActivity.class));
        findViewById(R.id.cardSettings).setOnClickListener(settingsListener);
        findViewById(R.id.setting).setOnClickListener(settingsListener);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_orders) {
                startActivity(new Intent(this, ManageOrdersActivity.class));
            } else if (id == R.id.nav_reports) {
                startActivity(new Intent(this, ReportsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            return true;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_items) {
            startActivity(new Intent(this, ManageItemsActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(this, ManageUsersActivity.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(this, ManageOrdersActivity.class));
        } else if (id == R.id.nav_reports) {
            startActivity(new Intent(this, ReportsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}