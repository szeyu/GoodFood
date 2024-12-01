package com.hmir.goodfood;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TipsAdapter adapter;
    private List<TipItem> tipsList;

    private RecyclerView recyclerView;
    private Handler autoScrollHandler = new Handler();
    private Runnable autoScrollRunnable;
    private int currentPosition = 0;  // Define currentPosition here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Populate the tips list
        tipsList = new ArrayList<>();
        tipsList.add(new TipItem(R.drawable.tip1, "Eat Healthy", "Maintaining good health should be the primary focus of everyone."));
        tipsList.add(new TipItem(R.drawable.tip2, "Healthy Recipes", "Browse thousands of healthy recipes from all over the world."));
        tipsList.add(new TipItem(R.drawable.tip3, "Track Your Health", "With amazing inbuilt tools you can track your progress."));

        adapter = new TipsAdapter(this, tipsList);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
        addDots(tipsList.size(), currentPosition);
// Start auto-slide
        startAutoSwipe();

        // Set "Get Started" button listener
        findViewById(R.id.getStartedButton).setOnClickListener(v -> {
            Intent intent = new Intent(TipsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        // Add scroll listener to update the dots
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (position != currentPosition) {
                    currentPosition = position;
                    addDots(tipsList.size(), currentPosition);  // Update dots when scrolling
                }
            }
        });

    }

    private void startAutoSwipe() {
        final Handler handler = new Handler();
        final Runnable swipeRunnable = new Runnable() {
            @Override
            public void run() {
                // Update the current position
                currentPosition = (currentPosition + 1) % tipsList.size(); // Loop back to the first tip

                // Smooth scroll to the new position
                recyclerView.smoothScrollToPosition(currentPosition);

                // Force the transition to swipe left-to-right
                if (currentPosition == 0) {
                    // Delay before resetting back to the first item, simulating a left-to-right effect
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(0); // Reset to the first item instantly
                        }
                    }, 1500); // This delay can be adjusted
                }

                handler.postDelayed(this, 4000); // Change tip every 4 seconds
            }
        };
        handler.post(swipeRunnable);
    }

    private void addDots(int count, int currentIndex) {
        LinearLayout dotLayout = findViewById(R.id.dot_layout);
        dotLayout.removeAllViews(); // Clear previous dots

        // Define dot size and margin
        int activeDotWidth = 70;  // Width of the active dot
        int activeDotHeight = 30; // Height of the active dot
        int inactiveDotWidth = 40;  // Width of the inactive dot
        int inactiveDotHeight = 20; // Height of the inactive dot
        int dotMargin = 15; // Adjust margin between dots

        for (int i = 0; i < count; i++) {
            // Create a rounded rectangle shape
            ShapeDrawable dotShape = new ShapeDrawable(new RoundRectShape(
                    new float[] {20, 20, 20, 20, 20, 20, 20, 20}, // Rounded corners
                    null, null)); // Optional padding

            // Set the background color based on whether it's the current index
            int color = (i == currentIndex) ? Color.parseColor("#FF5733") : Color.parseColor("#FFD8B3"); // Light orange for inactive
            dotShape.getPaint().setColor(color);

            // Create a View to hold the shape
            View dotView = new View(this);

            // Set the size based on whether it's active or inactive
            final int startWidth = (i == currentIndex) ? inactiveDotWidth : activeDotWidth;
            final int startHeight = (i == currentIndex) ? inactiveDotHeight : activeDotHeight;

            dotView.setLayoutParams(new LinearLayout.LayoutParams(startWidth, startHeight));
            dotView.setBackground(dotShape);

            // Set margin between dots
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dotView.getLayoutParams();
            params.setMargins(dotMargin, 0, dotMargin, 0);  // Left and right margins
            dotView.setLayoutParams(params);

            // Animate the size change of the dot
            ValueAnimator animatorWidth = ValueAnimator.ofInt(startWidth, (i == currentIndex) ? activeDotWidth : inactiveDotWidth);
            ValueAnimator animatorHeight = ValueAnimator.ofInt(startHeight, (i == currentIndex) ? activeDotHeight : inactiveDotHeight);

            animatorWidth.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dotView.getLayoutParams();
                layoutParams.width = animatedValue;
                dotView.setLayoutParams(layoutParams);
            });

            animatorHeight.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dotView.getLayoutParams();
                layoutParams.height = animatedValue;
                dotView.setLayoutParams(layoutParams);
            });

            animatorWidth.setDuration(300);  // Adjust duration for smoothness
            animatorHeight.setDuration(300); // Adjust duration for smoothness

            animatorWidth.start();
            animatorHeight.start();

            // Add the dot (rounded rectangle) to the layout
            dotLayout.addView(dotView);
        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoScrollHandler.removeCallbacks(autoScrollRunnable); // Stop auto-scroll on destroy
    }

}