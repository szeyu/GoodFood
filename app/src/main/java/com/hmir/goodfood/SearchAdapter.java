package com.hmir.goodfood;

import android.content.Intent;
import android.util.Log;
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
        void onItemClick(String recipeRef); // Pass recipeRef as String on click
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title); // Initialize TextView
        }
    }
}

