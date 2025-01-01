package com.hmir.goodfood;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying search results in a RecyclerView.
 * Handles the display and click events of recipe items in the search interface.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchItemViewHolder> {
    private List<Item> itemList; // Use a list of Item objects
    private OnItemClickListener listener;

    /**
     * Interface for handling item click events in the search results.
     */
    public interface OnItemClickListener {
        /**
         * Called when a search result item is clicked.
         *
         * @param recipeRef The reference ID of the clicked recipe
         */
        void onItemClick(String recipeRef);
    }

    /**
     * Sets the click listener for recipe items.
     *
     * @param listener The listener to handle item clicks
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SearchAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public SearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new SearchItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Retrieve item details
        String name = item.getName();  // Only name is required for search results
        String recipeRef = item.getRecipeId();  // Use recipeRef, which is a String
        Log.d("SearchAdapter", "Binding recipeRef: " + recipeRef);

        // Set item name in the title TextView
        holder.title.setText(name);

        // Set click listener to pass the recipeRef to DisplayActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DisplayActivity.class);
            intent.putExtra("recipe_id", recipeRef);  // Pass only the recipeRef
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * ViewHolder class for search result items.
     * Holds and manages the views for individual search result entries.
     */
    public static class SearchItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        /**
         * Constructs a new SearchItemViewHolder.
         *
         * @param itemView The view containing the layout for a single search result item
         */
        public SearchItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title); // Initialize TextView
        }
    }
}

