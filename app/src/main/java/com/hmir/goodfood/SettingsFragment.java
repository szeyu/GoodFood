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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button BtnPermissions, BtnReport;
    private ImageButton IBBackSettings;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_settings, container, false);


        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        BtnPermissions = view.findViewById(R.id.BtnPermissions);
        BtnReport = view.findViewById(R.id.BtnReport);
        IBBackSettings = view.findViewById(R.id.IBBackSettings);

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

        IBBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilePage.class);
                startActivity(intent);
            }
        });

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