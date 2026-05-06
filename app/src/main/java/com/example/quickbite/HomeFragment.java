package com.example.quickbite;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories, rvPopular;
    private TextView tvNoCategories, tvNoItems;
    private CategoryAdapter categoryAdapter;
    private FoodItemAdapter foodAdapter;
    private List<Category> categoryList;
    private List<FoodItem> foodItemList;
    private List<FoodItem> filteredList;
    private DatabaseReference mDatabase, foodDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("categories");
        foodDatabase = FirebaseDatabase.getInstance().getReference("FoodItems");

        rvCategories = view.findViewById(R.id.rvCategories);
        tvNoCategories = view.findViewById(R.id.tvNoCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            Toast.makeText(getContext(), "Clicked: " + category.getName(), Toast.LENGTH_SHORT).show();
            filterByCategory(category.getName());
        });
        rvCategories.setAdapter(categoryAdapter);

        rvPopular = view.findViewById(R.id.rvPopular);
        tvNoItems = view.findViewById(R.id.tvNoItems);
        rvPopular.setLayoutManager(new GridLayoutManager(getContext(), 2));
        foodItemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        foodAdapter = new FoodItemAdapter(filteredList, item -> {
            CartManager.getInstance().addItem(item);
            Toast.makeText(getContext(), item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        });
        rvPopular.setAdapter(foodAdapter);

        fetchCategories();
        fetchFoodItems();

        EditText searchBar = view.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        View seeAll = view.findViewById(R.id.seeAllTextView); 
        if (seeAll != null) {
            seeAll.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), DetailActivity.class));
            });
        }

        return view;
    }

    private void fetchFoodItems() {
        foodDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItemList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FoodItem item = postSnapshot.getValue(FoodItem.class);
                    if (item != null) {
                        foodItemList.add(item);
                    }
                }
                
                if (foodItemList.isEmpty()) {
                    rvPopular.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                } else {
                    rvPopular.setVisibility(View.VISIBLE);
                    tvNoItems.setVisibility(View.GONE);
                }
                
                filter(""); // Initial display all
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        for (FoodItem item : foodItemList) {
            // If text is empty, show only popular items by default
            // If text is present, search through all items
            if (text.isEmpty()) {
                if (item.isPopular()) {
                    filteredList.add(item);
                }
            } else {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        foodAdapter.updateList(filteredList);
    }

    private void filterByCategory(String categoryName) {
        filteredList.clear();
        for (FoodItem item : foodItemList) {
            if (item.getCategory() != null && item.getCategory().equalsIgnoreCase(categoryName)) {
                filteredList.add(item);
            }
        }
        
        if (filteredList.isEmpty()) {
            rvPopular.setVisibility(View.GONE);
            tvNoItems.setVisibility(View.VISIBLE);
            tvNoItems.setText("No items found in " + categoryName);
        } else {
            rvPopular.setVisibility(View.VISIBLE);
            tvNoItems.setVisibility(View.GONE);
        }

        foodAdapter.updateList(filteredList);
    }
    private void fetchCategories() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Category category = postSnapshot.getValue(Category.class);
                        if (category != null) {
                            categoryList.add(category);
                        }
                    }
                }
                
                if (categoryList.isEmpty()) {
                    rvCategories.setVisibility(View.GONE);
                    tvNoCategories.setVisibility(View.VISIBLE);
                } else {
                    rvCategories.setVisibility(View.VISIBLE);
                    tvNoCategories.setVisibility(View.GONE);
                }

                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}