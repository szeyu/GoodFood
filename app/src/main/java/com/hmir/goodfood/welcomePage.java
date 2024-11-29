package com.hmir.goodfood;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
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
            if (selectedDietTypes.toString().contains(button.getText())) {
                selectedDietTypes.replace(
                        selectedDietTypes.indexOf(button.getText().toString()),
                        selectedDietTypes.indexOf(button.getText().toString()) + button.getText().length() + 1,
                        ""
                );
                button.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                selectedDietTypes.append(button.getText()).append(",");
                button.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
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
        });
    }
}