package com.example.quickbite;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView tvTotalOrders;
    private DatabaseReference ordersDatabase;

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

        tvTotalOrders = findViewById(R.id.tvTotalOrdersCount);
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        fetchOrderCount();

        // Add Item FAB
        FloatingActionButton fab = findViewById(R.id.fabAddItem);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddItemActivity.class));
        });

        // Quick Action Cards
        findViewById(R.id.cardManageItems).setOnClickListener(v -> startActivity(new Intent(this, ManageItemsActivity.class)));
        findViewById(R.id.cardManageUsers).setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        findViewById(R.id.cardManageOrders).setOnClickListener(v -> startActivity(new Intent(this, ManageOrdersActivity.class)));
        findViewById(R.id.cardReports).setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
        findViewById(R.id.cardManageCategories).setOnClickListener(v -> startActivity(new Intent(this, ManageCategoriesActivity.class)));
        findViewById(R.id.cardManagePopular).setOnClickListener(v -> startActivity(new Intent(this, ManagePopularActivity.class)));
        
        // Notifications action (Image in Toolbar)
        findViewById(R.id.notification).setOnClickListener(v -> 
            Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show());

        // Settings action (Image in Toolbar)
        findViewById(R.id.setting).setOnClickListener(v -> 
            startActivity(new Intent(this, SettingsActivity.class)));

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

    private void fetchOrderCount() {
        ordersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int activeOrders = 0;
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        String status = order.getStatus();
                        if ("Pending".equalsIgnoreCase(status) || "Processing".equalsIgnoreCase(status)) {
                            activeOrders++;
                        }
                    }
                }
                tvTotalOrders.setText(String.valueOf(activeOrders));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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