package com.example.mwila;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.Arrays;

public class Students extends AppCompatActivity {

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

    private final String[] groupItems = {"All Groups", "Group A", "Group B", "Group C", "Group D"};
    private final String[] progItems = {"All Programmes", "Computer Science",
            "Information Technology", "Information Systems", "Software Engineering"};

    private final ArrayList<Student> students = new ArrayList<>();
    private boolean isLecturer;

    private LinearLayout list;
    private EditText etSearch;
    private Spinner spGroup, spProgramme;
    private TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        // Lecturers can add and delete students. Students can only view and search.
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        isLecturer = "Lecturer".equals(prefs.getString("programme", ""));
        loadStudents();

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

        list = findViewById(R.id.listStudents);
        etSearch = findViewById(R.id.etSearch);
        spGroup = findViewById(R.id.spGroup);
        spProgramme = findViewById(R.id.spProgramme);
        tvCount = findViewById(R.id.tvCount);

        roundCorners(root);

        spGroup.setAdapter(makeAdapter(groupItems));
        spProgramme.setAdapter(makeAdapter(progItems));

        AdapterView.OnItemSelectedListener filterListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        render();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                };
        spGroup.setOnItemSelectedListener(filterListener);
        spProgramme.setOnItemSelectedListener(filterListener);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) { }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                render();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // "Add Student" button, only for lecturers
        if (isLecturer) {
            addStudentButton((LinearLayout) root, bottomBar);
        }

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.navHome).setOnClickListener(v -> openClearTop(Dashboard.class));
        findViewById(R.id.navGroups).setOnClickListener(v -> openClearTop(Groups.class));
        findViewById(R.id.navLabs).setOnClickListener(v -> openClearTop(Labs.class));
        findViewById(R.id.navMore).setOnClickListener(v -> openClearTop(Settings.class));

        render();

        // Opened from a tile on the Lecturer Dashboard (only the first time, not on rotation)
        if (savedInstanceState == null) {
            String action = getIntent().getStringExtra("action");
            if ("add".equals(action) && isLecturer) {
                showAddDialog();
            } else if ("search".equals(action)) {
                etSearch.requestFocus();
                etSearch.postDelayed(() -> {
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
                }, 300);
            } else if ("delete".equals(action) && isLecturer) {
                toast("Tap the red trash icon next to a student to delete");
            }
        }
    }

    // ---------- Saved students (kept on the phone until the database is ready) ----------

    private void loadStudents() {
        students.clear();
        String json = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_STUDENTS, null);

        if (json == null) {
            // First time: start with the sample students from the design
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

    // ---------- Screen ----------

    private void render() {
        list.removeAllViews();

        String q = etSearch.getText().toString().trim().toLowerCase();
        int groupPos = spGroup.getSelectedItemPosition();
        int progPos = spProgramme.getSelectedItemPosition();
        String group = groupPos > 0 ? groupItems[groupPos] : null;
        String programme = progPos > 0 ? progItems[progPos] : null;

        int count = 0;
        for (Student s : students) {
            boolean okGroup = group == null || s.group.equals(group);
            boolean okProgramme = programme == null || s.programme.equals(programme);
            boolean okSearch = q.isEmpty()
                    || s.name.toLowerCase().contains(q) || s.number.contains(q);
            if (okGroup && okProgramme && okSearch) {
                addRow(s);
                count++;
            }
        }

        tvCount.setText(count + (count == 1 ? " student" : " students"));

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No students found");
            empty.setTextColor(0xFF667085);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(8), dp(32), dp(8), dp(32));
            list.addView(empty);
        }
    }

    private void addRow(Student student) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(12), dp(12), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(8));
        row.setLayoutParams(lp);
        row.setBackground(rounded(Color.WHITE, 16));
        row.setElevation(dp(2));
        row.setClickable(true);
        row.setFocusable(true);

        // Open the Student Profile with this student's details
        row.setOnClickListener(v -> {
            Intent intent = new Intent(this, Studentprofile.class);
            intent.putExtra("name", student.name);
            intent.putExtra("number", student.number);
            intent.putExtra("group", student.group);
            intent.putExtra("programme", student.programme);
            startActivity(intent);
        });

        TextView avatar = new TextView(this);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        avatar.setGravity(Gravity.CENTER);
        avatar.setText(student.name.substring(0, 1).toUpperCase());
        avatar.setTextColor(Color.WHITE);
        avatar.setTextSize(18);
        avatar.setTypeface(null, Typeface.BOLD);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(0xFF1E4FD8);
        avatar.setBackground(circle);
        row.addView(avatar);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tp.setMargins(dp(12), 0, dp(8), 0);
        texts.setLayoutParams(tp);
        row.addView(texts);

        TextView tvName = new TextView(this);
        tvName.setText(student.name);
        tvName.setTextColor(0xFF0B2A6F);
        tvName.setTextSize(15);
        tvName.setTypeface(null, Typeface.BOLD);
        texts.addView(tvName);

        TextView tvSub = new TextView(this);
        tvSub.setText(student.number + "  \u2022  " + student.group);
        tvSub.setTextColor(0xFF667085);
        tvSub.setTextSize(12);
        texts.addView(tvSub);

        if (isLecturer) {
            // Trash icon: only lecturers can delete
            ImageView delete = new ImageView(this);
            LinearLayout.LayoutParams delLp = new LinearLayout.LayoutParams(dp(40), dp(40));
            delete.setLayoutParams(delLp);
            delete.setPadding(dp(8), dp(8), dp(8), dp(8));
            delete.setImageResource(android.R.drawable.ic_menu_delete);
            delete.setColorFilter(0xFFE53935, PorterDuff.Mode.SRC_IN);
            delete.setContentDescription("Delete " + student.name);
            delete.setClickable(true);
            delete.setFocusable(true);
            delete.setOnClickListener(v -> confirmDelete(student));
            row.addView(delete);
        } else {
            TextView arrow = new TextView(this);
            arrow.setText("\u203A");
            arrow.setTextColor(0xFF667085);
            arrow.setTextSize(26);
            row.addView(arrow);
        }

        list.addView(row);
    }

    // ---------- Add and delete (lecturers) ----------

    private void addStudentButton(LinearLayout root, View bottomBar) {
        Button btn = new Button(this);
        btn.setText("+  Add Student");
        btn.setAllCaps(false);
        btn.setTextColor(Color.WHITE);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setBackground(rounded(0xFF1E4FD8, 14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(56));
        lp.setMargins(dp(16), dp(4), dp(16), dp(12));
        btn.setLayoutParams(lp);
        root.addView(btn, root.indexOfChild(bottomBar));
        btn.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), 0);

        final EditText etName = new EditText(this);
        etName.setHint("Full name");
        etName.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_WORDS
                | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        etName.setSingleLine(true);
        form.addView(etName);

        final EditText etNumber = new EditText(this);
        etNumber.setHint("Student number");
        etNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        etNumber.setSingleLine(true);
        form.addView(etNumber);

        LinearLayout.LayoutParams spLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(48));
        spLp.topMargin = dp(8);

        final Spinner spG = new Spinner(this);
        spG.setLayoutParams(spLp);
        spG.setAdapter(makeAdapter(Arrays.copyOfRange(groupItems, 1, groupItems.length)));
        form.addView(spG);

        final Spinner spP = new Spinner(this);
        spP.setLayoutParams(spLp);
        spP.setAdapter(makeAdapter(Arrays.copyOfRange(progItems, 1, progItems.length)));
        form.addView(spP);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Student")
                .setView(form)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        // Set the Add click here so the box stays open when something is not valid
        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String name = etName.getText().toString().trim();
                    String number = etNumber.getText().toString().trim();

                    if (name.isEmpty()) {
                        etName.setError("Enter the student's name");
                        return;
                    }
                    if (!number.matches("\\d{6,12}")) {
                        etNumber.setError("Enter 6 to 12 digits");
                        return;
                    }
                    for (Student existing : students) {
                        if (existing.number.equals(number)) {
                            etNumber.setError("This student number already exists");
                            return;
                        }
                    }

                    students.add(0, new Student(name, number,
                            (String) spG.getSelectedItem(), (String) spP.getSelectedItem()));
                    saveStudents();

                    // Clear the search and filters so the new student is visible
                    etSearch.setText("");
                    spGroup.setSelection(0);
                    spProgramme.setSelection(0);
                    render();

                    dialog.dismiss();
                    toast(name + " added");
                }));
        dialog.show();
    }

    private void confirmDelete(Student student) {
        new AlertDialog.Builder(this)
                .setTitle("Delete student")
                .setMessage("Delete " + student.name + " (" + student.number
                        + ")? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    students.remove(student);
                    saveStudents();
                    render();
                    toast(student.name + " deleted");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------- Helpers ----------

    // Spinner whose selected text is always dark, so it shows on the white box
    private ArrayAdapter<String> makeAdapter(String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(0xFF0B2A6F);
                tv.setTextSize(13);
                return tv;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    // Go to a screen that may already be open, without stacking copies of it
    private void openClearTop(Class<?> screen) {
        Intent intent = new Intent(this, screen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private GradientDrawable rounded(int color, int radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radiusDp));
        return g;
    }

    // tag="14" gives 14dp rounded corners to a view with a solid color
    private void roundCorners(View v) {
        Object tag = v.getTag();
        if (tag instanceof String && v.getBackground() instanceof ColorDrawable) {
            int color = ((ColorDrawable) v.getBackground()).getColor();
            v.setBackground(rounded(color, Integer.parseInt((String) tag)));
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