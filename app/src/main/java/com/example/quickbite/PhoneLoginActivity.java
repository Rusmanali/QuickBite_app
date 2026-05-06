package com.example.quickbite;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PhoneLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        EditText phoneEditText = findViewById(R.id.phoneEditText);
        Button sendOtpButton = findViewById(R.id.sendOtpButton);

        sendOtpButton.setOnClickListener(v -> {
            String phoneNumber = phoneEditText.getText().toString();
            if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "OTP sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                // To be implemented: actual phone authentication
            }
        });

        findViewById(R.id.backToLoginButton).setOnClickListener(v -> finish());
    }
}
