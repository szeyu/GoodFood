package com.example.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilePage extends AppCompatActivity {

    private TextView TVName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);



        // Get the current username from the TextView (this will be the original username when ProfilePage is first created)
        //String currentUsername = TVName.getText().toString();

        // Get the username passed from the EditProfile fragment
        String username = getIntent().getStringExtra("username");

        // Find the TextView to display the username
        TextView TVName = findViewById(R.id.TVName);

        // Update the TextView with the new username
        if (username != null && !username.isEmpty()) {
            TVName.setText(username);
        } else {
            // Set the original username here if needed
            TVName.setText("John Doe");  // Replace with your original username if stored elsewhere
        }

/*
        // Get the new username passed from EditProfile fragment
        String newUsername = getIntent().getStringExtra("newUsername");
        if (newUsername != null) {
            // Update the TextView (TVName) with the new username
            TextView usernameTextView = findViewById(R.id.TVName);
            usernameTextView.setText(newUsername);
        } */
    }


    public void toEditProfile (View view){
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ProfilePageMain,editProfileFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onButtonClicked(View view) {
        Intent intent = new Intent(this, ProfilePageFunctions.class);

        // Pass the fragment type based on button ID
        if (view.getId() == R.id.BtnEditProfile) {
            intent.putExtra("FRAGMENT_TYPE", "EditProfile");
        } else if (view.getId() == R.id.BtnSettings) {
            intent.putExtra("FRAGMENT_TYPE", "Settings");
        } else if (view.getId() == R.id.BtnPrivacyPolicy) {
            intent.putExtra("FRAGMENT_TYPE", "PrivacyPolicy");
        } else if (view.getId() == R.id.BtnHistory) {
            intent.putExtra("FRAGMENT_TYPE", "History");
        }

        startActivity(intent);
    }

}


