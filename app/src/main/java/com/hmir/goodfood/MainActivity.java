package com.hmir.goodfood;

import android.content.Intent;

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

            // redirect to home page
            redirectToHomePage();
        } else {
            // User is not signed in, redirect to login page
            redirectToLoginPage();
        }

        // Finish MainActivity as it's only for redirection purposes
        finish();
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
}