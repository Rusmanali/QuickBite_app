package com.example.quickbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ManageItemsActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private AdminItemAdapter adapter;
    private List<FoodItem> foodItemList;
    private List<FoodItem> filteredList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);

        Toolbar toolbar = findViewById(R.id.manageItemsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("FoodItems");

        rvItems = findViewById(R.id.rvManageItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        
        foodItemList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new AdminItemAdapter(filteredList, new AdminItemAdapter.OnItemActionListener() {
            @Override
            public void onEdit(FoodItem item) {
                // Open AddItemActivity for editing
                Intent intent = new Intent(ManageItemsActivity.this, AddItemActivity.class);
                intent.putExtra("ITEM_ID", item.getId());
                intent.putExtra("ITEM_NAME", item.getName());
                intent.putExtra("ITEM_PRICE", item.getPrice());
                intent.putExtra("ITEM_DESCRIPTION", item.getDescription());
                intent.putExtra("ITEM_IMAGE", item.getImageUrl());
                intent.putExtra("ITEM_RATING", item.getRating());
                intent.putExtra("ITEM_POPULAR", item.isPopular());
                intent.putExtra("ITEM_CATEGORY", item.getCategory());
                startActivity(intent);
            }

            @Override
            public void onDelete(FoodItem item) {
                mDatabase.child(item.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(ManageItemsActivity.this, "Item deleted", Toast.LENGTH_SHORT).show());
            }
        });
        rvItems.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchItems);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabAddItem);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddItemActivity.class)));

        fetchFoodItems();
    }

    private void fetchFoodItems() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItemList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FoodItem item = postSnapshot.getValue(FoodItem.class);
                    if (item != null) {
                        foodItemList.add(item);
                    }
                }
                filter(""); // Initial display
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageItemsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(foodItemList);
        } else {
            for (FoodItem item : foodItemList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}