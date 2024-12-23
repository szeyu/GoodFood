package com.hmir.goodfood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hmir.goodfood.R;
import com.hmir.goodfood.utilities.NutritionalRecord;
import com.hmir.goodfood.utilities.NutritionalRecordHelper;

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

    private NutritionalRecordHelper recordHelper = new NutritionalRecordHelper();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        tvCalorieIntake = view.findViewById(R.id.tv_calorie_intake);
        progressCalorieIntake = view.findViewById(R.id.todayProgressBar);
        barChartNutrition = view.findViewById(R.id.todayBarChart);
        rvMealsHistory = view.findViewById(R.id.rv_meals_history);

        rvMealsHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        fetchTodayData();

        return view;
    }

    private void fetchTodayData() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        recordHelper.fetchAllNutritionalRecords().addOnSuccessListener(records -> {
            double totalCalories = 0;
            double totalProtein = 0, totalCarbs = 0, totalFat = 0, totalSodium = 0, totalCalcium=0, totalCholesterol=0, totalMagnesium=0, totalIron=0, totalPotassium=0;
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

                    if (record.getImage() != null) {
                        mealImages.add(record.getImage().toString());
                    }
                }
            }

            updateCalorieIntake(totalCalories);
            updateNutritionChart(totalProtein, totalCarbs, totalFat, totalSodium, totalIron, totalCalcium, totalCholesterol, totalMagnesium, totalPotassium);
            updateMealsHistory(mealImages);

        }).addOnFailureListener(e -> Log.e("TodayFragment", "Error fetching records", e));
    }

    private void updateCalorieIntake(double totalCalories) {
        tvCalorieIntake.setText(String.format("Calories: %.0f/1000", totalCalories));
        progressCalorieIntake.setProgress((int) totalCalories);
    }

    private void updateNutritionChart(double protein, double carbs, double fat, double sodium, double iron, double calcium, double cholesterol, double magnesium, double potassium) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) protein));
        entries.add(new BarEntry(1, (float) carbs));
        entries.add(new BarEntry(2, (float) fat));
        entries.add(new BarEntry(3, (float) sodium));
        entries.add(new BarEntry(4, (float) iron ));
        entries.add(new BarEntry(5, (float) calcium));
        entries.add(new BarEntry(6, (float) cholesterol));
        entries.add(new BarEntry(7, (float) magnesium ));
        entries.add(new BarEntry(8, (float) potassium ));

        BarDataSet dataSet = new BarDataSet(entries, "Nutrition Intake");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barChartNutrition.setData(barData);
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
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new MealsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MealsViewHolder holder, int position) {
            // Use Glide or Picasso for image loading
            // Glide.with(holder.itemView.getContext()).load(mealImages.get(position)).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mealImages.size();
        }

        static class MealsViewHolder extends RecyclerView.ViewHolder {
            MealsViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
