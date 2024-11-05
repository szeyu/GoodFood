package com.example.goodfood;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String code;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String code) {
        super(fragmentActivity);
        this.code = code;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if ("today".equals(code)) {
            return new TodayFragment();
        } else if ("month".equals(code)) {
            return new ThisMonthFragment();
        }
        // Default case or handle other options
        return new ErrorFragment();
    }

    @Override
    public int getItemCount() {
        return 1; // Only one fragment is shown based on the "code"
    }
}
