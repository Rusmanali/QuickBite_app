package com.example.quickbite;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {
    private FoodItem foodItem;
    private ImageView detailImage;
    private TextView detailTitle, detailDescription, detailPrice;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        foodItem = (FoodItem) getIntent().getSerializableExtra("foodItem");

        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailDescription = findViewById(R.id.detailDescription);
        detailPrice = findViewById(R.id.detailPrice);
        ratingBar = findViewById(R.id.ratingBar);

        if (foodItem != null) {
            detailTitle.setText(foodItem.getName());
            
            // Format price to show Rs. correctly
            String price = foodItem.getPrice();
            if (price != null) {
                price = price.replace("Rs.", "").replace("Rs", "").trim();
                detailPrice.setText("Rs. " + price);
            }

            ratingBar.setRating(foodItem.getRating());
            // Using name as description for now as FoodItem doesn't have description field
            detailDescription.setText("Enjoy our delicious " + foodItem.getName() + ". Prepared with high-quality ingredients and served fresh just for you.");
            
            Glide.with(this)
                    .load(foodItem.getImageUrl())
                    .placeholder(R.drawable.pizza_pizza_filled_with_tomatoes_salami_olives)
                    .into(detailImage);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(v -> {
            if (foodItem != null) {
                CartManager.getInstance().addItem(foodItem);
                Toast.makeText(DetailActivity.this, foodItem.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
