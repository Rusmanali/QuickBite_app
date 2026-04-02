package com.example.quickbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Mock click on "See All" to go to DetailActivity
        // In your fragment_home.xml, the 'See All' TextView needs an ID if you want to click it.
        // Assuming it has an ID or using a generic approach:
        View seeAll = view.findViewById(R.id.seeAllTextView); 
        if (seeAll != null) {
            seeAll.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), DetailActivity.class));
            });
        }

        return view;
    }
}