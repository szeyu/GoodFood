package com.hmir.goodfood;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recipe with a name, list of ingredients, and list of steps.
 * Implements Parcelable to allow passing Recipe objects between activities.
 */
public class Recipe implements Parcelable {
    private String name;
    private List<String> ingredients;
    private List<String> steps;

    // Constructor
    public Recipe(String name, List<String> ingredients, List<String> steps) {
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients); // Defensive copy
        this.steps = new ArrayList<>(steps); // Defensive copy
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return new ArrayList<>(ingredients); // Defensive copy
    }

     public List<String> getSteps() {
        return new ArrayList<>(steps); // Corrected to return steps
    }

    // Parcelable implementation
    protected Recipe(Parcel in) {
        name = in.readString();
        ingredients = in.createStringArrayList();
        steps = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(ingredients);
        dest.writeStringList(steps);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
