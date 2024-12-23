package com.hmir.goodfood;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hmir.goodfood.utilities.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(MainActivity.this);

        // Check the user's authentication status
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is signed in
            // Print all user info
            printUserInfo(currentUser);

            // Check if user data exists in SharedPreferences
            if (isUserDataAvailable()) {
                // Redirect to home page
                redirectToHomePage();
            } else {
                // Redirect to welcome page to collect user info
                redirectToWelcomePage();
            }
        } else {
            // User is not signed in, redirect to login page
            redirectToLoginPage();
        }

        // Finish MainActivity as it's only for redirection purposes
        finish();
    }

    private boolean isUserDataAvailable() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Check if the key "Username" or any other mandatory field exists
        return sharedPreferences.contains("Username") &&
                sharedPreferences.contains("Age") &&
                sharedPreferences.contains("Height") &&
                sharedPreferences.contains("Weight")&&
                sharedPreferences.contains("DietTypes");
    }
    private void printUserInfo(FirebaseUser user){
        String TAG = "Firebase";
        if (user != null) {
            // Print user info
            Log.d(TAG, "User Display Name: " + user.getDisplayName());
            Log.d(TAG, "User Email: " + user.getEmail());
            Log.d(TAG, "User Photo URL: " + user.getPhotoUrl());
            Log.d(TAG, "User UID: " + user.getUid());
        }
    }

    private void redirectToHomePage() {
        Intent intent = new Intent(MainActivity.this, HomePage.class);
        startActivity(intent);
    }

    private void redirectToLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginPage.class);
        startActivity(intent);
    }

    private void redirectToWelcomePage() {
        Intent intent = new Intent(MainActivity.this, welcomePage.class);
        startActivity(intent);
    }
}