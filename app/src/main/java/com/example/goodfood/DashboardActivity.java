package com.example.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        String code = getIntent().getStringExtra("code");

        // set title for fragment : either "Today" or "This Month"
        TextView dashboardTitle = findViewById(R.id.dashboardTitle);
        if("today".equals(code)){
            dashboardTitle.setText(R.string.dashboardTodayTitle);
            replaceFragment(new TodayFragment());
        } else {
            dashboardTitle.setText(R.string.dashboardThisMonthTitle);
            replaceFragment(new ThisMonthFragment());
        }

    }

    private void replaceFragment(Fragment fragment) {
        // to replace the fragment displayed in the middle
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}