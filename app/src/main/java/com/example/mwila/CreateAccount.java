package com.example.mwila;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {

    private TextView tvStudent, tvLecturer;
    private EditText etName, etNumber, etEmail, etPassword, etConfirm;
    private Spinner spProgramme;
    private boolean isStudent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        tvStudent = findViewById(R.id.tvStudent);
        tvLecturer = findViewById(R.id.tvLecturer);
        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etNumber);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        spProgramme = findViewById(R.id.spProgramme);
        Button btnCreate = findViewById(R.id.btnCreateAccount);

        // Rounded white boxes
        EditText[] fields = {etName, etNumber, etEmail, etPassword, etConfirm};
        for (EditText f : fields) f.setBackground(box(Color.WHITE, Color.WHITE, 14));
        spProgramme.setBackground(box(Color.WHITE, Color.WHITE, 14));
        btnCreate.setBackground(box(Color.WHITE, Color.WHITE, 14));

        // Edit this list to match your university's programmes
        String[] programmes = {"Select programme", "Computer Science",
                "Information Technology", "Information Systems", "Software Engineering"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, programmes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProgramme.setAdapter(adapter);

        setRole(true);
        tvStudent.setOnClickListener(v -> setRole(true));
        tvLecturer.setOnClickListener(v -> setRole(false));

        setupShow(etPassword, (TextView) findViewById(R.id.tvShowPass));
        setupShow(etConfirm, (TextView) findViewById(R.id.tvShowConfirm));

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvHaveAccount).setOnClickListener(v -> finish());
        btnCreate.setOnClickListener(v -> register());
    }

    private void setRole(boolean student) {
        isStudent = student;
        tvStudent.setBackground(student ? box(Color.WHITE, Color.WHITE, 24)
                : box(0xFF1E4FD8, Color.WHITE, 24));
        tvLecturer.setBackground(student ? box(0xFF1E4FD8, Color.WHITE, 24)
                : box(Color.WHITE, Color.WHITE, 24));
        tvStudent.setTextColor(student ? 0xFF0B3A8F : Color.WHITE);
        tvLecturer.setTextColor(student ? Color.WHITE : 0xFF0B3A8F);
        etNumber.setHint(student ? "Student Number (e.g. 20261234)" : "Staff Number");
        spProgramme.setVisibility(student ? View.VISIBLE : View.GONE);
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

    private void register() {
        String name = etName.getText().toString().trim();
        String number = etNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Enter your full name");
            etName.requestFocus();
            return;
        }
        if (number.isEmpty()) {
            etNumber.setError("Required");
            etNumber.requestFocus();
            return;
        }
        if (isStudent && spProgramme.getSelectedItemPosition() == 0) {
            toast("Please select your programme");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }
        if (pass.length() < 8 || !pass.matches(".*[A-Za-z].*") || !pass.matches(".*[0-9].*")) {
            etPassword.setError("At least 8 characters, with a letter and a number");
            etPassword.requestFocus();
            return;
        }
        if (!pass.equals(confirm)) {
            etConfirm.setError("Passwords do not match");
            etConfirm.requestFocus();
            return;
        }

        // Save the name and programme so the dashboard can greet the student
        SharedPreferences sp = getSharedPreferences("studex", MODE_PRIVATE);
        sp.edit()
                .putString("name", name)
                .putString("number", number)
                .putString("programme", isStudent
                        ? (String) spProgramme.getSelectedItem() : "Lecturer")
                .apply();

        // TODO: save the user to your server/database here
        toast("Account created! Please sign in.");
        finish();   // back to Sign In
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