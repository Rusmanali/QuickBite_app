package com.example.quickbite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = items.get(position);
        holder.name.setText(item.getName());
        
        // Format price to show Rs. correctly
        String price = item.getPrice();
        if (price != null) {
            price = price.replace("Rs.", "").replace("Rs", "").trim();
            holder.price.setText("Rs. " + price);
        }
        
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.pizza_pizza_filled_with_tomatoes_salami_olives)
                .into(holder.image);

        if (holder.tvPopularBadge != null) {
            holder.tvPopularBadge.setVisibility(item.isPopular() ? View.VISIBLE : View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, tvPopularBadge;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.adminItemImage);
            name = itemView.findViewById(R.id.adminItemName);
            price = itemView.findViewById(R.id.adminItemPrice);
            tvPopularBadge = itemView.findViewById(R.id.tvPopularBadge);
            btnEdit = itemView.findViewById(R.id.btnEditItem);
            btnDelete = itemView.findViewById(R.id.btnDeleteItem);
        }
    }
}