package com.example.mwila;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Forgotpassword extends AppCompatActivity {

    private EditText etContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        etContact = findViewById(R.id.etContact);
        Button btnSendCode = findViewById(R.id.btnSendCode);

        // Rounded white boxes
        etContact.setBackground(box(Color.WHITE, Color.WHITE, 14));
        btnSendCode.setBackground(box(Color.WHITE, Color.WHITE, 14));

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvBackToSignIn).setOnClickListener(v -> finish());
        btnSendCode.setOnClickListener(v -> sendCode());
    }

    private void sendCode() {
        String contact = etContact.getText().toString().trim();

        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(contact).matches();
        boolean isPhone = contact.replace("+", "").replace(" ", "").matches("\\d{9,13}");

        if (contact.isEmpty()) {
            etContact.setError("Enter your email or phone number");
            etContact.requestFocus();
            return;
        }
        if (!isEmail && !isPhone) {
            etContact.setError("Enter a valid email address or phone number");
            etContact.requestFocus();
            return;
        }

        // TODO: ask the server to send a 6-digit code, then open the Verify Code screen
        Intent intent = new Intent(this, Verication.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }

    private GradientDrawable box(int fill, int stroke, int radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setStroke(Math.round(getResources().getDisplayMetrics().density), stroke);
        g.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        return g;
    }
}