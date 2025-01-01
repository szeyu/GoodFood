package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hmir.goodfood.utilities.UserHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The WelcomePage class is an activity that collects user details such as username, age,
 * height, weight, and dietary preferences. The data is saved to SharedPreferences and
 * uploaded to Firestore for further use.
 */
public class WelcomePage extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText ageEditText;
    private EditText heightEditText;
    private EditText weightEditText;

    private Button confirmButton;
    private Button halalButton;
    private Button veganButton;
    private Button pescatarianButton;
    private Button customButton;
    private Button dairyButton;
    private Button nutsButton;
    private Button seafoodButton;
    private Button othersButton;

    private StringBuilder selectedDietTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        // Store selected diet types
        selectedDietTypes = new StringBuilder();

        initializeUIElements();
        setupInputFilters();
        setupDietButtons();
        setupConfirmButton();
    }

    /**
     * Sets up the functionality for the confirm button.
     * Validates input fields, saves the data, and navigates to the HomePage activity.
     */
    private void setupConfirmButton() {
        confirmButton.setOnClickListener(v -> {
            // Get input data and also email
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String username = usernameEditText.getText().toString();
            String age = ageEditText.getText().toString();
            String height = heightEditText.getText().toString();
            String weight = weightEditText.getText().toString();

            // Check email
            if (email == null)
                Log.e("WelcomePage", "While saving data in SharedPreference, email is null");

            // Validate inputs
            if (username.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                Toast.makeText(WelcomePage.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            saveUserData(email, username, age, height, weight);
            navigateToHomePage();
        });
    }

    /**
     * Navigates the user to the HomePage activity after successful data submission.
     */
    private void navigateToHomePage() {
        Intent intent = new Intent(WelcomePage.this, HomePage.class);
        startActivity(intent);
        finish(); // Close the welcomePage activity
    }

    /**
     * Saves user data in SharedPreferences and uploads it to Firestore.
     *
     * @param email the email of the current user.
     * @param username the username entered by the user.
     * @param age the age of the user.
     * @param height the height of the user.
     * @param weight the weight of the user.
     */
    private void saveUserData(String email, String username, String age, String height, String weight) {
        // Save data in Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Email", email);
        editor.putString("Username", username);
        editor.putString("Age", age);
        editor.putString("Height", height);
        editor.putString("Weight", weight);
        editor.putString("DietTypes", selectedDietTypes.toString());
        //pfp
        editor.putString("ProfilePicUri",null);
        //

        editor.apply();

        // Save data to FireStore
        UserHelper newUser = new UserHelper(1);
        List<String> health_labels = convertHealthLabelsToList(selectedDietTypes);
        Map<String, Object> newUserMapping = new HashMap<>();
        newUserMapping.put("username", username);
        newUserMapping.put("age", Long.parseLong(age));
        newUserMapping.put("height", Double.parseDouble(height));
        newUserMapping.put("weight", Double.parseDouble(weight));
        newUserMapping.put("health_labels", health_labels);
        newUserMapping.put("favourite_recipes", null);
        newUserMapping.put("nutritional_records", null);
        //pfp
        newUserMapping.put("ProfilePicUri",null);
        //
        newUser.addNewUser(newUserMapping);

        // Show confirmation
        Toast.makeText(WelcomePage.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets up the functionality for dietary preference buttons.
     * Tracks selected diet types and manages the state of each button.
     */
    private void setupDietButtons() {
        View.OnClickListener dietButtonListener = view -> {
            Button button = (Button) view;
            // Toggle selection state
            button.setSelected(!button.isSelected());

            // Manage selectedDietTypes
            String buttonText = button.getText().toString();
            if (button.isSelected()) {
                selectedDietTypes.append(buttonText).append(",");
            } else {
                int startIndex = selectedDietTypes.indexOf(buttonText);
                if (startIndex != -1) {
                    selectedDietTypes.delete(startIndex, startIndex + buttonText.length() + 1); // +1 for the comma
                }
            }
        };

        halalButton.setOnClickListener(dietButtonListener);
        veganButton.setOnClickListener(dietButtonListener);
        pescatarianButton.setOnClickListener(dietButtonListener);
        customButton.setOnClickListener(dietButtonListener);
        dairyButton.setOnClickListener(dietButtonListener);
        nutsButton.setOnClickListener(dietButtonListener);
        seafoodButton.setOnClickListener(dietButtonListener);
        othersButton.setOnClickListener(dietButtonListener);
    }

    /**
     * Sets up input filters for age, height, and weight fields to ensure valid input.
     */
    private void setupInputFilters() {
        // Restrict age to integers only
        InputFilter[] integerFilter = new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    // Combine the existing text with the new input
                    StringBuilder newInput = new StringBuilder(dest);
                    newInput.replace(dstart, dend, source.subSequence(start, end).toString());
                    // Check if the input starts with 0
                    if (newInput.length() > 0 && newInput.charAt(0) == '0') return "";

                    // Check if input is all digits
                    for (int i = 0; i < newInput.length(); i++) {
                        if (!Character.isDigit(newInput.charAt(i))) return "";
                    }
                    // Limit to 3 digits
                    if (newInput.length() > 3) return "";

                    // Check if input exceeds 150
                    try {
                        int age = Integer.parseInt(newInput.toString());
                        if (age > 150) return "";
                    } catch (NumberFormatException e) {
                        return "";
                    }

                    return null;
                }
        };

        // Allow decimals for height and weight
        InputFilter[] decimalFilter = new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    String destText = dest.toString();
                    String result = destText.substring(0, dstart) + source + destText.substring(dend);

                    // Restrict to 2 decimal places
                    return result.matches("\\d*(\\.\\d{0,2})?") ? null : "";
                }};

        ageEditText.setFilters(integerFilter);
        heightEditText.setFilters(decimalFilter);
        weightEditText.setFilters(decimalFilter);

        ageEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        heightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    /**
     * Initializes UI elements such as EditTexts and Buttons by finding their references in the layout.
     */
    private void initializeUIElements() {
        usernameEditText = findViewById(R.id.editText_username);
        ageEditText = findViewById(R.id.editText_age);
        heightEditText = findViewById(R.id.editText_height);
        weightEditText = findViewById(R.id.editText_weight);
        confirmButton = findViewById(R.id.button_confirm);

        halalButton = findViewById(R.id.button_halal);
        veganButton = findViewById(R.id.button_vegan);
        pescatarianButton = findViewById(R.id.button_pescatarian);
        customButton = findViewById(R.id.button_custom);
        dairyButton = findViewById(R.id.button_dairy);
        nutsButton = findViewById(R.id.button_nuts);
        seafoodButton = findViewById(R.id.button_seafood);
        othersButton = findViewById(R.id.button_others);
    }

    /**
     * Converts a StringBuilder containing comma-separated health labels into a List of Strings.
     *
     * @param sb the StringBuilder containing health labels, separated by commas.
     *            Example: "Vegan,Gluten-Free,Low-Carb".
     * @return a List of health labels as Strings. If the StringBuilder is empty or null, returns an empty List.
     */
    private static List<String> convertHealthLabelsToList(StringBuilder sb) {
        // Check if StringBuilder is empty or null
        List<String> list = new ArrayList<>();
        if (sb == null || sb.length() == 0) {
            return list;
        }

        // Convert StringBuilder to String and split by ','
        String str = sb.toString();
        String[] health_labels = str.split(",");

        // Convert the array to a List
        return new ArrayList<>(Arrays.asList(health_labels));
    }
}