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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
        InputFilter[] integerFilter = new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }};
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

            // Show confirmation
            Toast.makeText(welcomePage.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to homepage
            Intent intent = new Intent(welcomePage.this, HomePage.class);
            startActivity(intent);
            finish(); // Optional: Close the welcomePage activity
        });
    }
}