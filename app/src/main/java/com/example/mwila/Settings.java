package com.example.mwila;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    private static final String PREFS = "studex";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Keep the header below the status bar and the bottom bar above the gesture bar
        View root = findViewById(R.id.root);
        View header = findViewById(R.id.headerBox);
        View bottomBar = findViewById(R.id.bottomBar);
        final int headerTop = header.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            header.setPadding(header.getPaddingLeft(), headerTop + bars.top,
                    header.getPaddingRight(), header.getPaddingBottom());
            bottomBar.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });

        // Who is signed in
        String name = prefs.getString("name", "Student").trim();
        if (name.isEmpty()) name = "Student";
        String programme = prefs.getString("programme", "");
        String role = programme.equals("Lecturer") ? "Lecturer" : "Student";
        ((TextView) findViewById(R.id.tvSub)).setText(name + "  \u2022  " + role);

        roundCorners(root);

        // Notifications switch (remembered on the phone)
        SwitchCompat swNotif = findViewById(R.id.swNotif);
        swNotif.setChecked(prefs.getBoolean("notifications", true));
        swNotif.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean("notifications", checked).apply());
        findViewById(R.id.rowNotifications).setOnClickListener(v -> swNotif.toggle());

        // Rows
        findViewById(R.id.rowProfile).setOnClickListener(v -> showProfile());
        findViewById(R.id.rowPassword).setOnClickListener(v -> showChangePassword());
        findViewById(R.id.rowPrefs).setOnClickListener(v -> showPreferences());
        findViewById(R.id.rowHelp).setOnClickListener(v -> showHelp());
        findViewById(R.id.rowLogout).setOnClickListener(v -> confirmLogout());

        // Navigation
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.navHome).setOnClickListener(v -> openClearTop(Dashboard.class));
        findViewById(R.id.navStudents).setOnClickListener(v -> openClearTop(Students.class));
        findViewById(R.id.navGroups).setOnClickListener(v -> openClearTop(Groups.class));
        findViewById(R.id.navLabs).setOnClickListener(v -> openClearTop(Labs.class));
    }

    private void showProfile() {
        String name = prefs.getString("name", "Not set");
        String number = prefs.getString("number", "Not set");
        String programme = prefs.getString("programme", "");

        StringBuilder text = new StringBuilder();
        text.append("Name:  ").append(name).append('\n');
        if (programme.equals("Lecturer")) {
            text.append("Staff number:  ").append(number).append('\n');
            text.append("Role:  Lecturer");
        } else {
            text.append("Student number:  ").append(number).append('\n');
            text.append("Role:  Student\n");
            text.append("Programme:  ").append(programme.isEmpty() ? "Not set" : programme);
        }

        new AlertDialog.Builder(this)
                .setTitle("My Profile")
                .setMessage(text.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showChangePassword() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), 0);

        final EditText etCurrent = passwordBox("Current password");
        final EditText etNew = passwordBox("New password");
        final EditText etConfirm = passwordBox("Confirm new password");
        form.addView(etCurrent);
        form.addView(etNew);
        form.addView(etConfirm);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(form)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();

        // Set the click here so the box stays open when something is not valid
        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String current = etCurrent.getText().toString();
                    String pass = etNew.getText().toString();
                    String confirm = etConfirm.getText().toString();

                    if (current.isEmpty()) {
                        etCurrent.setError("Enter your current password");
                        return;
                    }
                    if (pass.length() < 8 || !pass.matches(".*[A-Za-z].*")
                            || !pass.matches(".*[0-9].*")) {
                        etNew.setError("At least 8 characters, with a letter and a number");
                        return;
                    }
                    if (!pass.equals(confirm)) {
                        etConfirm.setError("Passwords do not match");
                        return;
                    }

                    // TODO: send the new password to the server here
                    dialog.dismiss();
                    toast("Password changed");
                }));
        dialog.show();
    }

    private void showPreferences() {
        new AlertDialog.Builder(this)
                .setTitle("App Preferences")
                .setMessage("Remove the sign-in details saved by \"Remember me\" on this phone?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    prefs.edit().remove("user").apply();
                    toast("Saved sign-in removed");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showHelp() {
        new AlertDialog.Builder(this)
                .setTitle("Help & Support")
                .setMessage("Need help with STUDEX?\n\nContact your lecturer or your "
                        + "university's ICT support.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Do you want to log out of STUDEX?")
                .setPositiveButton("Log out", (dialog, which) -> {
                    // Back to Sign In and close every other screen
                    Intent intent = new Intent(this, SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------- Helpers ----------

    private EditText passwordBox(String hint) {
        EditText box = new EditText(this);
        box.setHint(hint);
        box.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        box.setSingleLine(true);
        return box;
    }

    // Go to a screen that may already be open, without stacking copies of it
    private void openClearTop(Class<?> screen) {
        Intent intent = new Intent(this, screen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    // tag="circle" makes a circle, a number like tag="20" makes rounded corners
    private void roundCorners(View v) {
        Object tag = v.getTag();
        if (tag instanceof String && v.getBackground() instanceof ColorDrawable) {
            GradientDrawable g = new GradientDrawable();
            g.setColor(((ColorDrawable) v.getBackground()).getColor());
            String t = (String) tag;
            if (t.equals("circle")) {
                g.setShape(GradientDrawable.OVAL);
            } else {
                g.setCornerRadius(Float.parseFloat(t)
                        * getResources().getDisplayMetrics().density);
            }
            v.setBackground(g);
        }
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int i = 0; i < group.getChildCount(); i++) {
                roundCorners(group.getChildAt(i));
            }
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}