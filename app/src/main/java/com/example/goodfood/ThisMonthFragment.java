package com.example.goodfood;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class ThisMonthFragment extends Fragment {

    private BarChart thisMonthBarChart;

    private LinearLayout popularMealsLinearLayout;

    private ImageButton nutrientIntakeInfoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_this_month, container, false);

        thisMonthBarChart = view.findViewById(R.id.thisMonthBarChart);
        popularMealsLinearLayout = view.findViewById(R.id.thisMonthPopularMealsLinearLayout);
        nutrientIntakeInfoButton = view.findViewById(R.id.thisMonthNutrientIntakeInfoButton);

        // listener for nutrient intake info button
        nutrientIntakeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        // Calculate average calories
        int totalCalories = 28500;
        int caloriesSuggested = 30000;
        int average = totalCalories / 30;

        int percentage = (int) ((double) totalCalories / caloriesSuggested * 100);

        // Update ProgressBar and TextViews
        ProgressBar progressBar = view.findViewById(R.id.thisMonthProgressBar);
        TextView avgCalText = view.findViewById(R.id.thisMonthAvgCalText);
        TextView calorieText = view.findViewById(R.id.thisMonthCalorieText);

        progressBar.setProgress(percentage);
        avgCalText.setText(Integer.toString(average));
        calorieText.setText(totalCalories + " / " + caloriesSuggested + " cal");

        // Create an ArrayList with simulated nutrients data
        ArrayList<Nutrient> nutrients = new ArrayList<>();
        nutrients.add(new Nutrient("Cholesterol", 80, 100));
        nutrients.add(new Nutrient("Carbohydrates", 180, 150));
        nutrients.add(new Nutrient("Protein", 120, 100));
        nutrients.add(new Nutrient("Fat", 90, 100));
        nutrients.add(new Nutrient("Fibre", 25, 30));
        nutrients.add(new Nutrient("Vitamins", 60, 50));
        nutrients.add(new Nutrient("Minerals", 50, 60));
        nutrients.add(new Nutrient("Water", 200, 250));

        // Create an ArrayList with simulated meals data
        ArrayList<Meal> meals = new ArrayList<>();
        meals.add(new Meal("Pizza", 300));
        meals.add(new Meal("Burger", 500));
        meals.add(new Meal("Salad", 150));

        // Populate the meal container dynamically
        LayoutInflater inflaterMeal = LayoutInflater.from(getContext());
        for (Meal meal : meals) {
            // Inflate each meal item layout
            View mealView = inflaterMeal.inflate(R.layout.meal_item, popularMealsLinearLayout, false);

            // Set the meal's name and calories dynamically
            TextView mealName = mealView.findViewById(R.id.meal_name);
            TextView mealCalories = mealView.findViewById(R.id.meal_calories);

            mealName.setText(meal.getName());
            mealCalories.setText(meal.getCalories() + " kcal");

            // Add the inflated view to the meal container
            popularMealsLinearLayout.addView(mealView);
        }

        // Set up the chart
        setUpChart(nutrients);

        return view;
    }

    private void showAlertDialog () {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setTitle("Chart Colour Indicator")
                .setMessage(String.format("For each bar groups:\n%s: Your Recorded Intake\n - %s: Under suggested amount\n" +
                                " - %s: Over suggested amount\n\n%s: Recommended Intake\n - %s: Suggested amount\n",
                        "Left Bar","Yellow","Red","Right Bar","Orange"))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void setUpChart(ArrayList<Nutrient> nutrients) {
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.poppins_light);

        ArrayList<BarEntry> takenEntries = new ArrayList<>();
        ArrayList<BarEntry> suggestedEntries = new ArrayList<>();

        // Prepare the data for the chart
        for (int i = 0; i < nutrients.size(); i++) {
            Nutrient nutrient = nutrients.get(i);
            takenEntries.add(new BarEntry(i, nutrient.amountTaken));
            suggestedEntries.add(new BarEntry(i, nutrient.suggestedAmount));
        }

        // Create BarDataSets
        BarDataSet takenDataSet = new BarDataSet(takenEntries, "Amount Taken");
        BarDataSet suggestedDataSet = new BarDataSet(suggestedEntries, "Suggested Amount");

        // Set colors and value visibility
        suggestedDataSet.setColor(getResources().getColor(R.color.orange));
        suggestedDataSet.setDrawValues(true);

        // Color Logic - Handle the color change for both bars
        ArrayList<Integer> takenColors = new ArrayList<>();

        // Loop through each nutrient to set specific colors based on conditions
        for (int i = 0; i < nutrients.size(); i++) {
            Nutrient nutrient = nutrients.get(i);
            if (nutrient.amountTaken < nutrient.suggestedAmount) {
                takenColors.add(ContextCompat.getColor(getContext(), R.color.yellow)); // Amount Taken is Yellow
            } else {
                takenColors.add(ContextCompat.getColor(getContext(), R.color.red)); // Amount Taken is Red
            }
        }

        takenDataSet.setColors(takenColors);
        takenDataSet.setDrawValues(true);

        // Set the Value Formatter to display the actual value on top of the bars
        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value); // Display values as integers
            }
        };
        takenDataSet.setValueFormatter(valueFormatter);
        suggestedDataSet.setValueFormatter(valueFormatter);
        takenDataSet.setValueTypeface(customFont);
        suggestedDataSet.setValueTypeface(customFont);


        // X-axis configuration
        float barWidth = 0.2f;
        float groupSpace = 0.5f;
        float barSpace = 0.05f;
        String [] nutrientLabels = new String [] {"Cholesterol", "Carbohydrates", "Protein", "Fat", "Fibre", "Vitamins", "Minerals", "Water"};

        BarData data = new BarData(takenDataSet, suggestedDataSet);
        data.setBarWidth(barWidth);
        thisMonthBarChart.setData(data);
        thisMonthBarChart.setVisibleXRangeMaximum(3);

        XAxis xAxis = thisMonthBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(0 + thisMonthBarChart.getBarData().getGroupWidth(groupSpace, barSpace) * nutrients.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(nutrientLabels));
        xAxis.setDrawGridLines(false);
        xAxis.setTypeface(customFont);

        thisMonthBarChart.groupBars(0, groupSpace, barSpace);

        // Y-Axis customization
        YAxis leftAxis = thisMonthBarChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setTypeface(customFont);
        thisMonthBarChart.getAxisRight().setEnabled(false);
        thisMonthBarChart.getLegend().setEnabled(false);

        thisMonthBarChart.setDragEnabled(true);
        thisMonthBarChart.setExtraRightOffset(12f);
        thisMonthBarChart.setExtraLeftOffset(12f);
        thisMonthBarChart.setExtraBottomOffset(20f);
        thisMonthBarChart.getDescription().setEnabled(false);

        RoundedBarChart render = new RoundedBarChart(thisMonthBarChart, thisMonthBarChart.getAnimator(), thisMonthBarChart.getViewPortHandler());
        render.setmRadius(20);
        thisMonthBarChart.setRenderer(render);
        thisMonthBarChart.invalidate();
    }
}