package com.example.quickbite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onOrderLongClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvUserName.setText(order.getUserName());
        holder.tvOrderId.setText("ID: " + order.getOrderId());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Rs. %.2f", order.getTotalPrice()));
        holder.tvStatus.setText(order.getStatus());
        
        int statusColor;
        int statusBackground;
        
        String status = order.getStatus() != null ? order.getStatus() : "Pending";
        switch (status.toLowerCase()) {
            case "completed":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_700);
                statusBackground = ContextCompat.getColor(holder.itemView.getContext(), R.color.tint_green);
                break;
            case "processing":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.button_gold);
                statusBackground = ContextCompat.getColor(holder.itemView.getContext(), R.color.tint_yellow);
                break;
            case "rejected":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark);
                statusBackground = ContextCompat.getColor(holder.itemView.getContext(), R.color.tint_red);
                break;
            default: // Pending
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.text_brown);
                statusBackground = ContextCompat.getColor(holder.itemView.getContext(), R.color.tint_yellow);
                break;
        }
        
        holder.tvStatus.setTextColor(statusColor);
        holder.tvStatus.getBackground().setTint(statusBackground);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(order.getTimestamp())));

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onOrderLongClick(order);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvOrderId, tvPrice, tvStatus, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvOrderUserName);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
        }
    }
}