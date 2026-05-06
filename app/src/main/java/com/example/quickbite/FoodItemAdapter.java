package com.example.quickbite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.ViewHolder> {

    private List<FoodItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FoodItem item);
    }

    public FoodItemAdapter(List<FoodItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.rating.setText(String.valueOf(item.getRating()));
        
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.pizza_pizza_filled_with_tomatoes_salami_olives)
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnAdd.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<FoodItem> newList) {
        this.items = newList;
        notifyDataSetChanged();
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