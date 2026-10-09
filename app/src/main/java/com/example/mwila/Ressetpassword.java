package com.example.mwila;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Ressetpassword extends AppCompatActivity {

    private EditText etNewPass, etConfirmPass;
    private Button btnReset;
    private View bannerSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ressetpassword);

        etNewPass = findViewById(R.id.etNewPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnReset = findViewById(R.id.btnReset);
        bannerSuccess = findViewById(R.id.bannerSuccess);

        // Rounded boxes
        etNewPass.setBackground(box(Color.WHITE, Color.WHITE, 14));
        etConfirmPass.setBackground(box(Color.WHITE, Color.WHITE, 14));
        btnReset.setBackground(box(Color.WHITE, Color.WHITE, 14));
        bannerSuccess.setBackground(box(0xFFE8F5E9, 0xFF2E9E6B, 12));

        setupShow(etNewPass, (TextView) findViewById(R.id.tvShowNew));
        setupShow(etConfirmPass, (TextView) findViewById(R.id.tvShowConfirm));

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        btnReset.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String pass = etNewPass.getText().toString();
        String confirm = etConfirmPass.getText().toString();

        if (pass.length() < 8 || !pass.matches(".*[A-Za-z].*") || !pass.matches(".*[0-9].*")) {
            etNewPass.setError("At least 8 characters, with a letter and a number");
            etNewPass.requestFocus();
            return;
        }
        if (!pass.equals(confirm)) {
            etConfirmPass.setError("Passwords do not match");
            etConfirmPass.requestFocus();
            return;
        }

        // TODO: send the new password to the server here
        btnReset.setEnabled(false);
        bannerSuccess.setVisibility(View.VISIBLE);

        // After 2 seconds go back to Sign In and close the reset screens
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(this, SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 2000);
    }

    private void setupShow(EditText field, TextView toggle) {
        final boolean[] visible = {false};
        toggle.setOnClickListener(v -> {
            visible[0] = !visible[0];
            field.setTransformationMethod(visible[0]
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            field.setSelection(field.getText().length());
            toggle.setText(visible[0] ? "HIDE" : "SHOW");
        });
    }

    private GradientDrawable box(int fill, int stroke, int radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setStroke(Math.round(getResources().getDisplayMetrics().density), stroke);
        g.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        return g;
    }
}