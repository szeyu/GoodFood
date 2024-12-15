package com.hmir.goodfood;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment that displays the nutritional details of a specific nutrient.
 *
 * This fragment is designed to show a label (e.g., "Protein") and its associated value
 * (e.g., "20g"). It is created using the {@link #newInstance(String, String)} factory method
 * by passing the label and value as arguments.
 */
public class NutritionalDetailFragment extends Fragment {

    /**
     * The key for the label argument passed to the fragment.
     */
    private static final String ARG_LABEL = "label";

    /**
     * The key for the value argument passed to the fragment.
     */
    private static final String ARG_VALUE = "value";

    /**
     * Default public constructor required for fragment subclasses.
     */
    public NutritionalDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of {@link NutritionalDetailFragment} with the specified label and value.
     *
     * @param label The label of the nutrient (e.g., "Protein").
     * @param value The value of the nutrient (e.g., "20g").
     * @return A new instance of {@code NutritionalDetailFragment}.
     */
    public static NutritionalDetailFragment newInstance(String label, String value) {
        NutritionalDetailFragment fragment = new NutritionalDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LABEL, label);
        args.putString(ARG_VALUE, value);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater  The {@link LayoutInflater} object that can be used to inflate
     *                  any views in the fragment.
     * @param container The parent {@link ViewGroup} that this fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The {@link View} for the fragment's UI, or {@code null}.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nutritional_detail, container, false);

        // Get references to the TextViews for the label and value
        TextView labelTextView = view.findViewById(R.id.nutrientLabel);
        TextView valueTextView = view.findViewById(R.id.nutrientValue);

        // Retrieve arguments and set the label and value if they are provided
        if(getArguments() != null){
            String label = getArguments().getString(ARG_LABEL);
            String value = getArguments().getString(ARG_VALUE);

            labelTextView.setText(label);
            valueTextView.setText(value);
        }

        return view;
    }
}