package com.hmir.goodfood;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying recipe results in a RecyclerView.
 * Handles the display and click events of recipe items in the results list.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private Context context;
    private List<Item> items;

    /**
     * Constructs a new ResultAdapter.
     *
     * @param context The context in which the adapter is being used
     * @param items   The list of items to display
     */
    public ResultAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = new ArrayList<>(items); // Defensive copy
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        Item item = items.get(position);
        holder.itemTitle.setText(item.getName());  // Display only name

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DisplayActivity.class);
            intent.putExtra("recipe_id", item.getRecipeId());  // Pass the recipe reference ID for fetching full details
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Static ViewHolder class for result items.
     * Holds and manages the views for individual result entries.
     */
    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemTitle;

        /**
         * Constructs a new ResultViewHolder.
         *
         * @param itemView The view containing the layout for a single result item
         */
        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
        }

        /**
         * Gets the title TextView of this ViewHolder.
         *
         * @return The TextView displaying the item title
         */
        @NonNull
        public TextView getItemTitle() {
            return itemTitle;
        }
    }
}
