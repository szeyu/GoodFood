//package com.example.goodfood;
package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hmir.goodfood.ProfilePage;

public class PrivacyPolicyFragment extends Fragment {

    private ImageButton BtnBackPrivacyPolicy;

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        BtnBackPrivacyPolicy = view.findViewById(R.id.IBBack);
        BtnBackPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilePage.class);
                startActivity(intent);
            }
        });
        return view;
    }

}