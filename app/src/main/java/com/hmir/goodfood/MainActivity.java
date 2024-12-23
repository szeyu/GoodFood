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
import com.hmir.goodfood.utilities.UserHelper;

/**
 * The MainActivity class is the entry point of the application. It checks the user's
 * authentication and Firestore status to redirect them to the appropriate page:
 * - If the user exists in Firestore and is authenticated, they are redirected to the HomePage.
 * - If the user does not exist in Firestore, they are redirected to the WelcomePage.
 * - If the user is not authenticated, they are redirected to the LoginPage.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(MainActivity.this);

        // Check if user exists in Firestore using email
        isUserExistInFirestore(exist -> {
            if (exist) {
                UserHelper userHelper = new UserHelper();
                // Check the user's authentication status
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // User is signed in
                    // Print all user info
                    printUserInfo(currentUser);

                    // Save user data from Firestore to SharedPreferences
                    if(!isUserEmailMatched())
                        userHelper.saveUserDataFromFirestoreToSharedPreferences(getApplicationContext());
                    redirectToHomePage();
                } else {
                    // User is not signed in
                    redirectToLoginPage();
                }
            } else {
                // Go to welcome page if user doesn't exist in Firestore
                redirectToWelcomePage();
            }
        });

        // Finish MainActivity as it's only for redirection purposes
        finish();
    }

    /**
     * Checks if the user's email matches the one stored in SharedPreferences.
     *
     * @return True if emails match, false otherwise.
     */
    private boolean isUserEmailMatched() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        Log.d("MainActivity", "Email from SharedPreferences: " + sharedPreferences.getString("Email", ""));
        return sharedPreferences.getString("Email", "")
                .equals(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    /**
     * Checks if the user exists in Firestore.
     *
     * @param listener A callback listener to handle the asynchronous result.
     */
    private void isUserExistInFirestore(OnUserExistListener listener) {
        UserHelper userHelper = new UserHelper(1);
        userHelper.isUserExist()
                .addOnSuccessListener(exist -> {
                    Log.d("UserExist", "User exists in Firestore: " + exist + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    listener.onResult(exist);
                })
                .addOnFailureListener(e -> {
                    Log.e("UserExist", "Error checking user existence", e);
                    listener.onResult(false);
                });
    }

    /**
     * Prints the user's information for debugging purposes.
     *
     * @param user The FirebaseUser object representing the currently signed-in user.
     */
    private void printUserInfo(FirebaseUser user) {
        String TAG = "Firebase";
        if (user != null) {
            // Print user info
            Log.d(TAG, "User Display Name: " + user.getDisplayName());
            Log.d(TAG, "User Email: " + user.getEmail());
            Log.d(TAG, "User Photo URL: " + user.getPhotoUrl());
            Log.d(TAG, "User UID: " + user.getUid());
        }
    }

    /**
     * Redirects the user to the HomePage activity.
     */
    private void redirectToHomePage() {
        Intent intent = new Intent(MainActivity.this, HomePage.class);
        startActivity(intent);
    }

    /**
     * Redirects the user to the LoginPage activity.
     */
    private void redirectToLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginPage.class);
        startActivity(intent);
    }

    /**
     * Redirects the user to the WelcomePage activity.
     */
    private void redirectToWelcomePage() {
        Intent intent = new Intent(MainActivity.this, WelcomePage.class);
        startActivity(intent);
    }

    /**
     * A callback interface for handling the asynchronous result of the Firestore user existence check.
     */
    private interface OnUserExistListener {
        /**
         * Called when the user existence check completes.
         *
         * @param exists true if the user exists, false otherwise.
         */
        void onResult(boolean exists);
    }
}