package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

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
        tipsList.add(new TipItem(R.drawable.tip1, "Eat Healthy", "Maintain a balanced diet every day."));
        tipsList.add(new TipItem(R.drawable.tip2, "Healthy Recipes", "Discover healthy recipes from around the world."));
        tipsList.add(new TipItem(R.drawable.tip3, "Track Your Health", "Use tools to track and improve your progress."));

        adapter = new TipsAdapter(this, tipsList);
        recyclerView.setAdapter(adapter);
// Start auto-slide
        startAutoSwipe();

        // Set "Get Started" button listener
        findViewById(R.id.getStartedButton).setOnClickListener(v -> {
            Intent intent = new Intent(TipsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void startAutoSwipe() {
        final Handler handler = new Handler();
        final Runnable swipeRunnable = new Runnable() {
            @Override
            public void run() {
                // Update the current position
                currentPosition = (currentPosition + 1) % tipsList.size(); // Loop back to the first tip

                // Scroll to the new position
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

                handler.postDelayed(this, 6000); // Change tip every 3 seconds
            }
        };
        handler.post(swipeRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoScrollHandler.removeCallbacks(autoScrollRunnable); // Stop auto-scroll on destroy
    }

}