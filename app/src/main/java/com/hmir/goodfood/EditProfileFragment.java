package com.hmir.goodfood;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hmir.goodfood.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.hmir.goodfood.utilities.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * The EditProfileFragment class provides a user interface for editing the user's profile details.
 * Users can update their username, age, height, weight, and dietary preferences.
 * Changes are saved locally in SharedPreferences and optionally synced to Firebase if internet connectivity is available.
 */
public class EditProfileFragment extends Fragment {

    private TextInputEditText etUsername;
    private TextInputEditText etAge;
    private TextInputEditText etHeight;
    private TextInputEditText etWeight;
    private ImageButton btnSave;

    private SharedPreferences sharedPreferences;

    private String originalUsername;
    private String originalAge;
    private String originalHeight;
    private String originalWeight;
    private String originalDietPreferences;
    private Button halalButton, veganButton, pescatarianButton, customButton, dairyButton, nutsButton, seafoodButton, othersButton;

    private String selectedDietPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", requireActivity().MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize views
        etUsername = view.findViewById(R.id.ETUsername);
        etAge = view.findViewById(R.id.ETAge);
        etHeight = view.findViewById(R.id.ETHeight);
        etWeight = view.findViewById(R.id.ETWeight);
        btnSave = view.findViewById(R.id.IBDone);

        // Restrict age to integers only
        InputFilter[] integerFilter = new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }};
        etAge.setFilters(integerFilter);
        etAge.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Allow decimals for height and weight
        InputFilter[] decimalFilter = new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            String destText = dest.toString();
            String result = destText.substring(0, dstart) + source + destText.substring(dend);
            if (result.matches("\\d*(\\.\\d{0,2})?")) { // Restrict to 2 decimal places
                return null;
            }
            return "";
        }};
        etHeight.setFilters(decimalFilter);
        etWeight.setFilters(decimalFilter);
        etHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Initialize diet buttons
        halalButton = view.findViewById(R.id.button_halal);
        veganButton = view.findViewById(R.id.button_vegan);
        pescatarianButton = view.findViewById(R.id.button_pescatarian);
        customButton = view.findViewById(R.id.button_custom);
        dairyButton = view.findViewById(R.id.button_dairy);
        nutsButton = view.findViewById(R.id.button_nuts);
        seafoodButton = view.findViewById(R.id.button_seafood);
        othersButton = view.findViewById(R.id.button_others);

        // Load original data
        loadOriginalData();

        // Add text change listeners
        addTextWatchers();

        // Set click listeners for diet buttons
        setDietButtonListeners();

        // Save button click listener
        btnSave.setOnClickListener(v -> saveChanges());

        return view;
    }

    // Method to Loads original user profile data from SharedPreferences and populates the input fields.
    private void loadOriginalData() {
        originalUsername = sharedPreferences.getString("Username", "");
        originalAge = sharedPreferences.getString("Age", "");
        originalHeight = sharedPreferences.getString("Height", "");
        originalWeight = sharedPreferences.getString("Weight", "");
        originalDietPreferences = sharedPreferences.getString("DietPreferences", "");

        // Set fields with the original data
        etUsername.setText(originalUsername);
        etAge.setText(originalAge);
        etHeight.setText(originalHeight);
        etWeight.setText(originalWeight);

        // Restore selected diet preference
        selectedDietPreference = originalDietPreferences;

        btnSave.setVisibility(View.GONE); // Hide Save button initially
    }

    // Method to add TextWatchers to input fields to detect changes and toggle the Save button visibility.
    private void addTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                detectChanges();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etUsername.addTextChangedListener(textWatcher);
        etAge.addTextChangedListener(textWatcher);
        etHeight.addTextChangedListener(textWatcher);
        etWeight.addTextChangedListener(textWatcher);
    }

    // Method to set click listeners for dietary preference buttons to update the selected preference.
    private void setDietButtonListeners() {

            View.OnClickListener dietButtonClickListener = v -> {
                int id = v.getId();
                if (id == R.id.button_halal) {
                    selectedDietPreference = "Halal";
                } else if (id == R.id.button_vegan) {
                    selectedDietPreference = "Vegan";
                } else if (id == R.id.button_pescatarian) {
                    selectedDietPreference = "Pescatarian";
                } else if (id == R.id.button_custom) {
                    selectedDietPreference = "Custom";
                } else if (id == R.id.button_dairy) {
                    selectedDietPreference = "Dairy-Free";
                } else if (id == R.id.button_nuts) {
                    selectedDietPreference = "Nut-Free";
                } else if (id == R.id.button_seafood) {
                    selectedDietPreference = "Seafood";
                } else if (id == R.id.button_others) {
                    selectedDietPreference = "Others";
                }
                detectChanges();
        };

        // Attach the listener to all buttons
        halalButton.setOnClickListener(dietButtonClickListener);
        veganButton.setOnClickListener(dietButtonClickListener);
        pescatarianButton.setOnClickListener(dietButtonClickListener);
        customButton.setOnClickListener(dietButtonClickListener);
        dairyButton.setOnClickListener(dietButtonClickListener);
        nutsButton.setOnClickListener(dietButtonClickListener);
        seafoodButton.setOnClickListener(dietButtonClickListener);
        othersButton.setOnClickListener(dietButtonClickListener);
    }

    // Method to detect changes in the input fields or dietary preference buttons and toggles the Save button visibility.
    private void detectChanges() {
        String newUsername = etUsername.getText().toString();
        String newAge = etAge.getText().toString();
        String newHeight = etHeight.getText().toString();
        String newWeight = etWeight.getText().toString();

        if (!newUsername.equals(originalUsername) ||
                !newAge.equals(originalAge) ||
                !newHeight.equals(originalHeight) ||
                !newWeight.equals(originalWeight) ||
                (selectedDietPreference != null && !selectedDietPreference.equals(originalDietPreferences))) {
            btnSave.setVisibility(View.VISIBLE); // Show Save button if changes detected
        } else {
            btnSave.setVisibility(View.GONE); // Hide Save button if no changes
        }
    }

    // Method to save the changes made to the profile either in local storage or firebase
    // depending on the Internet connectivity
    private void saveChanges() {
        // Save updated data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String updatedUsername = etUsername.getText().toString();
        String updatedAge = etAge.getText().toString();
        String updatedHeight = etHeight.getText().toString();
        String updatedWeight = etWeight.getText().toString();

        editor.putString("Username", updatedUsername);
        editor.putString("Age", updatedAge);
        editor.putString("Height", updatedHeight);
        editor.putString("Weight", updatedWeight);
        editor.putString("DietPreferences", selectedDietPreference);
        editor.apply();

        // Firebase sync using UserHelper
        UserHelper userHelper = new UserHelper();
        if (isInternetAvailable()) {
            userHelper.updateUserInfo(
                    updatedUsername,
                    List.of(selectedDietPreference), // Assuming single diet preference; adapt as needed
                    Long.parseLong(updatedAge),
                    Double.parseDouble(updatedHeight),
                    Double.parseDouble(updatedWeight),
                    new ArrayList<>(), // Placeholder for favourite_recipes
                    new ArrayList<>()  // Placeholder for nutritional_records
            );
            Toast.makeText(requireContext(), "Changes saved and synced to Firebase.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Internet unavailable. Changes saved locally.", Toast.LENGTH_SHORT).show();
        }

        // Reload original data for consistency
        loadOriginalData();
    }


    // Method to check whether the emulator / device is connected to the Internet
    // different message will be displayed after the user make changes on his / her profile based on the Internet Connectivity
    private boolean isInternetAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

}
