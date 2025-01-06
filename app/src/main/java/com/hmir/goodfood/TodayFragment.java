package com.hmir.goodfood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * Fragment that displays the user's nutritional information for the current day.
 * This includes calorie intake, nutritional charts, and meal history.
 *
 * <p>Features:
 * <ul>
 *     <li>Daily calorie tracking with progress bar</li>
 *     <li>Nutritional breakdown chart</li>
 *     <li>Visual meal history with interactive elements</li>
 *     <li>Real-time updates from Firebase</li>
 * </ul>
 */
public class TodayFragment extends Fragment {

    private TextView tvCalorieIntake;
    private ProgressBar progressCalorieIntake;
    private BarChart barChartNutrition;
    private RecyclerView rvMealsHistory;
    private TextView caloriePercentageText;
    private ImageButton TdynutrientIntakeInfoButton;
    private ImageView exceedImageView;
    private UserHelper userHelper = new UserHelper();
    private List<NutritionalRecord> records = new ArrayList<>(); // Store fetched records

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        tvCalorieIntake = view.findViewById(R.id.HOMEtv_calorie_intake);
        progressCalorieIntake = view.findViewById(R.id.HOMEtodayProgressBar);
        barChartNutrition = view.findViewById(R.id.todayBarChart);
        rvMealsHistory = view.findViewById(R.id.rv_meals_history);
        exceedImageView = view.findViewById(R.id.HOMEdizzyface);
        rvMealsHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TdynutrientIntakeInfoButton = view.findViewById(R.id.todayNutrientIntakeInfoButton);

        fetchTodayData();

        return view;
    }

    /**
     * Fetches and processes nutritional records for the current day.
     * Updates the UI with calorie intake, nutritional charts, and meal history.
     *
     * <p>This method:
     * <ul>
     *     <li>Retrieves all nutritional records</li>
     *     <li>Filters for today's records</li>
     *     <li>Calculates total nutritional values</li>
     *     <li>Updates UI components with the processed data</li>
     * </ul>
     */
    private void fetchTodayData() {
        userHelper.fetchAllUserNutritionalRecords(new UserHelper.OnRecordListFetchedCallback() {
            @Override
            public void onRecordListFetched(List<NutritionalRecord> records) {
                TodayFragment.this.records = records;
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

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

                for (NutritionalRecord record : records) {
                    String recordDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(record.getDate_time().toDate());

                    if (recordDate.equals(todayDate)) {
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

                // Attach listener to the button
                TdynutrientIntakeInfoButton.setOnClickListener(v -> showNutrientExceedDialog(exceededNutrients));
                updateCalorieIntake(totalCalories);
                //updateNutritionChart(totalProtein, totalCarbs, totalFat, totalSodium, totalIron, totalCalcium, totalCholesterol, totalMagnesium, totalPotassium);
                updateMealsHistory(mealImages);
                try {
                    updateNutritionChart(totalProtein, totalCarbs, totalFat, totalSodium, totalIron, totalCalcium, totalCholesterol, totalMagnesium, totalPotassium);
                } catch (Exception e) {
                    Log.e("TodayFragment", "Error in updateNutritionChart: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Failed to update chart: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                if (e != null) {
                    // Show the error message in a Toast
                    String errorMessage = e.getMessage();

                    // Show a Toast with the error message (if it's too long, you can shorten it)
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unknown error occurred", Toast.LENGTH_LONG).show();
                }

                // Optionally, show a generic failure message to the user
                Toast.makeText(getContext(), "Failed to load nutritional data. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Displays a dialog showing the list of nutrients that have exceeded their recommended daily values.
     * If the provided list is empty, shows a congratulatory message instead.
     *
     * This method creates and shows a {@link NutrientExceedDialogFragment} using the child fragment manager.
     * The dialog will remain visible until dismissed by the user.
     *
     * @param exceededNutrients A list of nutrient names that have exceeded their recommended levels.
     *                         Pass an empty list if no nutrients have exceeded their limits.
     * @see NutrientExceedDialogFragment
     */
    private void showNutrientExceedDialog(List<String> exceededNutrients) {
        NutrientExceedDialogFragment dialog = new NutrientExceedDialogFragment(exceededNutrients);
        dialog.show(getChildFragmentManager(), "NutrientExceedDialog");
    }

    private void updateCalorieIntake(double totalCalories) {
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
        progressCalorieIntake = getView().findViewById(R.id.HOMEtodayProgressBar);
        caloriePercentageText = getView().findViewById(R.id.HOMEpercentage_cal);
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
    private void updateNutritionChart(float protein, float carbs, float fat, float sodium, float iron, float calcium, float cholesterol, float magnesium, float potassium) {
        Log.d("TodayFragment", "updateNutritionChart called with values: protein=" + protein + ", carbs=" + carbs + ", fat=" + fat);

        if (barChartNutrition == null) {
            Log.e("TodayFragment", "Bar chart view is null.");
            return;
        }

        // Dummy data for testing
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) protein));
        entries.add(new BarEntry(1, (float) carbs));
        entries.add(new BarEntry(2, (float) fat));
        entries.add(new BarEntry(3, (float) sodium));
        entries.add(new BarEntry(4, (float) iron));
        entries.add(new BarEntry(5, (float) calcium));
        entries.add(new BarEntry(6, (float) cholesterol));
        entries.add(new BarEntry(7, (float) magnesium));
        entries.add(new BarEntry(8, (float) potassium));

        // Names for each bar
        final String[] barNames = {
                "Protein", "Carbs", "Fat", "Sodium", "Iron", "Calcium", "Cholesterol", "Magnesium", "Potassium"
        };

        BarDataSet dataSet = new BarDataSet(entries, "Nutrients");
        dataSet.setColor(getResources().getColor(R.color.yellow)); // Use a simple color for now
        BarData barData = new BarData(dataSet);

        barChartNutrition.setData(barData);

        // Disable gridlines
        barChartNutrition.getAxisLeft().setDrawGridLines(false);
        barChartNutrition.getAxisRight().setDrawGridLines(false);
        barChartNutrition.getXAxis().setDrawGridLines(false);

        // Disable axis lines
        barChartNutrition.getAxisLeft().setDrawAxisLine(false);
        barChartNutrition.getAxisRight().setDrawAxisLine(false);
        barChartNutrition.getXAxis().setDrawAxisLine(false);

        // Hide X-axis index number
        barChartNutrition.getXAxis().setEnabled(false);
        barChartNutrition.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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
        barChartNutrition.getAxisLeft().setDrawLabels(true);
        barChartNutrition.getAxisRight().setEnabled(false); // Hide Y-axis on the right
        barChartNutrition.getXAxis().setGranularity(1f); // For proper spacing between bars

        // Set a more modern and clean appearance (optional)
        barChartNutrition.getLegend().setEnabled(false); // Disable the legend if not needed
        barChartNutrition.setDescription(null);  // Hide description

        // Add animations (optional)
        barChartNutrition.animateY(1000);
        // show the values above bars
        dataSet.setDrawValues(true);

        barChartNutrition.invalidate(); // Refresh the chart

        Log.d("TodayFragment", "updateNutritionChart completed successfully.");
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
        rvMealsHistory.setAdapter(adapter);

        // Attach click listener to the RecyclerView
        rvMealsHistory.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
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
            // Use Glide or Picasso for image loading
            // Glide.with(holder.itemView.getContext()).load(mealImages.get(position)).into(holder.imageView);
            String imageUrl = mealImages.get(position);
            // Use Glide or Picasso to load the image from the URL
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
                imageView = itemView.findViewById(R.id.image_view_meal); // Make sure this ID matches your XML
            }
        }
    }
}
