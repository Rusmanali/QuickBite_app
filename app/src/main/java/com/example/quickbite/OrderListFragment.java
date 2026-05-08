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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private String status;
    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseReference mDatabase;
    private TextView tvNoOrders;

    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString("STATUS", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString("STATUS");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        
        adapter = new OrderAdapter(orderList, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                showOrderDetailsDialog(order);
            }

            @Override
            public void onOrderLongClick(Order order) {
                if ("Completed".equalsIgnoreCase(order.getStatus()) || "Rejected".equalsIgnoreCase(order.getStatus())) {
                    showDeleteConfirmation(order);
                }
            }
        });
        rvOrders.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        fetchOrders();

        return view;
    }

    private void showDeleteConfirmation(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete this order from records?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mDatabase.child(order.getOrderId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Order deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete order", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showOrderDetailsDialog(Order order) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order_details, null);
        
        TextView tvOrderId = view.findViewById(R.id.tvDetailOrderId);
        TextView tvUser = view.findViewById(R.id.tvDetailUser);
        TextView tvTotal = view.findViewById(R.id.tvDetailTotal);
        RecyclerView rvItems = view.findViewById(R.id.rvOrderDetailsItems);

        String displayId = order.getOrderId();
        if (displayId != null && displayId.length() > 5) {
            displayId = displayId.substring(displayId.length() - 5);
        }
        tvOrderId.setText("Order ID: #" + displayId);
        tvUser.setText("Customer: " + order.getUserName());
        tvTotal.setText(String.format(java.util.Locale.getDefault(), "Rs. %.2f", order.getTotalPrice()));

        rvItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (order.getItems() != null) {
            rvItems.setAdapter(new OrderDetailItemsAdapter(order.getItems()));
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setView(view);
        
        if (!"Completed".equalsIgnoreCase(order.getStatus()) && !"Rejected".equalsIgnoreCase(order.getStatus())) {
            builder.setPositiveButton("Update Status", (dialog, which) -> showStatusDialog(order));
        }
        
        builder.setNegativeButton("Close", null);
        builder.show();
    }

    private void showStatusDialog(Order order) {
        String[] options;
        String[] nextStatuses;

        if ("Pending".equalsIgnoreCase(order.getStatus())) {
            options = new String[]{"Accept Order", "Reject Order"};
            nextStatuses = new String[]{"Processing", "Rejected"};
        } else if ("Processing".equalsIgnoreCase(order.getStatus())) {
            options = new String[]{"Mark as Completed"};
            nextStatuses = new String[]{"Completed"};
        } else {
            return; // No actions for Completed or Rejected orders
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Update Order Status");
        builder.setItems(options, (dialog, which) -> {
            String newStatus = nextStatuses[which];
            mDatabase.child(order.getOrderId()).child("status").setValue(newStatus)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Order updated to " + newStatus, Toast.LENGTH_SHORT).show());
        });
        builder.show();
    }

    private void fetchOrders() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Order order = postSnapshot.getValue(Order.class);
                    if (order != null && order.getStatus().equalsIgnoreCase(status)) {
                        orderList.add(order);
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
            }
        });
    }
}