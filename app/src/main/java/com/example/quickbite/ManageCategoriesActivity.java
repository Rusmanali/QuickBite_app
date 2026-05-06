package com.example.quickbite;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ManageCategoriesActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private AdminCategoryAdapter adapter;
    private List<Category> categoryList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        mDatabase = FirebaseDatabase.getInstance().getReference("categories");

        rvCategories = findViewById(R.id.rvManageCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();

        adapter = new AdminCategoryAdapter(categoryList, new AdminCategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showAddEditDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                mDatabase.child(category.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(ManageCategoriesActivity.this, "Category deleted", Toast.LENGTH_SHORT).show());
            }
        });
        rvCategories.setAdapter(adapter);

        findViewById(R.id.fabAddCategory).setOnClickListener(v -> showAddEditDialog(null));

        fetchCategories();
    }

    private void fetchCategories() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageCategoriesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        EditText etName = view.findViewById(R.id.etCategoryName);
        EditText etImageUrl = view.findViewById(R.id.etCategoryImageUrl);

        if (category != null) {
            builder.setTitle("Edit Category");
            etName.setText(category.getName());
            etImageUrl.setText(category.getImageUrl());
        } else {
            builder.setTitle("Add Category");
        }

        builder.setView(view);
        builder.setPositiveButton(category != null ? "Update" : "Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (!TextUtils.isEmpty(name)) {
                String id = category != null ? category.getId() : mDatabase.push().getKey();
                Category newCategory = new Category(id, name, imageUrl);
                if (id != null) {
                    mDatabase.child(id).setValue(newCategory)
                            .addOnSuccessListener(aVoid -> Toast.makeText(ManageCategoriesActivity.this, "Success", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(ManageCategoriesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}