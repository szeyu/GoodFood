package com.hmir.goodfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a recipe item in the GoodFood application.
 * This class stores recipe details including name, ingredients, steps, and recipe ID.
 * Implements defensive copying for mutable collections to ensure data integrity.
 */
public class Item {
    private String name;
    private final List<String> ingredients;
    private final List<String> steps;
    @DocumentId
    private String recipe_id;

    /**
     * Default constructor initializing empty lists for ingredients and steps.
     */
    public Item() {
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
    }

    /**
     * Constructs a new Item with the specified details.
     *
     * @param name        The name of the recipe
     * @param ingredients List of ingredients
     * @param steps       List of preparation steps
     * @param recipe_id   The unique identifier for the recipe
     */
    public Item(@Nullable String name,
                @Nullable List<String> ingredients,
                @Nullable List<String> steps,
                @Nullable String recipe_id) {
        this.name = name;
        this.ingredients = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
        this.steps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
        this.recipe_id = recipe_id;
    }

    /**
     * Gets the recipe name.
     *
     * @return The name of the recipe
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name The new name for the recipe
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Gets the list of ingredients.
     *
     * @return An unmodifiable view of the ingredients list
     */
    @NonNull
    public List<String> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * Sets the list of ingredients.
     *
     * @param ingredients The new list of ingredients
     */
    public void setIngredients(@Nullable List<String> ingredients) {
        this.ingredients.clear();
        if (ingredients != null) {
            this.ingredients.addAll(ingredients);
        }
    }

    /**
     * Gets the list of preparation steps.
     *
     * @return An unmodifiable view of the steps list
     */
    @NonNull
    public List<String> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    /**
     * Sets the list of preparation steps.
     *
     * @param steps The new list of preparation steps
     */
    public void setSteps(@Nullable List<String> steps) {
        this.steps.clear();
        if (steps != null) {
            this.steps.addAll(steps);
        }
    }

    /**
     * Gets the recipe ID.
     *
     * @return The unique identifier of the recipe
     */
    @Nullable
    public String getRecipeId() {
        return recipe_id;
    }

    /**
     * Sets the recipe ID.
     *
     * @param recipe_id The new recipe ID
     */
    public void setRecipeId(@Nullable String recipe_id) {
        this.recipe_id = recipe_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) &&
                Objects.equals(ingredients, item.ingredients) &&
                Objects.equals(steps, item.steps) &&
                Objects.equals(recipe_id, item.recipe_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients, steps, recipe_id);
    }

    @Override
    @NonNull
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                ", recipe_id='" + recipe_id + '\'' +
                '}';
    }
}