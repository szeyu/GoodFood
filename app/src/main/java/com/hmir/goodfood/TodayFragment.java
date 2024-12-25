package com.hmir.goodfood;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class TodayFragment extends Fragment {

    private TextView tvCalorieIntake;
    private ProgressBar progressCalorieIntake;
    private BarChart barChartNutrition;
    private RecyclerView rvMealsHistory;
    private TextView caloriePercentageText;
    private ImageButton TdynutrientIntakeInfoButton;
    private ImageView exceedImageView;
    private UserHelper userHelper = new UserHelper();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        tvCalorieIntake = view.findViewById(R.id.HOMEtv_calorie_intake);
        progressCalorieIntake = view.findViewById(R.id.HOMEtodayProgressBar);
        barChartNutrition = view.findViewById(R.id.todayBarChart);
        rvMealsHistory = view.findViewById(R.id.rv_meals_history);
        exceedImageView = view.findViewById(R.id.HOMEdizzyface);

        rvMealsHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        fetchTodayData();

        return view;
    }

    private void fetchTodayData() {
        userHelper.fetchAllUserNutritionalRecords(new UserHelper.OnRecordListFetchedCallback() {
            @Override
            public void onRecordListFetched(List<NutritionalRecord> records) {
                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                double totalCalories = 0;
                float totalProtein = 0, totalCarbs = 0, totalFat = 0, totalSodium = 0, totalCalcium = 0, totalCholesterol = 0, totalMagnesium = 0, totalIron = 0, totalPotassium = 0;
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


    private void updateMealsHistory(List<String> mealImages) {
        MealsAdapter adapter = new MealsAdapter(mealImages);
        rvMealsHistory.setAdapter(adapter);
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
