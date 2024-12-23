package com.hmir.goodfood;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hmir.goodfood.R;
import com.hmir.goodfood.utilities.NutritionalRecord;
import com.hmir.goodfood.utilities.NutritionalRecordHelper;
import com.hmir.goodfood.utilities.RoundedBarChart;
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

    //private NutritionalRecordHelper recordHelper = new NutritionalRecordHelper();
    private UserHelper userHelper = new UserHelper();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        Toast.makeText(getContext(), "TodayFragment onCreateView Called and xml file shown", Toast.LENGTH_SHORT).show();

        tvCalorieIntake = view.findViewById(R.id.tv_calorie_intake);
        progressCalorieIntake = view.findViewById(R.id.todayProgressBar);
        barChartNutrition = view.findViewById(R.id.todayBarChart);
        rvMealsHistory = view.findViewById(R.id.rv_meals_history);

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
                double totalProtein = 0, totalCarbs = 0, totalFat = 0, totalSodium = 0, totalCalcium = 0, totalCholesterol = 0, totalMagnesium = 0, totalIron = 0, totalPotassium = 0;
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
                updateNutritionChart(totalProtein, totalCarbs, totalFat, totalSodium, totalIron, totalCalcium, totalCholesterol, totalMagnesium, totalPotassium);
                updateMealsHistory(mealImages);
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
        int progress = (int) ((totalCalories / 1000) * 100);
        tvCalorieIntake.setText(String.format("Calories: %.0f/1000", totalCalories));
        progressCalorieIntake.setProgress(progress);
        caloriePercentageText.setText(progress + "%");
    }

    private void setupProgressBar() {
        progressCalorieIntake = getView().findViewById(R.id.todayProgressBar);
        caloriePercentageText = getView().findViewById(R.id.percentage_cal);
    }

    private void updateNutritionChart(double protein, double carbs, double fat, double sodium, double iron, double calcium, double cholesterol, double magnesium, double potassium) {
        Toast.makeText(getContext(), "nutritions retriving....", Toast.LENGTH_LONG).show();
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.poppins_light_regular);

        // Prepare data entries
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

        // Labels for X-axis
        String[] nutrientLabels = {"Protein", "Carbs", "Fat", "Sodium", "Iron", "Calcium", "Cholesterol", "Magnesium", "Potassium"};

        // Color logic for bars
        ArrayList<Integer> barColors = new ArrayList<>();
        for (BarEntry entry : entries) {
            if (entry.getY() < 50) { // Example threshold
                barColors.add(ContextCompat.getColor(getContext(), R.color.yellow)); // Below threshold
            } else {
                barColors.add(ContextCompat.getColor(getContext(), R.color.red)); // Above threshold
            }
        }

        // Create BarDataSet
        BarDataSet dataSet = new BarDataSet(entries, "Nutrition Intake");
        dataSet.setColors(barColors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTypeface(customFont);
        dataSet.setValueTextSize(12f);

        // Custom value formatter for bar values
        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value); // Display values as integers
            }
        };
        dataSet.setValueFormatter(valueFormatter);

        // Create BarData and set to the chart
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.4f); // Adjust bar width
        barChartNutrition.setData(barData);

        // X-axis configuration
        XAxis xAxis = barChartNutrition.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(nutrientLabels));
        xAxis.setDrawGridLines(false);
        xAxis.setTypeface(customFont);

        // Y-axis configuration
        YAxis leftAxis = barChartNutrition.getAxisLeft();
        leftAxis.setGranularity(10f); // Adjust based on your range
        leftAxis.setAxisMinimum(0f); // Set minimum value to 0
        leftAxis.setDrawGridLines(false);
        leftAxis.setTypeface(customFont);

        barChartNutrition.getAxisRight().setEnabled(false); // Disable the right Y-axis

        // Chart properties
        barChartNutrition.getDescription().setEnabled(false);
        barChartNutrition.setDragEnabled(true);
        barChartNutrition.setExtraRightOffset(12f);
        barChartNutrition.setExtraLeftOffset(12f);
        barChartNutrition.setExtraBottomOffset(20f);

        // Optional: Use a custom renderer for rounded bars
        RoundedBarChart render = new RoundedBarChart(barChartNutrition, barChartNutrition.getAnimator(), barChartNutrition.getViewPortHandler());
        render.setmRadius(20); // Adjust bar corner radius
        barChartNutrition.setRenderer(render);

        // Refresh the chart
        barChartNutrition.invalidate();
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
