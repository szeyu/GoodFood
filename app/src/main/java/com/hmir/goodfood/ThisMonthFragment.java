package com.hmir.goodfood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
 * Fragment that displays the user's nutritional information for the current month.
 * Provides monthly averages and trends of nutritional data.
 *
 * <p>Features:
 * <ul>
 *     <li>Monthly calorie tracking with averages</li>
 *     <li>Nutritional trends visualization</li>
 *     <li>Monthly meal history gallery</li>
 *     <li>Statistical analysis of nutritional intake</li>
 * </ul>
 */
public class ThisMonthFragment extends Fragment {

    private BarChart MonthlyBarChart;
    private ProgressBar MonthlyprogressCalorieIntake;
    private RecyclerView rvMonthlyMealsHistory;
    private TextView MonthlyAvgcalorie;
    private ImageButton MonthlynutrientIntakeInfoButton;
    private TextView tvMonthlyCalorieIntake;
    private ImageView exceedMonthlyCalorieImg;
    private TextView avgtextview;
    private UserHelper userHelper = new UserHelper();
    private List<NutritionalRecord> records = new ArrayList<>(); // Store fetched records

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_this_month, container, false);

        tvMonthlyCalorieIntake = view.findViewById(R.id.tv_Monthly_calorie_intake);
        MonthlyprogressCalorieIntake = view.findViewById(R.id.thisMonthProgressBar);
        MonthlyBarChart = view.findViewById(R.id.HOMEthisMonthBarChart);
        rvMonthlyMealsHistory = view.findViewById(R.id.thisMonth_rv_meals_history);
        MonthlyAvgcalorie = view.findViewById(R.id.thisMonthavg_cal);
        exceedMonthlyCalorieImg = view.findViewById(R.id.exceedMonthCalorie);
        rvMonthlyMealsHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        avgtextview = view.findViewById(R.id.avgtext);
        MonthlynutrientIntakeInfoButton = view.findViewById(R.id.thisMonthNutrientIntakeInfoButton);
        fetchThisMonthData();

        return view;
    }

    /**
     * Fetches and processes nutritional records for the current month.
     * Calculates monthly averages and updates the UI accordingly.
     *
     * <p>This method:
     * <ul>
     *     <li>Retrieves all nutritional records</li>
     *     <li>Filters for current month's records</li>
     *     <li>Calculates monthly totals and averages</li>
     *     <li>Updates UI components with the processed data</li>
     * </ul>
     */
    private void fetchThisMonthData() {
        userHelper.fetchAllUserNutritionalRecords(new UserHelper.OnRecordListFetchedCallback() {
            @Override
            public void onRecordListFetched(List<NutritionalRecord> records) {
                ThisMonthFragment.this.records = records;
                String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

                double totalCalories = 0;
                float totalProtein = 0;
                float totalCarbs = 0;
                float totalFat = 0;
                float totalSodium = 0;
                float totalCalcium = 0;
                float totalCholesterol = 0;
                float totalMagnesium = 0;
                float totalIron = 0;
                float totalPotassium = 0;
                List<String> mealImages = new ArrayList<>();
                int daysInMonth = 0;

                for (NutritionalRecord record : records) {
                    String recordMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                            .format(record.getDate_time().toDate());

                    if (recordMonth.equals(currentMonth)) {
                        totalCalories += record.getCalories();
                        totalProtein += record.getProtein();
                        totalCarbs += record.getCarbs();
                        totalFat += record.getFat();
                        totalSodium += record.getSodium();
                        totalIron += record.getIron();
                        totalCalcium += record.getCalcium();
                        totalCholesterol += record.getCholesterol();
                        totalMagnesium += record.getMagnesium();
                        totalPotassium += record.getPotassium();

                        // Handle image field safely
                        if (record.getImage() != null) {
                            Uri imageUri = Uri.parse(record.getImage());
                            mealImages.add(imageUri.toString());
                        }

                        // Count number of days in the current month
                        daysInMonth++;
                    }
                }
                // Define recommended daily allowances (RDAs)
                float rdaProtein = 50f; // Example values
                float rdaCarbs = 300f;
                float rdaFat = 70f;
                float rdaSodium = 2300f;
                float rdaCalcium = 1000f;
                float rdaCholesterol = 300f;
                float rdaMagnesium = 400f;
                float rdaIron = 18f;
                float rdaPotassium = 3500f;

                // Check for exceeded nutrients
                List<String> exceededNutrients = new ArrayList<>();
                if (totalProtein > rdaProtein) exceededNutrients.add("Protein");
                if (totalCarbs > rdaCarbs) exceededNutrients.add("Carbohydrates");
                if (totalFat > rdaFat) exceededNutrients.add("Fat");
                if (totalSodium > rdaSodium) exceededNutrients.add("Sodium");
                if (totalCalcium > rdaCalcium) exceededNutrients.add("Calcium");
                if (totalCholesterol > rdaCholesterol) exceededNutrients.add("Cholesterol");
                if (totalMagnesium > rdaMagnesium) exceededNutrients.add("Magnesium");
                if (totalIron > rdaIron) exceededNutrients.add("Iron");
                if (totalPotassium > rdaPotassium) exceededNutrients.add("Potassium");
                MonthlynutrientIntakeInfoButton.setOnClickListener(v -> showNutrientExceedDialog(exceededNutrients));

                if (daysInMonth > 0) {
                    // Update calorie intake (average for the month)
                    updateCalorieIntake(totalCalories, daysInMonth);
                    // Update nutrition chart (average for the month)
                    updateNutritionChart(totalProtein, totalCarbs, totalFat, totalSodium, totalIron, totalCalcium, totalCholesterol, totalMagnesium, totalPotassium, daysInMonth);
                    // Update meals history
                    updateMealsHistory(mealImages);
                } else {
                    Toast.makeText(getContext(), "No records found for this month", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle error the same way as in TodayFragment
                if (e != null) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unknown error occurred", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void showNutrientExceedDialog(List<String> exceededNutrients) {
        NutrientExceedDialogFragment dialog = new NutrientExceedDialogFragment(exceededNutrients);
        dialog.show(getChildFragmentManager(), "NutrientExceedDialog");
    }
    private void updateCalorieIntake(double totalCalories, int daysInMonth) {
        setupProgressBar();

        // Calculate average daily calories
        int averageCalories =  (int) (totalCalories / daysInMonth);

        int progress;
        String AvgCalorie;

        if (totalCalories > 30000) {   //assume total suggested calorie intake is 30kcal
            progress = 100; // Set progress to 100%
            MonthlyAvgcalorie.setVisibility(View.GONE);
            avgtextview.setVisibility(View.GONE);
            exceedMonthlyCalorieImg.setVisibility(View.VISIBLE); // Make the image visible
        } else {
            progress = (int) ((totalCalories / 30000) * 100); // Calculate the progress percentage
            AvgCalorie = averageCalories + " cal"; // Display the average daily intake
            exceedMonthlyCalorieImg.setVisibility(View.GONE);
            MonthlyAvgcalorie.setText(AvgCalorie);
        }
        tvMonthlyCalorieIntake.setText(String.format("Calories: %.0f/30000", totalCalories));
        MonthlyprogressCalorieIntake.setProgress(progress);
    }

    private void setupProgressBar() {
        MonthlyprogressCalorieIntake = getView().findViewById(R.id.thisMonthProgressBar);
        MonthlyAvgcalorie = getView().findViewById(R.id.thisMonthavg_cal);
    }

    /**
     * Updates the nutrition chart with current data.
     *
     * @param protein Protein content in grams
     * @param carbs Carbohydrate content in grams
     * @param fat Fat content in grams
     * @param sodium Sodium content in milligrams
     * @param iron Iron content in milligrams
     * @param calcium Calcium content in milligrams
     * @param cholesterol Cholesterol content in milligrams
     * @param magnesium Magnesium content in milligrams
     * @param potassium Potassium content in milligrams
     */
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
                Toast.makeText(getContext(), "Selected: " + barName, Toast.LENGTH_SHORT).show();
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

    /**
     * Updates the meals history display with the provided meal images.
     * Sets up click listeners for meal detail viewing and handles navigation
     * to the meal history detail screen.
     *
     * @param mealImages List of meal image URLs to display
     * @throws IllegalStateException if the RecyclerView is not properly initialized
     */
    private void updateMealsHistory(List<String> mealImages) {
        MealsAdapter adapter = new MealsAdapter(mealImages);
        rvMonthlyMealsHistory.setAdapter(adapter);
        // Attach click listener to the RecyclerView
        rvMonthlyMealsHistory.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }
                    });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(childView);
                    if (position != RecyclerView.NO_POSITION) {
                        String imageUrl = mealImages.get(position);

                        // Navigate to MealHistoryActivity
                        // You need to get the nutritional details for this image, e.g.:
                        NutritionalRecord selectedRecord = getNutritionalRecordForImage(imageUrl);
                        if (selectedRecord != null) {
                            // Now you have the selected record, pass it along with the image URL
                            Intent intent = new Intent(getContext(), MealHistory.class);
                            intent.putExtra("imageUrl", imageUrl);
                            intent.putExtra("calories", selectedRecord.getCalories());
                            intent.putExtra("protein", selectedRecord.getProtein());
                            intent.putExtra("carbs", selectedRecord.getCarbs());
                            intent.putExtra("fat", selectedRecord.getFat());
                            intent.putExtra("sodium", selectedRecord.getSodium());
                            intent.putExtra("iron", selectedRecord.getIron());
                            intent.putExtra("calcium", selectedRecord.getCalcium());
                            intent.putExtra("cholesterol", selectedRecord.getCholesterol());
                            intent.putExtra("magnesium", selectedRecord.getMagnesium());
                            intent.putExtra("potassium", selectedRecord.getPotassium());

                            startActivity(intent);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Retrieves the nutritional record associated with a specific meal image.
     *
     * @param imageUrl The URL of the meal image to search for
     * @return The matching NutritionalRecord, or null if no match is found
     * @throws IllegalArgumentException if imageUrl is null
     */
    private NutritionalRecord getNutritionalRecordForImage(String imageUrl) {
        // This function assumes you have some way to get the nutritional record for the selected image.
        // If you already have a list of NutritionalRecords, you can iterate over them to find the corresponding record.
        // Here's a simple loop assuming `records` is the list of all nutritional records:

        if (imageUrl == null) {
            throw new IllegalArgumentException("Image URL cannot be null");
        }
        for (NutritionalRecord record : records) {
            if (record.getImage() != null && record.getImage().equals(imageUrl)) {
                return record;
            }
        }
        return null;
    }

    private static class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealsViewHolder> {
        private final List<String> mealImages;

        MealsAdapter(List<String> mealImages) {
            this.mealImages = mealImages;
        }

        @NonNull
        @Override
        public MealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_meal_image, parent, false);
            return new MealsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MealsViewHolder holder, int position) {
            String imageUrl = mealImages.get(position);
            Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mealImages.size();
        }

        static class MealsViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            MealsViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_view_meal);
            }
        }
    }
}