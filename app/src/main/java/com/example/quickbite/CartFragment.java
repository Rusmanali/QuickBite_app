package com.example.quickbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Locale;

public class CartFragment extends Fragment {
    private RecyclerView rvCartItems;
    private CartAdapter adapter;
    private TextView tvTotal;
    private DatabaseReference ordersDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        mAuth = FirebaseAuth.getInstance();
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");

        rvCartItems = view.findViewById(R.id.rvCartItems);
        tvTotal = view.findViewById(R.id.tvTotalPrice); 
        
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(CartManager.getInstance().getCartItems(), this::updateTotal);
        rvCartItems.setAdapter(adapter);

        updateTotal();

        Button btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);
        if (btnPlaceOrder != null) {
            btnPlaceOrder.setOnClickListener(v -> placeOrder());
        }
        
        return view;
    }

    private void placeOrder() {
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = (user != null) ? user.getUid() : "Guest_" + System.currentTimeMillis();
        String userName = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "Guest User";
        
        String orderId = ordersDatabase.push().getKey();
        Order order = new Order(
                orderId,
                userId,
                userName,
                CartManager.getInstance().getCartItems(),
                CartManager.getInstance().getTotalPrice(),
                "Pending",
                System.currentTimeMillis()
        );

        if (orderId != null) {
            ordersDatabase.child(orderId).setValue(order)
                    .addOnSuccessListener(aVoid -> {
                        CartManager.getInstance().clearCart();
                        startActivity(new Intent(requireContext(), OrderConfirmationActivity.class));
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void updateTotal() {
        if (tvTotal != null) {
            tvTotal.setText(String.format(Locale.getDefault(), "Rs. %.2f", CartManager.getInstance().getTotalPrice()));
        }
    }
}