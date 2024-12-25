package com.hmir.goodfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;

public class HomePage extends AppCompatActivity {

    private TextView TVNameHomePage;

    //pfp
    private ImageView IVProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);


        // Initialize the TextView
        TVNameHomePage = findViewById(R.id.TVNameHomePage);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("Username", "Guest");

        // Set the username in the TextView
        TVNameHomePage.setText( username );

    }

    public void toSearchPage(View view) {
        Intent intent = new Intent(HomePage.this, SearchActivity.class);
        startActivity(intent);
    }

    public void toTodayStats(View view){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("code", "today");
        startActivity(intent);
    }

    public void toThisMonthStats (View view){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("code", "month");
        startActivity(intent);
    }

    // In HomePage.java or ProfilePage.java
    public void refreshProfileData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("Username", "Guest");

        // Assuming you have a TextView to display the username
        TextView usernameTextView = findViewById(R.id.TVNameHomePage); // Or the corresponding ID in ProfilePage
        usernameTextView.setText(username);
    }

    /* pfp
    @Override
    protected void onResume() {
        super.onResume();

        // Ensure that IVProfile is not null before setting its image
        IVProfile = findViewById(R.id.IVProfile);
        if (IVProfile != null) {
            refreshProfilePicture();
        } else {
            Log.e("HomePage", "IVProfile ImageView is null");
        }
    }


    private void refreshProfilePicture() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Get the profile picture URI from SharedPreferences
        String profilePicUri = sharedPreferences.getString("ProfilePicUri", "android.resource://" + getPackageName() + "/" + R.drawable.profile_pic_circle);

        IVProfile = findViewById(R.id.IVProfile); // Assuming you have an ImageView in HomePage

        try {
            // Check if the URI is different from the default URI
            if (!profilePicUri.equals("android.resource://" + getPackageName() + "/" + R.drawable.profile_pic_circle)) {
                Uri profileUri = Uri.parse(profilePicUri); // Try to parse the URI
                IVProfile.setImageURI(profileUri); // Set the image URI
            } else {
                IVProfile.setImageResource(R.drawable.profile_pic_circle); // Use default profile picture if URI is missing
            }
        } catch (Exception e) {
            // Log the error if URI parsing fails
            Log.e("HomePage", "Error loading profile picture URI", e);
            IVProfile.setImageResource(R.drawable.profile_pic_circle); // Set default image in case of error
        }
    }
    */

}