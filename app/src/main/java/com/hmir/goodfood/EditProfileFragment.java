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


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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