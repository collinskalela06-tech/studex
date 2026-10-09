package com.example.mwila;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SigninActivity extends AppCompatActivity {

    private EditText etUser, etPassword;
    private TextView tvShow;
    private CheckBox cbRemember;
    private SharedPreferences prefs;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        tvShow = findViewById(R.id.tvShow);
        cbRemember = findViewById(R.id.cbRemember);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnCreate = findViewById(R.id.btnCreate);

        prefs = getSharedPreferences("studex", MODE_PRIVATE);
        etUser.setText(prefs.getString("user", ""));

        // Rounded boxes: white fields, white button, outlined white button
        etUser.setBackground(box(Color.WHITE, Color.WHITE, 14));
        etPassword.setBackground(box(Color.WHITE, Color.WHITE, 14));
        btnSignIn.setBackground(box(Color.WHITE, Color.WHITE, 14));
        btnCreate.setBackground(box(Color.TRANSPARENT, Color.WHITE, 14));

        // Show / hide password
        tvShow.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            etPassword.setTransformationMethod(passwordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            etPassword.setSelection(etPassword.getText().length());
            tvShow.setText(passwordVisible ? "HIDE" : "SHOW");
        });

        btnSignIn.setOnClickListener(v -> signIn());

        // Create New Account -> Create Account screen
        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, CreateAccount.class)));

        // Forgot password? -> Forgot Password screen
        findViewById(R.id.tvForgot).setOnClickListener(v ->
                startActivity(new Intent(this, Forgotpassword.class)));
    }

    private void signIn() {
        String user = etUser.getText().toString().trim();
        String pass = etPassword.getText().toString();

        if (user.isEmpty()) {
            etUser.setError("Enter your phone number or email");
            etUser.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (cbRemember.isChecked()) prefs.edit().putString("user", user).apply();
        else prefs.edit().remove("user").apply();

        // TODO: check the details against the database, then open the dashboard
        Intent intent = new Intent(this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private GradientDrawable box(int fill, int stroke, int radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setStroke(dp(1), stroke);
        g.setCornerRadius(dp(radiusDp));
        return g;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}