package com.hmir.goodfood;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hmir.goodfood.utilities.UserHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class welcomePage extends AppCompatActivity {
    private EditText usernameEditText, ageEditText, heightEditText, weightEditText;
    private Button confirmButton;
    private Button halalButton, veganButton, pescatarianButton, customButton, dairyButton, nutsButton, seafoodButton, othersButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.editText_username);
        ageEditText = findViewById(R.id.editText_age);
        heightEditText = findViewById(R.id.editText_height);
        weightEditText = findViewById(R.id.editText_weight);
        confirmButton = findViewById(R.id.button_confirm);

        // Restrict age to integers only
        InputFilter[] integerFilter = new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    // Combine the existing text with the new input
                    StringBuilder newInput = new StringBuilder(dest);
                    newInput.replace(dstart, dend, source.subSequence(start, end).toString());
                    // Check if the input starts with 0
                    if (newInput.length() > 0 && newInput.charAt(0) == '0') {
                        return "";
                    }
                    // Check if input is all digits
                    for (int i = 0; i < newInput.length(); i++) {
                        if (!Character.isDigit(newInput.charAt(i))) {
                            return "";
                        }
                    }
                    // Limit to 3 digits
                    if (newInput.length() > 3) {
                        return "";
                    }
                    // Check if input exceeds 150
                    try {
                        int age = Integer.parseInt(newInput.toString());
                        if (age > 150) {
                            return "";
                        }
                    } catch (NumberFormatException e) {
                        return "";
                    }

                    return null;
                }
        };
        ageEditText.setFilters(integerFilter);
        ageEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Allow decimals for height and weight
        InputFilter[] decimalFilter = new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            String destText = dest.toString();
            String result = destText.substring(0, dstart) + source + destText.substring(dend);
            if (result.matches("\\d*(\\.\\d{0,2})?")) { // Restrict to 2 decimal places
                return null;
            }
            return "";
        }};
        heightEditText.setFilters(decimalFilter);
        weightEditText.setFilters(decimalFilter);
        heightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Initialize diet buttons
        halalButton = findViewById(R.id.button_halal);
        veganButton = findViewById(R.id.button_vegan);
        pescatarianButton = findViewById(R.id.button_pescatarian);
        customButton = findViewById(R.id.button_custom);
        dairyButton = findViewById(R.id.button_dairy);
        nutsButton = findViewById(R.id.button_nuts);
        seafoodButton = findViewById(R.id.button_seafood);
        othersButton = findViewById(R.id.button_others);

        // Store selected diet types
        StringBuilder selectedDietTypes = new StringBuilder();

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

        // Confirm button listener
        confirmButton.setOnClickListener(v -> {
            // Get input data
            String username = usernameEditText.getText().toString();
            String age = ageEditText.getText().toString();
            String height = heightEditText.getText().toString();
            String weight = weightEditText.getText().toString();

            // Validate inputs
            if (username.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                Toast.makeText(welcomePage.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data in Shared Preferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Username", username);
            editor.putString("Age", age);
            editor.putString("Height", height);
            editor.putString("Weight", weight);
            editor.putString("DietTypes", selectedDietTypes.toString());
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

            newUser.addNewUser(newUserMapping);

            // Show confirmation
            Toast.makeText(welcomePage.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to homepage
            Intent intent = new Intent(welcomePage.this, HomePage.class);
            startActivity(intent);
            finish(); // Optional: Close the welcomePage activity
        });
    }

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