package com.example.quickbite;

public class CartItem {
    private FoodItem foodItem;
    private int quantity;

    public CartItem() {}

    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        try {
            String priceStr = foodItem.getPrice().replace("$", "");
            return Double.parseDouble(priceStr) * quantity;
        } catch (Exception e) {
            return 0.0;
        }
    }
}