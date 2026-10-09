package com.example.mwila;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class Reports extends AppCompatActivity {

    private static final String PREFS = "studex";
    private static final String KEY_STUDENTS = "students_json";

    private static class Student {
        final String name, number, group, programme;

        Student(String name, String number, String group, String programme) {
            this.name = name;
            this.number = number;
            this.group = group;
            this.programme = programme;
        }
    }

    private final ArrayList<Student> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

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

        roundCorners(root);

        // Reports
        findViewById(R.id.rowExport).setOnClickListener(v -> exportCsv());
        findViewById(R.id.rowByGroup).setOnClickListener(v ->
                showCounts("Students per Group", true));
        findViewById(R.id.rowByProgramme).setOnClickListener(v ->
                showCounts("Students per Programme", false));

        // Navigation
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.navHome).setOnClickListener(v -> openClearTop(Dashboard.class));
        findViewById(R.id.navStudents).setOnClickListener(v -> openClearTop(Students.class));
        findViewById(R.id.navGroups).setOnClickListener(v -> openClearTop(Groups.class));
        findViewById(R.id.navLabs).setOnClickListener(v -> openClearTop(Labs.class));
        findViewById(R.id.navMore).setOnClickListener(v -> openClearTop(Settings.class));
    }

    // Reload the numbers every time this screen comes back to the front
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        loadStudents();

        HashSet<String> groupSet = new HashSet<>();
        for (Student s : students) groupSet.add(s.group);

        ((TextView) findViewById(R.id.tvStudents)).setText(String.valueOf(students.size()));
        ((TextView) findViewById(R.id.tvGroups)).setText(String.valueOf(groupSet.size()));
        ((TextView) findViewById(R.id.tvLabs)).setText("5");
    }

    // ---------- Reports ----------

    private void exportCsv() {
        if (students.isEmpty()) {
            toast("No students to export");
            return;
        }

        StringBuilder csv = new StringBuilder("Name,Student Number,Group,Programme\n");
        for (Student s : students) {
            csv.append(quote(s.name)).append(',')
                    .append(quote(s.number)).append(',')
                    .append(quote(s.group)).append(',')
                    .append(quote(s.programme)).append('\n');
        }

        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, "STUDEX student list");
        send.putExtra(Intent.EXTRA_TEXT, csv.toString());
        startActivity(Intent.createChooser(send, "Share student list"));
    }

    private void showCounts(String title, boolean byGroup) {
        if (students.isEmpty()) {
            toast("No students yet");
            return;
        }

        Map<String, Integer> counts = new TreeMap<>();
        for (Student s : students) {
            String key = byGroup ? s.group : s.programme;
            Integer old = counts.get(key);
            counts.put(key, old == null ? 1 : old + 1);
        }

        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            text.append(e.getKey()).append(":  ").append(e.getValue())
                    .append(e.getValue() == 1 ? " student" : " students").append('\n');
        }
        text.append("\nTotal:  ").append(students.size());

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(text.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private String quote(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    // ---------- Saved students (same list the Student List screen uses) ----------

    private void loadStudents() {
        students.clear();
        String json = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_STUDENTS, null);

        if (json == null) {
            // Same sample students as the Student List screen
            students.add(new Student("Hope Nsana", "20261234", "Group A", "Computer Science"));
            students.add(new Student("Collins Kalela", "20265456", "Group B", "Information Technology"));
            students.add(new Student("Judith Mushabati", "20260321", "Group C", "Information Systems"));
            students.add(new Student("Grace Chilufya", "20260418", "Group D", "Computer Science"));
            students.add(new Student("Mayamiko Phiri", "20260567", "Group A", "Software Engineering"));
            students.add(new Student("Nancy Mailoni", "20260611", "Group B", "Information Technology"));
            students.add(new Student("Joseph Lungu", "20260702", "Group C", "Computer Science"));
            students.add(new Student("Natasha Zulu", "20260815", "Group D", "Information Systems"));
            saveStudents();
            return;
        }

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                students.add(new Student(o.getString("name"), o.getString("number"),
                        o.getString("group"), o.getString("programme")));
            }
        } catch (JSONException e) {
            students.clear();
        }
    }

    private void saveStudents() {
        JSONArray array = new JSONArray();
        try {
            for (Student s : students) {
                JSONObject o = new JSONObject();
                o.put("name", s.name);
                o.put("number", s.number);
                o.put("group", s.group);
                o.put("programme", s.programme);
                array.put(o);
            }
        } catch (JSONException e) {
            return;
        }
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putString(KEY_STUDENTS, array.toString()).apply();
    }

    // ---------- Helpers ----------

    // Go to a screen that may already be open, without stacking copies of it
    private void openClearTop(Class<?> screen) {
        Intent intent = new Intent(this, screen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}