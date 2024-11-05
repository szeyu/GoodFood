package com.example.goodfood;

import java.util.ArrayList;
import java.util.List;

public class SearchExample {

    // Initialize the list of food items
    private static final List<String> foodItems = new ArrayList<>();

    public static void main(String[] args) {
        // Step 1: Populate the list with sample data
        foodItems.add("Burger");
        foodItems.add("Cheese Burger");
        foodItems.add("Chicken Burger");
        foodItems.add("Veggie Burger");
        foodItems.add("Pizza");
        foodItems.add("Pasta");

        // Example of performing a search
        List<String> results = search("burger");
        System.out.println("Search Results: " + results);
    }

    // Step 2: Create the search method
    public static List<String> search(String query) {
        List<String> results = new ArrayList<>();

        // Step 3: Loop through the foodItems list and find matches
        for (String item : foodItems) {
            // Check if the item contains the search query (case-insensitive)
            if (item.toLowerCase().contains(query.toLowerCase())) {
                results.add(item);
            }
        }

        return results;
    }
}
