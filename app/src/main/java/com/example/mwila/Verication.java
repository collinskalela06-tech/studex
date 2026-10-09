package com.example.mwila;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Verication extends AppCompatActivity {

    private EditText[] boxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verication);

        boxes = new EditText[]{
                findViewById(R.id.d1), findViewById(R.id.d2), findViewById(R.id.d3),
                findViewById(R.id.d4), findViewById(R.id.d5), findViewById(R.id.d6)};
        Button btnContinue = findViewById(R.id.btnContinue);

        // Show where the code was sent (sent from the Forgot Password screen)
        String contact = getIntent().getStringExtra("contact");
        if (contact != null && !contact.isEmpty()) {
            ((TextView) findViewById(R.id.tvSentTo)).setText(
                    "We have sent a 6-digit code to " + contact + ". Please enter it below.");
        }

        btnContinue.setBackground(box(Color.WHITE, Color.WHITE, 14));

        for (int i = 0; i < boxes.length; i++) {
            final int index = i;
            boxes[i].setBackground(box(Color.WHITE, Color.WHITE, 12));

            // Move to the next box after typing a digit
            boxes[i].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
                public void onTextChanged(CharSequence s, int a, int b, int c) {
                    if (s.length() == 1 && index < boxes.length - 1) {
                        boxes[index + 1].requestFocus();
                    }
                }
                public void afterTextChanged(Editable s) { }
            });

            // Backspace on an empty box goes back to the previous one
            boxes[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && boxes[index].getText().length() == 0 && index > 0) {
                    boxes[index - 1].setText("");
                    boxes[index - 1].requestFocus();
                    return true;
                }
                return false;
            });
        }
        boxes[0].requestFocus();

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvResend).setOnClickListener(v ->
                toast("A new code has been sent"));
        btnContinue.setOnClickListener(v -> verify());
    }

    private void verify() {
        StringBuilder code = new StringBuilder();
        for (EditText b : boxes) code.append(b.getText().toString());

        if (code.length() < 6) {
            toast("Enter the 6-digit code");
            return;
        }

        // TODO: check the code with the server, then open the Reset Password screen
        Intent intent = new Intent(this, Ressetpassword.class);
        startActivity(intent);
    }

    private GradientDrawable box(int fill, int stroke, int radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setStroke(Math.round(getResources().getDisplayMetrics().density), stroke);
        g.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        return g;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}