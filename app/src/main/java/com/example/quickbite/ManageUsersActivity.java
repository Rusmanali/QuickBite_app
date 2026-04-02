package com.example.quickbite;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        Toolbar toolbar = findViewById(R.id.manageUsersToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Spinner roleSpinner = findViewById(R.id.userRoleSpinner);
        RecyclerView recyclerView = findViewById(R.id.recyclerUsers);

        // Dummy Data for RecyclerView
        List<String> users = new ArrayList<>();
        users.add("John Doe - Customer");
        users.add("Jane Smith - Admin");
        users.add("Mike Ross - Delivery");
        users.add("Rachel Zane - Customer");
        users.add("Harvey Specter - Admin");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Using a simple built-in layout for demonstration
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users) {
            // Note: RecyclerView requires a proper Adapter, but for the sake of 
            // "Design Only" and "Dummy Data", we'd usually use a custom one.
            // I'll implement a quick inner class adapter.
        };

        // Real RecyclerView Adapter implementation
        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @androidx.annotation.NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
                android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_2, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@androidx.annotation.NonNull RecyclerView.ViewHolder holder, int position) {
                android.widget.TextView text1 = holder.itemView.findViewById(android.R.id.text1);
                android.widget.TextView text2 = holder.itemView.findViewById(android.R.id.text2);
                String[] parts = users.get(position).split(" - ");
                text1.setText(parts[0]);
                text2.setText(parts[1]);
                holder.itemView.setOnClickListener(v -> Toast.makeText(ManageUsersActivity.this, "Viewing " + parts[0], Toast.LENGTH_SHORT).show());
            }

            @Override
            public int getItemCount() {
                return users.size();
            }
        });

        roleSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position > 0) {
                    Toast.makeText(ManageUsersActivity.this, "Filtering by: " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}