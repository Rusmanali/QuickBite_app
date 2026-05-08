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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories, rvPopular;
    private TextView tvNoCategories, tvNoItems, tvPopularTitle, tvWelcome;
    private View cardPromo;
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

        cardPromo = view.findViewById(R.id.cardPromo);
        rvCategories = view.findViewById(R.id.rvCategories);
        tvNoCategories = view.findViewById(R.id.tvNoCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        
        categoryAdapter = new CategoryAdapter(categoryList, (category, isSelected) -> {
            if (isSelected) {
                filterByCategory(category.getName());
                cardPromo.setVisibility(View.GONE);
            } else {
                filter(""); // Show popular items again
                cardPromo.setVisibility(View.VISIBLE);
            }
        });
        rvCategories.setAdapter(categoryAdapter);

        rvPopular = view.findViewById(R.id.rvPopular);
        tvNoItems = view.findViewById(R.id.tvNoItems);
        tvPopularTitle = view.findViewById(R.id.tvPopularTitle);
        tvWelcome = view.findViewById(R.id.tvWelcome);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            tvWelcome.setText(getString(R.string.welcome_user, user.getDisplayName()));
        } else {
            tvWelcome.setText(R.string.welcome_guest);
        }
        
        // Default to Horizontal for Popular items
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        foodItemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        foodAdapter = new FoodItemAdapter(filteredList, new FoodItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FoodItem item) {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("foodItem", item);
                startActivity(intent);
            }

            @Override
            public void onAddClick(FoodItem item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Add to Cart")
                        .setMessage("Do you want to add " + item.getName() + " in cart?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            CartManager.getInstance().addItem(item);
                            Toast.makeText(requireContext(), item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
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
        if (text.isEmpty()) {
            tvPopularTitle.setText("Popular Now");
            cardPromo.setVisibility(View.VISIBLE);
            rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            for (FoodItem item : foodItemList) {
                if (item.isPopular()) {
                    filteredList.add(item);
                }
            }
        } else {
            tvPopularTitle.setText("Search Results");
            cardPromo.setVisibility(View.GONE);
            rvPopular.setLayoutManager(new GridLayoutManager(getContext(), 2));
            for (FoodItem item : foodItemList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        foodAdapter.updateList(filteredList);
    }

    private void filterByCategory(String categoryName) {
        filteredList.clear();
        tvPopularTitle.setText(categoryName);
        cardPromo.setVisibility(View.GONE);
        rvPopular.setLayoutManager(new GridLayoutManager(getContext(), 2));
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