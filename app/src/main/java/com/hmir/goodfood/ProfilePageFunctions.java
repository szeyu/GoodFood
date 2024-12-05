package com.example.goodfood;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class ProfilePageFunctions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page_functions);


        String fragmentType = getIntent().getStringExtra("FRAGMENT_TYPE");

        if (fragmentType != null) {
            Fragment fragment = null;

            // Determine which fragment to load
            switch (fragmentType) {
                case "EditProfile":
                    fragment = new EditProfileFragment();
                    break;
                case "Settings":
                    fragment = new SettingsFragment();
                    break;
                case "PrivacyPolicy":
                    fragment = new PrivacyPolicyFragment();
                    break;
                case "History":
                    fragment = new ThisMonthFragment();
                    break;
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FCProfileFunctions, fragment)
                        .commit();
            }
        } //

    }


}