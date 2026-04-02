package com.example.quickbite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText emailEditText = findViewById(R.id.editTextemail);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.loginButton);
        Button adminLoginButton = findViewById(R.id.adminLoginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (validateLogin(email, password)) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        adminLoginButton.setOnClickListener(v -> {
            // In a real app, you might want to redirect to a specific Admin Login 
            // or verify admin credentials here. For this demo, we'll go to the Admin Dashboard.
            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
        });
    }

    private boolean validateLogin(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            new AlertDialog.Builder(this)
                .setTitle("Invalid Password")
                .setMessage("Password must be at least 6 characters long.")
                .setPositiveButton("OK", null)
                .show();
            return false;
        }
        return true;
    }
}