//package com.example.goodfood;
package com.hmir.goodfood;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    private Button BtnPermissions;
    private Button BtnReport;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_settings, container, false);


        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        BtnPermissions = view.findViewById(R.id.BtnPermissions);
        BtnReport = view.findViewById(R.id.BtnReport);
        //IBBackSettings = view.findViewById(R.id.IBBackSettings);

        // listener for nutrient intake info button
        BtnPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPermissionsDialog();
            }
        });

        BtnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportProblemDialog();
            }
        });
        /*
        IBBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilePage.class);
                startActivity(intent);
            }
        }); */

        return view;

    }

    private void showPermissionsDialog () {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setTitle("App Permissions")
                .setMessage(String.format("\nYou have agreed with the permission of GoodFood to access the functionalities below:\n\nCamera" +
                                "\nPhotos and videos\nCalendar\nLocation\nNotifications\n\nThis is to ensure the usability of the app functions."))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
    private void showReportProblemDialog() {
        // Create an EditText programmatically
        EditText input = new EditText(requireContext());
        input.setHint("Describe your issue here...");
        input.setPadding(50, 180, 50, 180);

        // Build the AlertDialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setTitle("Report a Problem")
                .setView(input) // Set the EditText as the view
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the user input from the EditText
                        String problemDescription = input.getText().toString().trim();

                        // Handle the input (e.g., send to server or log)
                        submitProblem(problemDescription);

                        dialog.dismiss(); // Close the dialog
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Close the dialog without action
                    }
                });

        // Create and show the dialog
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    // Example method to handle submission
    private void submitProblem(String description) {
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please describe the issue before submitting.", Toast.LENGTH_SHORT).show();
        } else {
            // Example: Log or send the description
            Toast.makeText(requireContext(), "Problem reported. Thank you for your feedback!", Toast.LENGTH_SHORT).show();
        }
    }




}