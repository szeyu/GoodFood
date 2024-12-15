package com.hmir.goodfood;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NutritionalDetailFragment extends Fragment {

    private static final String ARG_LABEL = "label";
    private static final String ARG_VALUE = "value";

    public NutritionalDetailFragment() {
        // Required empty public constructor
    }

    public static NutritionalDetailFragment newInstance(String label, String value) {
        NutritionalDetailFragment fragment = new NutritionalDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LABEL, label);
        args.putString(ARG_VALUE, value);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nutritional_detail, container, false);

        TextView labelTextView = view.findViewById(R.id.nutrientLabel);
        TextView valueTextView = view.findViewById(R.id.nutrientValue);

        if(getArguments() != null){
            String label = getArguments().getString(ARG_LABEL);
            String value = getArguments().getString(ARG_VALUE);

            labelTextView.setText(label);
            valueTextView.setText(value);
        }

        return view;
    }
}