package com.hmir.goodfood;

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

import java.util.ArrayList;
import java.util.List;

public class TodayFragment extends Fragment {

    private BarChart todayBarChart;
    private LinearLayout mealHistoryLinearLayout;
    private DatabaseHelper databaseHelper;
    private List<NutritionalRecord> nutritionalRecords = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        todayBarChart = view.findViewById(R.id.todayBarChart);
        mealHistoryLinearLayout = view.findViewById(R.id.todayMealHistoryLinearLayout);

        // Initialize the DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        fetchNutritionalData();

        return view;
    }

    private void fetchNutritionalData() {
        // Retrieve all nutritional records from the database
        nutritionalRecords = databaseHelper.getAllNutritionalRecords();
        if (nutritionalRecords == null) {
            nutritionalRecords = new ArrayList<>();
        }
        updateUI();
    }

    private void updateUI() {
        double caloriesTaken = 0;
        double caloriesSuggested = 2000;   //edit based on correct suggested calories

        // Calculate calories taken
        for (NutritionalRecord record : nutritionalRecords) {
            if ("Calories".equalsIgnoreCase(record.getName())) {
                caloriesTaken = Double.parseDouble(record.getCalories()); // Convert calories to double for calculation
                break;
            }
        }

        // Update progress bar and calorie information
        ProgressBar progressBar = getView().findViewById(R.id.todayProgressBar);
        //TextView percentageText = getView().findViewById(R.id.todayPercentageText);
        TextView calorieText = getView().findViewById(R.id.todayCalorieText);

        int percentage = caloriesSuggested > 0 ? (int) ((caloriesTaken / caloriesSuggested) * 100) : 0;
        progressBar.setProgress(percentage);
        //percentageText.setText(percentage + "%");
        calorieText.setText((int) caloriesTaken + " / " + (int) caloriesSuggested + " cal");

        // Update BarChart
        setUpChart((ArrayList<NutritionalRecord>) nutritionalRecords);
    }

    private void setUpChart(ArrayList<NutritionalRecord> nutritionalRecords) {
        ArrayList<BarEntry> takenEntries = new ArrayList<>();
        ArrayList<BarEntry> suggestedEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // Loop through nutritional records and add data to chart
        for (int i = 0; i < nutritionalRecords.size(); i++) {
            NutritionalRecord record = nutritionalRecords.get(i);

            // For now, we will use calories, protein, carbs, fat as nutrients in the chart
            takenEntries.add(new BarEntry(i, Float.parseFloat(record.getCalories())));
            suggestedEntries.add(new BarEntry(i, Float.parseFloat(record.getCalories()))); // Placeholder for suggested value
            labels.add(record.getName());
        }

        // Create BarDataSets for taken and suggested values
        BarDataSet takenDataSet = new BarDataSet(takenEntries, "Amount Taken");
        BarDataSet suggestedDataSet = new BarDataSet(suggestedEntries, "Suggested Amount");

        // Set colors for the bars
        takenDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.red));
        suggestedDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.orange));

        // Create BarData and set it to the chart
        BarData data = new BarData(takenDataSet, suggestedDataSet);
        data.setBarWidth(0.3f); // Adjust bar width
        todayBarChart.setData(data);

        // Customize the chart
        XAxis xAxis = todayBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        todayBarChart.getDescription().setEnabled(false);
        todayBarChart.setFitBars(true); // Makes bars fit in the chart
        todayBarChart.invalidate(); // Refresh chart
    }
}
