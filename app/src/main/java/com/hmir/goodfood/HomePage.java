package com.hmir.goodfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

/**
 * HomePage Activity - Main dashboard of the GoodFood application.
 * Displays user's nutritional information, daily calorie intake, and monthly statistics.
 */
public class HomePage extends AppCompatActivity {
    private static final String TAG = "HomePage";
    private static final int MAX_DAILY_CALORIES = 1000;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String MONTH_FORMAT = "yyyy-MM";
    private static final String SHARED_PREFS_NAME = "UserPreferences";

    // UI Components
    private TextView tvCalorieIntake;
    private ProgressBar progressCalorieIntake;
    private TextView caloriePercentageText;
    private ImageView exceedImageView;
    private BarChart monthlyBarChart;
    private TextView tvUsername;
    private ImageView ivProfile;

    // Data Helper
    private final UserHelper userHelper = new UserHelper();

    // Nutrient Names
    private final String[] nutrientNames = {
            "Protein", "Carbs", "Fat", "Sodium", "Iron",
            "Calcium", "Cholesterol", "Magnesium", "Potassium"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        initializeViews();
        setupUserProfile();
        loadNutritionalData();
    }

    /**
     * Initializes all UI components
     */
    private void initializeViews() {
        tvUsername = findViewById(R.id.TVNameHomePage);
        tvCalorieIntake = findViewById(R.id.HOMEtv_calorie_intake);
        progressCalorieIntake = findViewById(R.id.HOMEtodayProgressBar);
        exceedImageView = findViewById(R.id.HOMEdizzyface);
        caloriePercentageText = findViewById(R.id.HOMEpercentage_cal);
        monthlyBarChart = findViewById(R.id.HOMEthisMonthBarChart);
        ivProfile = findViewById(R.id.IVProfile);
    }

    /**
     * Sets up user profile from SharedPreferences
     */
    private void setupUserProfile() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String username = prefs.getString("Username", "Guest");
        tvUsername.setText(username);
    }

    /**
     * Loads both daily and monthly nutritional data
     */
    private void loadNutritionalData() {
        fetchTodayData();
        fetchThisMonthData();
    }

    /**
     * Fetches today's nutritional data
     */
    private void fetchTodayData() {
        userHelper.fetchAllUserNutritionalRecords(new UserHelper.OnRecordListFetchedCallback() {
            @Override
            public void onRecordListFetched(List<NutritionalRecord> records) {
                processTodayData(records);
            }

            @Override
            public void onError(Exception e) {
                handleError(e, "Error loading today's data");
            }
        });
    }

    /**
     * Processes today's nutritional records
     */
    private void processTodayData(List<NutritionalRecord> records) {
        String todayDate = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                .format(new Date());

        double totalCalories = records.stream()
                .filter(record -> {
                    String recordDate = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                            .format(record.getDate_time().toDate());
                    return recordDate.equals(todayDate);
                })
                .mapToDouble(NutritionalRecord::getCalories)
                .sum();

        updateProgressBar(totalCalories);
    }

    /**
     * Updates the calorie progress bar and related UI elements
     */
    private void updateProgressBar(double totalCalories) {
        int progress = calculateProgress(totalCalories);
        updateCalorieDisplay(totalCalories, progress);
    }

    private int calculateProgress(double totalCalories) {
        return Math.min((int) ((totalCalories / MAX_DAILY_CALORIES) * 100), 100);
    }

    private void updateCalorieDisplay(double totalCalories, int progress) {
        tvCalorieIntake.setText(String.format(Locale.getDefault(),
                "Calories: %.0f/%d", totalCalories, MAX_DAILY_CALORIES));
        progressCalorieIntake.setProgress(progress);

        if (totalCalories > MAX_DAILY_CALORIES) {
            exceedImageView.setVisibility(View.VISIBLE);
            caloriePercentageText.setVisibility(View.GONE);
        } else {
            exceedImageView.setVisibility(View.GONE);
            caloriePercentageText.setText(progress + "%");
            caloriePercentageText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Fetches monthly nutritional data
     */
    private void fetchThisMonthData() {
        userHelper.fetchAllUserNutritionalRecords(new UserHelper.OnRecordListFetchedCallback() {
            @Override
            public void onRecordListFetched(List<NutritionalRecord> records) {
                processMonthlyData(records);
            }

            @Override
            public void onError(Exception e) {
                handleError(e, "Error loading monthly data");
            }
        });
    }

    /**
     * Processes monthly nutritional records
     */
    private void processMonthlyData(List<NutritionalRecord> records) {
        String currentMonth = new SimpleDateFormat(MONTH_FORMAT, Locale.getDefault())
                .format(new Date());

        MonthlyNutrients nutrients = calculateMonthlyNutrients(records, currentMonth);

        if (nutrients.daysInMonth > 0) {
            updateNutritionChart(nutrients);
        } else {
            Toast.makeText(this, "No records found for this month", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Data class to hold monthly nutrient totals
     */
    private static class MonthlyNutrients {
        float protein;
        float carbs;
        float fat;
        float sodium;
        float iron;
        float calcium;
        float cholesterol;
        float magnesium;
        float potassium;
        int daysInMonth;
    }

    private MonthlyNutrients calculateMonthlyNutrients(List<NutritionalRecord> records,
                                                       String currentMonth) {
        MonthlyNutrients nutrients = new MonthlyNutrients();

        records.stream()
                .filter(record -> {
                    String recordMonth = new SimpleDateFormat(MONTH_FORMAT, Locale.getDefault())
                            .format(record.getDate_time().toDate());
                    return recordMonth.equals(currentMonth);
                })
                .forEach(record -> {
                    nutrients.protein += record.getProtein();
                    nutrients.carbs += record.getCarbs();
                    nutrients.fat += record.getFat();
                    nutrients.sodium += record.getSodium();
                    nutrients.iron += record.getIron();
                    nutrients.calcium += record.getCalcium();
                    nutrients.cholesterol += record.getCholesterol();
                    nutrients.magnesium += record.getMagnesium();
                    nutrients.potassium += record.getPotassium();
                    nutrients.daysInMonth++;
                });

        return nutrients;
    }

    /**
     * Updates the nutrition chart with monthly averages
     */
    private void updateNutritionChart(MonthlyNutrients nutrients) {
        if (monthlyBarChart == null) {
            Log.e(TAG, "Bar chart view is null.");
            return;
        }

        ArrayList<BarEntry> entries = createBarEntries(nutrients);
        setupBarChart(entries);
    }

    private ArrayList<BarEntry> createBarEntries(MonthlyNutrients nutrients) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        float[] values = {
                nutrients.protein / nutrients.daysInMonth,
                nutrients.carbs / nutrients.daysInMonth,
                nutrients.fat / nutrients.daysInMonth,
                nutrients.sodium / nutrients.daysInMonth,
                nutrients.iron / nutrients.daysInMonth,
                nutrients.calcium / nutrients.daysInMonth,
                nutrients.cholesterol / nutrients.daysInMonth,
                nutrients.magnesium / nutrients.daysInMonth,
                nutrients.potassium / nutrients.daysInMonth
        };

        for (int i = 0; i < values.length; i++) {
            entries.add(new BarEntry(i, values[i]));
        }

        return entries;
    }

    private void setupBarChart(ArrayList<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Nutrients");
        dataSet.setColor(getResources().getColor(R.color.yellow));
        dataSet.setDrawValues(true);

        BarData barData = new BarData(dataSet);
        monthlyBarChart.setData(barData);
        configureBarChartAppearance();
        setupBarChartListener();
    }

    private void configureBarChartAppearance() {
        monthlyBarChart.getAxisLeft().setDrawGridLines(false);
        monthlyBarChart.getAxisRight().setDrawGridLines(false);
        monthlyBarChart.getXAxis().setDrawGridLines(false);
        monthlyBarChart.getAxisLeft().setDrawAxisLine(false);
        monthlyBarChart.getAxisRight().setDrawAxisLine(false);
        monthlyBarChart.getXAxis().setDrawAxisLine(false);
        monthlyBarChart.getXAxis().setEnabled(false);
        monthlyBarChart.getAxisLeft().setDrawLabels(true);
        monthlyBarChart.getAxisRight().setEnabled(false);
        monthlyBarChart.getXAxis().setGranularity(1f);
        monthlyBarChart.getLegend().setEnabled(false);
        monthlyBarChart.setDescription(null);
        monthlyBarChart.animateY(1000);
    }

    private void setupBarChartListener() {
        monthlyBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                Toast.makeText(HomePage.this,
                        "Selected: " + nutrientNames[index],
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // Optional implementation
            }
        });
    }

    /**
     * Handles navigation and error events
     */
    public void toSearchPage(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void toTodayStats(View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("code", "today");
        startActivity(intent);
    }

    public void toThisMonthStats(View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("code", "month");
        startActivity(intent);
    }

    public void refreshProfileData() {
        setupUserProfile();
    }

    private void handleError(Exception e, String message) {
        String errorMessage = e != null ? e.getMessage() : "Unknown error occurred";
        Log.e(TAG, message + ": " + errorMessage, e);
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
    }
}