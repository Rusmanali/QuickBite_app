package com.example.quickbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Locale;

public class CartFragment extends Fragment {
    private RecyclerView rvCartItems;
    private CartAdapter adapter;
    private TextView tvTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        rvCartItems = view.findViewById(R.id.rvCartItems);
        tvTotal = view.findViewById(R.id.tvTotalPrice); // Wait, need to check ID in xml
        
        if (tvTotal == null) {
            // I might have forgotten to add an ID to the total TextView in XML
        }

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(CartManager.getInstance().getCartItems(), this::updateTotal);
        rvCartItems.setAdapter(adapter);

        updateTotal();

        Button btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);
        if (btnPlaceOrder != null) {
            btnPlaceOrder.setOnClickListener(v -> {
                if (CartManager.getInstance().getCartItems().isEmpty()) {
                    return;
                }
                startActivity(new Intent(requireContext(), OrderConfirmationActivity.class));
            });
        }
        
        return view;
    }

    private void updateTotal() {
        if (tvTotal != null) {
            tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", CartManager.getInstance().getTotalPrice()));
        }
    }
}