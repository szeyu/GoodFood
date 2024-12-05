package com.hmir.goodfood;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TipsAdapter adapter;
    private List<TipItem> tipsList;
    private Button buttonNext;
    private int currentPosition = 0; // Track the current tip

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        buttonNext = findViewById(R.id.getStartedButton);

        // Populate the tips list
        tipsList = new ArrayList<>();
        tipsList.add(new TipItem(R.drawable.tip1, "Eat Healthy", "Maintaining good health should be the primary focus of everyone."));
        tipsList.add(new TipItem(R.drawable.tip2, "Healthy Recipes", "Browse thousands of healthy recipes from all over the world."));
        tipsList.add(new TipItem(R.drawable.tip3, "Track Your Health", "With amazing inbuilt tools you can track your progress."));

        // Set up the adapter and attach to RecyclerView
        adapter = new TipsAdapter(this, tipsList);
        recyclerView.setAdapter(adapter);

        // Initially update dots and button text
        addDots(tipsList.size(), currentPosition);
        updateButtonText();

        // Button click listener
        buttonNext.setOnClickListener(v -> {
            if (currentPosition < tipsList.size() - 1) {
                // Move to the next tip
                currentPosition++;
                recyclerView.smoothScrollToPosition(currentPosition);

                // Update dots and button text
                addDots(tipsList.size(), currentPosition);
                updateButtonText();
            } else {
                // Last tip - navigate to the main activity
                Intent intent = new Intent(TipsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // RecyclerView scroll listener to update dots on swipe
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (position != currentPosition) {
                    currentPosition = position;
                    addDots(tipsList.size(), currentPosition);
                    updateButtonText();
                }
            }
        });
    }

    private void updateButtonText() {
        if (currentPosition < tipsList.size() - 1) {
            buttonNext.setText("Next");
        } else {
            buttonNext.setText("Get Started!");
        }
    }

    private void addDots(int count, int currentIndex) {
        LinearLayout dotLayout = findViewById(R.id.dot_layout);
        dotLayout.removeAllViews(); // Clear previous dots

        // Define dot size and margin
        int activeDotWidth = 70;  // Width of the active dot
        int activeDotHeight = 30; // Height of the active dot
        int inactiveDotWidth = 40;  // Width of the inactive dot
        int inactiveDotHeight = 20; // Height of the inactive dot
        int dotMargin = 15; // Margin between dots

        for (int i = 0; i < count; i++) {
            // Create a rounded rectangle shape
            ShapeDrawable dotShape = new ShapeDrawable(new RoundRectShape(
                    new float[] {20, 20, 20, 20, 20, 20, 20, 20}, // Rounded corners
                    null, null)); // Optional padding

            // Set the color for active or inactive dots
            int color = (i == currentIndex) ? Color.parseColor("#FF5733") : Color.parseColor("#FFD8B3"); // Light orange for inactive
            dotShape.getPaint().setColor(color);

            // Create a View for each dot
            View dotView = new View(this);

            // Set initial size based on active/inactive state
            dotView.setLayoutParams(new LinearLayout.LayoutParams(
                    (i == currentIndex) ? activeDotWidth : inactiveDotWidth,
                    (i == currentIndex) ? activeDotHeight : inactiveDotHeight));
            dotView.setBackground(dotShape);

            // Add margins to the dot view
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dotView.getLayoutParams();
            params.setMargins(dotMargin, 0, dotMargin, 0);
            dotView.setLayoutParams(params);

            // Add the dot to the layout
            dotLayout.addView(dotView);

            // If it's the current index, animate the change to make it visually distinct
            if (i == currentIndex) {
                ValueAnimator animatorWidth = ValueAnimator.ofInt(inactiveDotWidth, activeDotWidth);
                ValueAnimator animatorHeight = ValueAnimator.ofInt(inactiveDotHeight, activeDotHeight);

                animatorWidth.addUpdateListener(animation -> {
                    int animatedValue = (int) animation.getAnimatedValue();
                    params.width = animatedValue;
                    dotView.setLayoutParams(params);
                });

                animatorHeight.addUpdateListener(animation -> {
                    int animatedValue = (int) animation.getAnimatedValue();
                    params.height = animatedValue;
                    dotView.setLayoutParams(params);
                });

                animatorWidth.setDuration(300);  // Smooth transition duration
                animatorHeight.setDuration(300); // Smooth transition duration

                animatorWidth.start();
                animatorHeight.start();
            }
        }
    }}
