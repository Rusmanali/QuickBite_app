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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChanged();
    }

    public CartAdapter(List<CartItem> cartItems, OnCartChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        FoodItem food = item.getFoodItem();
        
        holder.name.setText(food.getName());
        holder.price.setText(food.getPrice());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        
        Glide.with(holder.itemView.getContext())
                .load(food.getImageUrl())
                .placeholder(R.drawable.pizza_pizza_filled_with_tomatoes_salami_olives)
                .into(holder.image);

        holder.btnPlus.setOnClickListener(v -> {
            CartManager.getInstance().addItem(food);
            notifyItemChanged(position);
            listener.onQuantityChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            CartManager.getInstance().removeItem(food);
            if (item.getQuantity() > 0) {
                notifyItemChanged(position);
            } else {
                notifyDataSetChanged();
            }
            listener.onQuantityChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btnPlus, btnMinus;
        TextView name, price, quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivCartItem);
            name = itemView.findViewById(R.id.tvCartItemName);
            price = itemView.findViewById(R.id.tvCartItemPrice);
            quantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}