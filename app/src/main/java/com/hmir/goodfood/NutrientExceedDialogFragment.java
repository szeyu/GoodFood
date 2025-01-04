package com.hmir.goodfood;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class NutrientExceedDialogFragment extends DialogFragment {

    private List<String> exceededNutrients;

    public NutrientExceedDialogFragment(List<String> exceededNutrients) {
        this.exceededNutrients = exceededNutrients;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exceeded Nutrients");

        if (exceededNutrients.isEmpty()) {
            builder.setMessage("Congratulations! All nutrients is in normal range");
        } else {
            StringBuilder message = new StringBuilder("The following nutrients exceeded recommended levels:\n\n");
            for (String nutrient : exceededNutrients) {
                message.append("- ").append(nutrient).append("\n");
            }
            builder.setMessage(message.toString());
        }

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }
}

