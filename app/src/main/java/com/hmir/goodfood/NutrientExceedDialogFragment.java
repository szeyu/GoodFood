package com.hmir.goodfood;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**

 * A DialogFragment that displays information about exceeded nutrient levels.
 * This dialog shows a list of nutrients that have exceeded their recommended daily values,
 * or a congratulatory message if all nutrients are within normal ranges.
 *
 * Usage:
 * <pre>
 * List<String> exceededNutrients = // ... list of exceeded nutrients
 * NutrientExceedDialogFragment dialog = new NutrientExceedDialogFragment(exceededNutrients);
 * dialog.show(getSupportFragmentManager(), "nutrient_dialog");
 * </pre>
 *
 * @see DialogFragment
 * @see AlertDialog
 */
public class NutrientExceedDialogFragment extends DialogFragment {

    private final List<String> exceededNutrients;

    /**
     * Creates a new instance of NutrientExceedDialogFragment.
     *
     * @param exceededNutrients A list of nutrient names that have exceeded their
     *                          recommended levels. Pass an empty list if no nutrients
     *                          have exceeded their limits.
     */
    public NutrientExceedDialogFragment(List<String> exceededNutrients) {
        // Create an unmodifiable view of the list
        this.exceededNutrients = new ArrayList<>(exceededNutrients);
    }

    /**
     * Creates and returns the dialog to be shown.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous
     *                          saved state, this is the state.
     * @return A new Dialog instance to be displayed by the Fragment.
     */
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

