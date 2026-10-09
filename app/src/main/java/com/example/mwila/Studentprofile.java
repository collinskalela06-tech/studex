package com.example.mwila;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Studentprofile extends AppCompatActivity {

    private TextView tabOverview, tabAcademic, tabAttendance;
    private View blockOverview, blockAcademic, blockAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentprofile);

        // Keep the header below the status bar and the button above the gesture bar
        View root = findViewById(R.id.root);
        View header = findViewById(R.id.headerBox);
        final int headerTop = header.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            header.setPadding(header.getPaddingLeft(), headerTop + bars.top,
                    header.getPaddingRight(), header.getPaddingBottom());
            root.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });

        // Details sent from the Student List
        String name = textOr(getIntent().getStringExtra("name"), "Student");
        String number = textOr(getIntent().getStringExtra("number"), "-");
        String group = textOr(getIntent().getStringExtra("group"), "-");
        String programme = textOr(getIntent().getStringExtra("programme"), "-");

        ((TextView) findViewById(R.id.tvAvatar)).setText(name.substring(0, 1).toUpperCase());
        ((TextView) findViewById(R.id.tvName)).setText(name);
        ((TextView) findViewById(R.id.tvSub)).setText(number + "  \u2022  " + group);
        ((TextView) findViewById(R.id.tvProgramme)).setText(programme);
        ((TextView) findViewById(R.id.tvGroup)).setText(group);

        // Sample details. Later these will come from the database.
        String email = name.toLowerCase().trim().replaceAll("\\s+", ".") + "@example.com";
        ((TextView) findViewById(R.id.tvEmail)).setText(email);
        ((TextView) findViewById(R.id.tvPhone)).setText("+260 97 123 4567");

        roundCorners(root);

        // Tabs
        tabOverview = findViewById(R.id.tabOverview);
        tabAcademic = findViewById(R.id.tabAcademic);
        tabAttendance = findViewById(R.id.tabAttendance);
        blockOverview = findViewById(R.id.blockOverview);
        blockAcademic = findViewById(R.id.blockAcademic);
        blockAttendance = findViewById(R.id.blockAttendance);

        tabOverview.setOnClickListener(v -> selectTab(0));
        tabAcademic.setOnClickListener(v -> selectTab(1));
        tabAttendance.setOnClickListener(v -> selectTab(2));
        selectTab(0);

        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setBackground(rounded(0xFF1E4FD8, 14));
        btnEdit.setOnClickListener(v ->
                Toast.makeText(this, "Edit Profile coming next", Toast.LENGTH_SHORT).show());

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void selectTab(int index) {
        blockOverview.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        blockAcademic.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        blockAttendance.setVisibility(index == 2 ? View.VISIBLE : View.GONE);
        styleTab(tabOverview, index == 0);
        styleTab(tabAcademic, index == 1);
        styleTab(tabAttendance, index == 2);
    }

    private void styleTab(TextView tab, boolean selected) {
        tab.setBackground(rounded(selected ? 0xFF1E4FD8 : Color.WHITE, 12));
        tab.setTextColor(selected ? Color.WHITE : 0xFF667085);
        tab.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
    }

    private String textOr(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }

    private GradientDrawable rounded(int color, int radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        return g;
    }

    // tag="circle" makes a circle, tag="20" makes 20dp rounded corners
    private void roundCorners(View v) {
        Object tag = v.getTag();
        if (tag instanceof String && v.getBackground() instanceof ColorDrawable) {
            int color = ((ColorDrawable) v.getBackground()).getColor();
            String t = (String) tag;
            if (t.equals("circle")) {
                GradientDrawable g = new GradientDrawable();
                g.setShape(GradientDrawable.OVAL);
                g.setColor(color);
                v.setBackground(g);
            } else {
                v.setBackground(rounded(color, Integer.parseInt(t)));
            }
        }
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int i = 0; i < group.getChildCount(); i++) {
                roundCorners(group.getChildAt(i));
            }
        }
    }
}