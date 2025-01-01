package com.hmir.goodfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * ProfilePage is an activity that allows users to view and manage their profile.
 * It provides functionalities for:
 * - Logging out from Google and Firebase
 * - Navigating to the edit profile, history, settings, and privacy policy sections.
 *
 * This activity requires a valid Google Sign-In configuration.
 */
public class ProfilePage extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private TextView TVName;
    private ImageView IVProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize the TextView and ImageView
        TVName = findViewById(R.id.TVName);
        IVProfile = findViewById(R.id.IVProfile);

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("Username", "Guest");

        // Set the username in the TextView
        TVName.setText(username);

        loadGoogleProfileData();


    }

    /**
     * Logs the user out of Google, clears user data from SharedPreferences,
     * signs out from Firebase, and redirects the user to the MainActivity.
     *
     * @param view the View that triggers this method (e.g., a Button).
     */
    public void logoutGoogle(View view) {
        // Remove SharedPreference user data
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Log.d("ProfilePage", "User logged out from Google");
            // Redirect to MainActivity
            Intent intent = new Intent(ProfilePage.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close ProfilePage
        });
    }

    public void goEditProfile(View view){
        Intent intent = new Intent(ProfilePage.this, ProfilePageFunctions.class);
        intent.putExtra("FRAGMENT_TYPE", "EditProfile");
        startActivity(intent);
    }

    public void goToHistory(View view){
        Intent intent = new Intent(ProfilePage.this, ProfilePageFunctions.class);
        intent.putExtra("FRAGMENT_TYPE", "History");
        startActivity(intent);
    }

    public void goToSettings(View view){
        Intent intent = new Intent(ProfilePage.this, ProfilePageFunctions.class);
        intent.putExtra("FRAGMENT_TYPE", "Settings");
        startActivity(intent);
    }

    public void goToPrivacyPolicy(View view){
        Intent intent = new Intent(ProfilePage.this, ProfilePageFunctions.class);
        intent.putExtra("FRAGMENT_TYPE", "PrivacyPolicy");
        startActivity(intent);
    }

    /**
     * Refreshes the user profile data displayed in the UI.
     * Retrieves the username from SharedPreferences and updates the corresponding TextView.
     * If no username is found, defaults to "Guest".
     *
     * <p>This method should be called whenever the user profile data needs to be updated,
     * such as after profile edits or when returning to the profile page.</p>
     *
     * @see SharedPreferences
     * @see TextView
     */
    public void refreshProfileData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("Username", "Guest");

        // Assuming you have a TextView to display the username
        TextView usernameTextView = findViewById(R.id.TVName); // Or the corresponding ID in ProfilePage
        usernameTextView.setText(username);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProfileData(); // Refresh data when the profile page is resumed
    }

    /**
     * Load user profile data, including the name and profile picture, directly from Firebase.
     */
    private void loadGoogleProfileData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Set the display name
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                TVName.setText(displayName);
            } else {
                TVName.setText("Guest"); // Fallback if no display name is available
            }

            // Load the profile picture
            Uri profilePicUri = user.getPhotoUrl();
            if (profilePicUri != null) {
                // Use Glide to load the image into IVProfile
                Glide.with(this)
                        .load(profilePicUri.toString()) // Convert URI to string
                        .placeholder(R.drawable.profile_pic) // Default placeholder image
                        .error(R.drawable.profile_pic) // Error image
                        .into(IVProfile);
            } else {
                IVProfile.setImageResource(R.drawable.profile_pic); // Fallback default image
            }
        } else {
            // Set fallback values if no user is logged in
            TVName.setText("Guest");
            IVProfile.setImageResource(R.drawable.profile_pic);
        }
    }

}

