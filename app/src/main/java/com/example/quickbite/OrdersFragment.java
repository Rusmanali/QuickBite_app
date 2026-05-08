package com.example.quickbite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseReference mDatabase;
    private TextView tvNoOrders;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        mAuth = FirebaseAuth.getInstance();
        rvOrders = view.findViewById(R.id.rvUserOrders);
        tvNoOrders = view.findViewById(R.id.tvNoUserOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        
        adapter = new OrderAdapter(orderList, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                // Optional: Show order details
            }

            @Override
            public void onOrderLongClick(Order order) {
                String status = order.getStatus();
                if ("Completed".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) {
                    showDeleteConfirmation(order);
                } else {
                    Toast.makeText(getContext(), "You cannot delete an order that is " + status, Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvOrders.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        fetchUserOrders();

        return view;
    }

    private void showDeleteConfirmation(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to remove this order from your history?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mDatabase.child(order.getOrderId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Order removed", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to remove order", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchUserOrders() {
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            rvOrders.setVisibility(View.GONE);
            tvNoOrders.setVisibility(View.VISIBLE);
            tvNoOrders.setText("Please login to see your orders");
            return;
        }

        mDatabase.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Order order = postSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(0, order); // Add to beginning to show most recent first
                    }
                }
                
                if (orderList.isEmpty()) {
                    rvOrders.setVisibility(View.GONE);
                    tvNoOrders.setVisibility(View.VISIBLE);
                } else {
                    rvOrders.setVisibility(View.VISIBLE);
                    tvNoOrders.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
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