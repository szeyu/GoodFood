package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.hmir.goodfood.utilities.NutritionalRecord;
import com.hmir.goodfood.utilities.UserHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomePage extends AppCompatActivity {
    private TextView tvCalorieIntake;
    private ProgressBar progressCalorieIntake;
    private TextView caloriePercentageText;
    private ImageView exceedImageView;
    private UserHelper userHelper = new UserHelper();
    private BarChart MonthlyBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        tvCalorieIntake = findViewById(R.id.HOMEtv_calorie_intake);
        progressCalorieIntake = findViewById(R.id.HOMEtodayProgressBar);
        exceedImageView = findViewById(R.id.HOMEdizzyface);
        caloriePercentageText = findViewById(R.id.HOMEpercentage_cal);
        MonthlyBarChart = findViewById(R.id.HOMEthisMonthBarChart);
        fetchTodayData();
        fetchThisMonthData();
    }

    private void fetchTodayData() {
        userHelper.fetchAllUserNutritionalRecords(new UserHelper.OnRecordListFetchedCallback() {
            @Override
            public void onRecordListFetched(List<NutritionalRecord> records) {
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                double totalCalories = 0;

                for (NutritionalRecord record : records) {
                    String recordDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(record.getDate_time().toDate());

                    if (recordDate.equals(todayDate)) {
                        totalCalories += record.getCalories();
                    }
                }
                updateProgressBar(totalCalories);
            }
            @Override
            public void onError(Exception e) {
                if (e != null) {
                    // Show the error message in a Toast
                    String errorMessage = e.getMessage();

                    // Show a Toast with the error message (if it's too long, you can shorten it)
                    Toast.makeText(HomePage.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomePage.this, "Unknown error occurred", Toast.LENGTH_LONG).show();
                }

                // Optionally, show a generic failure message to the user
                Toast.makeText(HomePage.this, "Failed to load nutritional data. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchThisMonthData() {
        userHelper.fetchAllUserNutritionalRecords(new UserHelper.OnRecordListFetchedCallback() {
            @Override
            public void onRecordListFetched(List<NutritionalRecord> records) {
                String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());


                float totalProtein = 0, totalCarbs = 0, totalFat = 0, totalSodium = 0, totalCalcium = 0, totalCholesterol = 0, totalMagnesium = 0, totalIron = 0, totalPotassium = 0;
                int daysInMonth = 0;

                for (NutritionalRecord record : records) {
                    String recordMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                            .format(record.getDate_time().toDate());

                    if (recordMonth.equals(currentMonth)) {
                        totalProtein += record.getProtein();
                        totalCarbs += record.getCarbs();
                        totalFat += record.getFat();
                        totalSodium += record.getSodium();
                        totalIron += record.getIron();
                        totalCalcium += record.getCalcium();
                        totalCholesterol += record.getCholesterol();
                        totalMagnesium += record.getMagnesium();
                        totalPotassium += record.getPotassium();

                        // Count number of days in the current month
                        daysInMonth++;
                    }
                }

                if (daysInMonth > 0) {
                    // Update nutrition chart (average for the month)
                    updateNutritionChart(totalProtein, totalCarbs, totalFat, totalSodium, totalIron, totalCalcium, totalCholesterol, totalMagnesium, totalPotassium, daysInMonth);
                } else {
                    Toast.makeText(HomePage.this, "No records found for this month", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle error the same way as in TodayFragment
                if (e != null) {
                    Toast.makeText(HomePage.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomePage.this, "Unknown error occurred", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateProgressBar(double totalCalories) {
        setupProgressBar();

        // Check if totalCalories exceeds 1000 and adjust the progress bar and percentage text accordingly
        int progress;
        String percentageText;

        if (totalCalories > 1000) {
            progress = 100; // Set progress to 100%
            // Display the image when exceeding 100%
            exceedImageView.setVisibility(View.VISIBLE); // Make the image visible
            caloriePercentageText.setVisibility(View.GONE);
        } else {
            progress = (int) ((totalCalories / 1000) * 100); // Calculate the progress percentage
            percentageText = progress + "%"; // Display the actual progress
            caloriePercentageText.setText(percentageText);
            exceedImageView.setVisibility(View.GONE); // Hide the image
        }
        tvCalorieIntake.setText(String.format("Calories: %.0f/1000", totalCalories));
        progressCalorieIntake.setProgress(progress);
    }

    private void setupProgressBar() {
        progressCalorieIntake = findViewById(R.id.HOMEtodayProgressBar);
        caloriePercentageText = findViewById(R.id.HOMEpercentage_cal);
    }

    private void updateNutritionChart(float protein, float carbs, float fat, float sodium, float iron, float calcium, float cholesterol, float magnesium, float potassium, int daysInMonth) {
        Log.d("ThisMonthFragment", "updateNutritionChart called with values: protein=" + protein + ", carbs=" + carbs + ", fat=" + fat);

        if (MonthlyBarChart == null) {
            Log.e("ThisMonthFragment", "Bar chart view is null.");
            return;
        }

        // Calculate averages for each nutrient
        float avgProtein = protein / daysInMonth;
        float avgCarbs = carbs / daysInMonth;
        float avgFat = fat / daysInMonth;
        float avgSodium = sodium / daysInMonth;
        float avgIron = iron / daysInMonth;
        float avgCalcium = calcium / daysInMonth;
        float avgCholesterol = cholesterol / daysInMonth;
        float avgMagnesium = magnesium / daysInMonth;
        float avgPotassium = potassium / daysInMonth;

        // Dummy data for testing
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, avgProtein));
        entries.add(new BarEntry(1, avgCarbs));
        entries.add(new BarEntry(2, avgFat));
        entries.add(new BarEntry(3, avgSodium));
        entries.add(new BarEntry(4, avgIron));
        entries.add(new BarEntry(5, avgCalcium));
        entries.add(new BarEntry(6, avgCholesterol));
        entries.add(new BarEntry(7, avgMagnesium));
        entries.add(new BarEntry(8, avgPotassium));

        // Names for each bar
        final String[] barNames = {
                "Protein", "Carbs", "Fat", "Sodium", "Iron", "Calcium", "Cholesterol", "Magnesium", "Potassium"
        };

        BarDataSet dataSet = new BarDataSet(entries, "Nutrients");
        dataSet.setColor(getResources().getColor(R.color.yellow)); // Use a simple color for now
        BarData barData = new BarData(dataSet);

        MonthlyBarChart.setData(barData);

        // Disable gridlines
        MonthlyBarChart.getAxisLeft().setDrawGridLines(false);
        MonthlyBarChart.getAxisRight().setDrawGridLines(false);
        MonthlyBarChart.getXAxis().setDrawGridLines(false);

        // Disable axis lines
        MonthlyBarChart.getAxisLeft().setDrawAxisLine(false);
        MonthlyBarChart.getAxisRight().setDrawAxisLine(false);
        MonthlyBarChart.getXAxis().setDrawAxisLine(false);

        // Hide X-axis index number
        MonthlyBarChart.getXAxis().setEnabled(false);
        // Add chart value selected listener
        MonthlyBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX(); // Get the index of the clicked bar
                String barName = barNames[index]; // Get the corresponding name from the barNames array

                // Display the selected bar's name (you can use a Toast, Snackbar, or TextView)
                Toast.makeText(HomePage.this, "Selected: " + barName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // Optional: Action to perform when nothing is selected
            }
        });

        // Customize the Y-axis labels (optional)
        MonthlyBarChart.getAxisLeft().setDrawLabels(true);
        MonthlyBarChart.getAxisRight().setEnabled(false); // Hide Y-axis on the right
        MonthlyBarChart.getXAxis().setGranularity(1f); // For proper spacing between bars

        // Set a more modern and clean appearance (optional)
        MonthlyBarChart.getLegend().setEnabled(false); // Disable the legend if not needed
        MonthlyBarChart.setDescription(null);  // Hide description

        // Add animations (optional)
        MonthlyBarChart.animateY(1000);
        //show the values above bars
        dataSet.setDrawValues(true);

        MonthlyBarChart.invalidate(); // Refresh the chart
        Log.d("ThisMonthFragment", "updateNutritionChart completed successfully.");
    }

    public void toSearchPage(View view) {
        Intent intent = new Intent(HomePage.this, SearchActivity.class);
        startActivity(intent);
    }

    public void toTodayStats(View view){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("code", "today");
        startActivity(intent);
    }

    public void toThisMonthStats (View view){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("code", "month");
        startActivity(intent);
    }
}