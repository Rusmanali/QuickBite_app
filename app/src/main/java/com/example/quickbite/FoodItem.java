package com.example.quickbite;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private String id;
    private String name;
    private String price;
    private String description;
    private String imageUrl;
    private float rating;
    private boolean popular;
    private String category;

    public FoodItem() {}

    public FoodItem(String id, String name, String price, String description, String imageUrl, float rating, boolean popular, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.popular = popular;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public boolean isPopular() { return popular; }
    public void setPopular(boolean popular) { this.popular = popular; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}