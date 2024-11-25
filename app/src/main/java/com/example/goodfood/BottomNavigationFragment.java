package com.example.goodfood;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationFragment extends Fragment {

    public BottomNavigationFragment() {
        // Required empty public constructor
    }

    public static BottomNavigationFragment newInstance() {
        BottomNavigationFragment fragment = new BottomNavigationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        // handle item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Class<?> targetActivity = null;

            if (item.getItemId() == R.id.nav_home) {
                targetActivity = MainActivity.class;
            } else if (item.getItemId() == R.id.nav_camera) {
                targetActivity = FoodScanner.class;
            } else if (item.getItemId() == R.id.nav_profile) {
                targetActivity = null;
            }

            if(targetActivity != null && getActivity() != null && !getActivity().getClass().equals(targetActivity)){
                Intent intent = new Intent(getActivity(), targetActivity);
                startActivity(intent);
                getActivity().finish();
            }

            return true;
        });

        return view;
    }
}