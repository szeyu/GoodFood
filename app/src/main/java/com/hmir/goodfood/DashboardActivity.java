package com.hmir.goodfood;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hmir.goodfood.R;

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