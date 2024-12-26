package com.hmir.goodfood;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hmir.goodfood.R;

import java.util.ArrayList;
import java.util.List;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealsViewHolder> {

    private final List<String> mealImages; // List of meal image URLs or URIs

    // Constructor to initialize the list of meal images
    public MealsAdapter(List<String> mealImages) {
        // Defensive copy to prevent external modification
        this.mealImages = new ArrayList<>(mealImages);
    }

    @NonNull
    @Override
    public MealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView (item_meal_image.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_image, parent, false);
        return new MealsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealsViewHolder holder, int position) {
        // Get the image URL or URI from the list at the current position
        String imageUrl = mealImages.get(position);

        // Use Glide (or Picasso) to load the image into the ImageView
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)  // URL or URI of the image
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        // Return the number of items (meal images) in the list
        return mealImages.size();
    }

    // ViewHolder class to hold the view elements for each item in the RecyclerView
    public static class MealsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        // Constructor to bind the view elements
        public MealsViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_meal); // Reference to the ImageView in item_meal_image.xml
        }
    }
}

