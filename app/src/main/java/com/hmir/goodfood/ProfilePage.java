package com.hmir.goodfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

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
}

