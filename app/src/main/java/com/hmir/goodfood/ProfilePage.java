package com.hmir.goodfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


/**
 * The ProfilePage class represents an activity where users can view and manage their profile details.
 * This includes options for signing out, editing profile information, accessing activity history,
 * adjusting settings, and reviewing the privacy policy. It also integrates with Google Sign-In
 * and Firebase for authentication purposes.
 */
public class ProfilePage extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private TextView TVName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        //
        // Initialize the TextView
        TVName = findViewById(R.id.TVName);

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("Username", "Guest");

        // Set the username in the TextView
        TVName.setText(username);
        //

    }

    public void logoutGoogle(View view) {
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

}

