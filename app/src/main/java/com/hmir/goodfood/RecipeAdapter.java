package com.hmir.goodfood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for displaying a list of recipes in a RecyclerView.
 * It binds each recipe to a view and handles clicks on individual recipe items through the OnRecipeClickListener.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    /**
     * Interface to handle clicks on individual recipes in the RecipeAdapter.
     * The implementing class should define what happens when a recipe is clicked.
     */
    public interface OnRecipeClickListener {
        /**
         * Called when a recipe has been clicked.
         *
         * @param recipe The recipe that was clicked.
         */
        void onRecipeClick(Recipe recipe);
    }

    /**
     * Constructs a new RecipeAdapter.
     *
     * @param recipes List of Recipe objects to be displayed in the RecyclerView
     * @param listener Listener for handling recipe click events
     */
    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = new ArrayList<>(recipes); // Defensive copy
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeNameTextView.setText(recipe.getName());
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * ViewHolder class for holding and recycling views for individual recipe items.
     * This class is used by the RecipeAdapter to bind data to the RecyclerView.
     */
    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeNameTextView;

        /**
         * Constructs a new RecipeViewHolder.
         *
         * @param itemView The view object containing the recipe item layout.
         *                 This view should contain a TextView with id 'recipeName'.
         */
        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipeName);
        }
    }
}
