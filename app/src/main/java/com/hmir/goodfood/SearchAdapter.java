package com.hmir.goodfood;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<Item> itemList; // Use a list of Item objects
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int id); // Pass item ID on click
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SearchAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Retrieve item details
        int id = item.getId();
        String name = item.getName();
        String imageResourceName = item.getImageResourceName();
        String fatContent = item.getFatContent();
        String calories = item.getCalories();
        String proteinContent = item.getProteinContent();
        String description = item.getDescription();
        String ingredients = item.getIngredients();

        // Set item name in the title TextView
        holder.title.setText(name);

        // Get the image resource ID
        int imageResId = item.getImageResId(holder.itemView.getContext()); // Pass context here

        // Set image to an ImageView (Assuming you have an ImageView in your item_layout)
        holder.image.setImageResource(imageResId);

        // Set click listener to pass more data via Intent
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DisplayActivity.class);
            intent.putExtra("food_name", name);
            intent.putExtra("food_id", id);
            intent.putExtra("food_image", imageResourceName); // You may want to pass the image name or resource ID
            intent.putExtra("food_fat", fatContent);
            intent.putExtra("food_calories", calories);
            intent.putExtra("food_protein", proteinContent);
            intent.putExtra("food_description", description);
            intent.putExtra("food_ingredients", ingredients);
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image; // Add ImageView to hold the image

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            image = itemView.findViewById(R.id.item_image); // Initialize ImageView
        }
    }
}
