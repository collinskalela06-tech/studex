package com.example.mwila;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Groups extends AppCompatActivity {

    private static final int[] COLORS = {0xFF1E4FD8, 0xFFE53935, 0xFF2E9E6B,
            0xFFF59E0B, 0xFF7C3AED};

    // Sample data. Later this will come from the database.
    private final ArrayList<String> groupNames = new ArrayList<>();
    private final ArrayList<Integer> memberCounts = new ArrayList<>();

    private LinearLayout list;
    private EditText etSearch;
    private TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupNames.add("Group A");
        memberCounts.add(5);
        groupNames.add("Group B");
        memberCounts.add(4);
        groupNames.add("Group C");
        memberCounts.add(4);
        groupNames.add("Group D");
        memberCounts.add(5);

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

        list = findViewById(R.id.listGroups);
        etSearch = findViewById(R.id.etSearch);
        tvCount = findViewById(R.id.tvCount);
        Button btnAdd = findViewById(R.id.btnAddGroup);

        roundCorners(root);
        btnAdd.setBackground(rounded(0xFF1E4FD8, 14));

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

        btnAdd.setOnClickListener(v -> showAddDialog());

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        findViewById(R.id.navHome).setOnClickListener(v -> openClearTop(Dashboard.class));
        findViewById(R.id.navStudents).setOnClickListener(v -> openClearTop(Students.class));
        findViewById(R.id.navLabs).setOnClickListener(v -> openClearTop(Labs.class));
        findViewById(R.id.navMore).setOnClickListener(v -> openClearTop(Settings.class));

        render();
    }

    private void render() {
        list.removeAllViews();
        String q = etSearch.getText().toString().trim().toLowerCase();

        int shown = 0;
        for (int i = 0; i < groupNames.size(); i++) {
            String name = groupNames.get(i);
            if (q.isEmpty() || name.toLowerCase().contains(q)) {
                addRow(name, memberCounts.get(i), COLORS[i % COLORS.length]);
                shown++;
            }
        }

        tvCount.setText(shown + (shown == 1 ? " group" : " groups"));

        if (shown == 0) {
            TextView empty = new TextView(this);
            empty.setText("No groups found");
            empty.setTextColor(0xFF667085);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(8), dp(32), dp(8), dp(32));
            list.addView(empty);
        }
    }

    private void addRow(String name, int members, int color) {
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
        row.setOnClickListener(v -> toast(name + " members coming next"));

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        int pad = dp(11);
        icon.setPadding(pad, pad, pad, pad);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(color);
        icon.setBackground(circle);
        icon.setImageResource(android.R.drawable.ic_menu_share);
        icon.setColorFilter(Color.WHITE);
        row.addView(icon);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tp.setMargins(dp(12), 0, dp(8), 0);
        texts.setLayoutParams(tp);
        row.addView(texts);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextColor(0xFF0B2A6F);
        tvName.setTextSize(16);
        tvName.setTypeface(null, Typeface.BOLD);
        texts.addView(tvName);

        TextView tvMembers = new TextView(this);
        tvMembers.setText(members == 1 ? "1 Member" : members + " Members");
        tvMembers.setTextColor(0xFF667085);
        tvMembers.setTextSize(12);
        texts.addView(tvMembers);

        TextView arrow = new TextView(this);
        arrow.setText("\u203A");
        arrow.setTextColor(0xFF667085);
        arrow.setTextSize(26);
        row.addView(arrow);

        list.addView(row);
    }

    private void showAddDialog() {
        final EditText input = new EditText(this);
        input.setHint("Group name (e.g. Group E)");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setSingleLine(true);

        FrameLayout container = new FrameLayout(this);
        container.setPadding(dp(20), dp(8), dp(20), 0);
        container.addView(input);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Group")
                .setView(container)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        // Set the Add click here so the box stays open when the name is not valid
        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        input.setError("Enter a group name");
                        return;
                    }
                    for (String existing : groupNames) {
                        if (existing.equalsIgnoreCase(name)) {
                            input.setError("This group already exists");
                            return;
                        }
                    }
                    groupNames.add(name);
                    memberCounts.add(0);
                    render();
                    dialog.dismiss();
                    toast(name + " added");
                }));
        dialog.show();
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