package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

public class EditProfileFragment extends Fragment {

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_edit_profile, container, false);

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Get references to the EditText (ETUsername) and ImageButton (IBDone)
        EditText ETUsername = view.findViewById(R.id.ETUsername);
        ImageButton IBDone = view.findViewById(R.id.IBDone);

        // Set a click listener for the done button
        IBDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ETUsername = view.findViewById(R.id.ETUsername);
                String newUsername = ETUsername.getText().toString();



                // Create an intent to navigate to ProfilePage
                Intent intent = new Intent(getActivity(), ProfilePage.class);

                // Pass the new username to ProfilePage
                intent.putExtra("username", newUsername);
                startActivity(intent);

                /*
                // Get the new username from the EditText
                String newUsername = ETUsername.getText().toString();

                // Check if the username is not empty
                if (!newUsername.isEmpty()) {
                    // Pass the new username to ProfilePage activity
                    Intent intent = new Intent(getActivity(), ProfilePage.class);
                    intent.putExtra("newUsername", newUsername); // Pass the username to ProfilePage
                    startActivity(intent);
                } */
            }
        });

        return view;
    }


}