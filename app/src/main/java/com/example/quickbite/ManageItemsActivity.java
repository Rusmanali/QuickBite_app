package com.example.quickbite;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class ManageItemsActivity extends AppCompatActivity {

    private ArrayList<String> foodItems;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);

        Toolbar toolbar = findViewById(R.id.manageItemsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = findViewById(R.id.listItems);
        SearchView searchView = findViewById(R.id.searchItems);
        FloatingActionButton fab = findViewById(R.id.fabAddItem);

        foodItems = new ArrayList<>(Arrays.asList("Burger", "Pizza", "Pasta", "Sushi", "Salad", "Taco", "Steak"));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodItems);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            showEditDeleteDialog(position);
        });

        fab.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");
        builder.setMessage("This would open a form to add a new food item.");
        builder.setPositiveButton("Simulate Add", (dialog, which) -> {
            foodItems.add("New Food Item");
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Item added (Simulated)", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditDeleteDialog(int position) {
        String item = foodItems.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item);
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
            if (which == 0) {
                Toast.makeText(this, "Edit " + item + " (Simulated)", Toast.LENGTH_SHORT).show();
            } else {
                foodItems.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, item + " deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}