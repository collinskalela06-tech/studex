package com.example.mwila;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("studex", MODE_PRIVATE);
        String name = prefs.getString("name", "Student").trim();
        if (name.isEmpty()) name = "Student";
        String programme = prefs.getString("programme", "");
        boolean isLecturer = "Lecturer".equals(programme);

        // Load Student Dashboard or Lecturer Dashboard based on role
        if (isLecturer) {
            setContentView(R.layout.activity_lecturer);
        } else {
            setContentView(R.layout.activity_dashboard);
        }

        // Keep the header below the status bar and the bottom bar above the gesture bar
        View root = findViewById(R.id.root);
        View header = findViewById(R.id.headerBox);
        View bottomBar = findViewById(R.id.bottomBar);
        if (root != null && header != null && bottomBar != null) {
            final int headerTop = header.getPaddingTop();
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                header.setPadding(header.getPaddingLeft(), headerTop + bars.top,
                        header.getPaddingRight(), header.getPaddingBottom());
                bottomBar.setPadding(0, 0, 0, bars.bottom);
                return insets;
            });
        }

        TextView tvHello = findViewById(R.id.tvHello);
        if (tvHello != null) tvHello.setText("Hello, " + name);

        TextView tvAvatar = findViewById(R.id.tvAvatar);
        if (tvAvatar != null) tvAvatar.setText(name.substring(0, 1).toUpperCase());

        TextView tvRole = findViewById(R.id.tvRole);
        if (tvRole != null) tvRole.setText(isLecturer ? "Lecturer" : "Student");

        if (root != null) roundCorners(root);

        if (isLecturer) {
            // Lecturer specific shortcuts
            openStudents(R.id.tileAdd, "add");
            openStudents(R.id.tileView, null);
            openStudents(R.id.tileSearch, "search");
            openStudents(R.id.tileDelete, "delete");
            open(R.id.tileGroups, Groups.class);
            open(R.id.tileReports, Reports.class);
        } else {
            // Student specific shortcuts
            open(R.id.tileStudents, Students.class);
            open(R.id.tileGroups, Groups.class);
            open(R.id.tileLecturers, Lecturer.class);
            open(R.id.tileLabs, Labs.class);
            open(R.id.tileReports, Reports.class);
            open(R.id.tileSettings, Settings.class);
        }

        // Bottom navigation bar available for all users
        open(R.id.navStudents, Students.class);
        open(R.id.navGroups, Groups.class);
        open(R.id.navLabs, Labs.class);
        open(R.id.navMore, Settings.class);
    }

    private void openStudents(int id, String action) {
        View v = findViewById(id);
        if (v != null) {
            v.setOnClickListener(view -> {
                Intent intent = new Intent(this, Students.class);
                if (action != null) intent.putExtra("action", action);
                startActivity(intent);
            });
        }
    }

    private void open(int id, Class<?> screen) {
        View v = findViewById(id);
        if (v != null) {
            v.setOnClickListener(view -> startActivity(new Intent(this, screen)));
        }
    }

    // tag="circle" makes a circle, a number like tag="16" makes rounded corners
    private void roundCorners(View v) {
        Object tag = v.getTag();
        if (tag instanceof String && v.getBackground() instanceof ColorDrawable) {
            GradientDrawable g = new GradientDrawable();
            g.setColor(((ColorDrawable) v.getBackground()).getColor());
            String t = (String) tag;
            if (t.equals("circle")) {
                g.setShape(GradientDrawable.OVAL);
            } else {
                try {
                    g.setCornerRadius(Float.parseFloat(t)
                            * getResources().getDisplayMetrics().density);
                } catch (NumberFormatException ignored) { }
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
}