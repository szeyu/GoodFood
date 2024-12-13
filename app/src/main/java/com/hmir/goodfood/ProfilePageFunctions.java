//package com.example.goodfood;
package com.hmir.goodfood;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.hmir.goodfood.EditProfileFragment;

public class ProfilePageFunctions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page_functions);


        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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


    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle back button click
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}