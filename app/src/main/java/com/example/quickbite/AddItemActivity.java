package com.example.quickbite;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    private EditText etName, etPrice, etImageUrl;
    private AutoCompleteTextView autoCompleteCategory;
    private Button btnSave;
    private DatabaseReference databaseReference, categoryDatabase;
    
    private String itemId = null;
    private List<String> categoryNames;
    private ArrayAdapter<String> categoryAdapter;
    private float existingRating = 5.0f;
    private boolean existingPopular = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Toolbar toolbar = findViewById(R.id.addItemToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etName = findViewById(R.id.etFoodName);
        etPrice = findViewById(R.id.etFoodPrice);
        etImageUrl = findViewById(R.id.etFoodImageUrl);
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory);
        btnSave = findViewById(R.id.btnSaveItem);

        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems");
        categoryDatabase = FirebaseDatabase.getInstance().getReference("categories");

        categoryNames = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryNames);
        autoCompleteCategory.setAdapter(categoryAdapter);

        fetchCategories();

        // Check if editing
        if (getIntent().hasExtra("ITEM_ID")) {
            itemId = getIntent().getStringExtra("ITEM_ID");
            etName.setText(getIntent().getStringExtra("ITEM_NAME"));
            etPrice.setText(getIntent().getStringExtra("ITEM_PRICE"));
            etImageUrl.setText(getIntent().getStringExtra("ITEM_IMAGE"));
            existingRating = getIntent().getFloatExtra("ITEM_RATING", 5.0f);
            existingPopular = getIntent().getBooleanExtra("ITEM_POPULAR", false);
            
            btnSave.setText("Update Item");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Item");
            }
        }

        btnSave.setOnClickListener(v -> saveFoodItem());
    }

    private void fetchCategories() {
        categoryDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryNames.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Category category = postSnapshot.getValue(Category.class);
                        if (category != null) {
                            categoryNames.add(category.getName());
                        }
                    }
                } else {
                    Toast.makeText(AddItemActivity.this, "No categories found. Please add them first.", Toast.LENGTH_LONG).show();
                }
                categoryAdapter.notifyDataSetChanged();
                
                if (getIntent().hasExtra("ITEM_CATEGORY")) {
                    String categoryToSelect = getIntent().getStringExtra("ITEM_CATEGORY");
                    autoCompleteCategory.setText(categoryToSelect, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddItemActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveFoodItem() {
        String name = etName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String selectedCategory = autoCompleteCategory.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(imageUrl) || TextUtils.isEmpty(selectedCategory)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = (itemId != null) ? itemId : databaseReference.push().getKey();
        FoodItem item = new FoodItem(id, name, price, imageUrl, existingRating, existingPopular, selectedCategory);

        if (id != null) {
            databaseReference.child(id).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        String msg = (itemId != null) ? "Item updated successfully!" : "Item added successfully!";
                        Toast.makeText(AddItemActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}