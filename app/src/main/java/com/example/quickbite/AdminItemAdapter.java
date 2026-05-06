package com.example.quickbite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class AdminItemAdapter extends RecyclerView.Adapter<AdminItemAdapter.ViewHolder> {

    private List<FoodItem> items;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(FoodItem item);
        void onDelete(FoodItem item);
    }

    public AdminItemAdapter(List<FoodItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using the new food card layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.rating.setText(String.valueOf(item.getRating()));
        
        // Load image from URL using Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.pizza_pizza_filled_with_tomatoes_salami_olives)
                .into(holder.image);

        // Show popular indicator if needed (repurposing the btnAdd or adding a tint)
        if (item.isPopular()) {
            holder.btnAdd.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.btnAdd.setImageResource(android.R.drawable.ic_input_add);
        }

        // For admin dashboard, maybe we want to edit/delete on click
        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        
        // The add button in the card could be repurposed for delete or edit in admin view
        // or just kept as is.
        holder.btnAdd.setOnClickListener(v -> listener.onEdit(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btnAdd;
        TextView name, price, rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.foodImage);
            name = itemView.findViewById(R.id.tvFoodName);
            price = itemView.findViewById(R.id.tvFoodPrice);
            rating = itemView.findViewById(R.id.tvRating);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}