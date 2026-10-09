package com.example.mwila;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Labs extends AppCompatActivity {

    private static final int[] COLORS = {0xFF1E4FD8, 0xFFE53935, 0xFF2E9E6B,
            0xFFF59E0B, 0xFF7C3AED};

    // Sample data from the design. Later this will come from the database.
    private final String[] titles = {"Lab 1", "Lab 2", "Lab 3", "Lab 4", "Lab 5"};
    private final String[] subjects = {"Programming Fundamentals", "Data Structures",
            "Networks", "Operating Systems", "Databases"};

    private LinearLayout list;
    private EditText etSearch;
    private TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labs);

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

        list = findViewById(R.id.listLabs);
        etSearch = findViewById(R.id.etSearch);
        tvCount = findViewById(R.id.tvCount);

        roundCorners(root);

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

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.navHome).setOnClickListener(v -> openClearTop(Dashboard.class));
        findViewById(R.id.navStudents).setOnClickListener(v -> openClearTop(Students.class));
        findViewById(R.id.navGroups).setOnClickListener(v -> openClearTop(Groups.class));
        findViewById(R.id.navMore).setOnClickListener(v -> openClearTop(Settings.class));

        render();
    }

    private void render() {
        list.removeAllViews();
        String q = etSearch.getText().toString().trim().toLowerCase();

        int shown = 0;
        for (int i = 0; i < titles.length; i++) {
            if (q.isEmpty() || titles[i].toLowerCase().contains(q)
                    || subjects[i].toLowerCase().contains(q)) {
                addRow(titles[i], subjects[i], COLORS[i % COLORS.length]);
                shown++;
            }
        }

        tvCount.setText(shown + (shown == 1 ? " lab" : " labs"));

        if (shown == 0) {
            TextView empty = new TextView(this);
            empty.setText("No labs found");
            empty.setTextColor(0xFF667085);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(8), dp(32), dp(8), dp(32));
            list.addView(empty);
        }
    }

    private void addRow(String title, String subject, int color) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(14), dp(14), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(10));
        row.setLayoutParams(lp);
        row.setBackground(rounded(Color.WHITE, 16));
        row.setElevation(dp(2));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(v -> toast(title + " details coming next"));

        // The circle shows the lab number (for example "Lab 3" gives 3)
        TextView badge = new TextView(this);
        badge.setLayoutParams(new LinearLayout.LayoutParams(dp(46), dp(46)));
        badge.setGravity(Gravity.CENTER);
        badge.setText(title.replaceAll("\\D+", ""));
        badge.setTextColor(Color.WHITE);
        badge.setTextSize(18);
        badge.setTypeface(null, Typeface.BOLD);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(color);
        badge.setBackground(circle);
        row.addView(badge);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tp.setMargins(dp(12), 0, dp(8), 0);
        texts.setLayoutParams(tp);
        row.addView(texts);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(0xFF0B2A6F);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        texts.addView(tvTitle);

        TextView tvSubject = new TextView(this);
        tvSubject.setText(subject);
        tvSubject.setTextColor(0xFF667085);
        tvSubject.setTextSize(12);
        texts.addView(tvSubject);

        TextView arrow = new TextView(this);
        arrow.setText("\u203A");
        arrow.setTextColor(0xFF667085);
        arrow.setTextSize(26);
        row.addView(arrow);

        list.addView(row);
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