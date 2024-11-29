package com.hmir.goodfood;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.hmir.goodfood.utilities.FavouriteRecipe;
import com.hmir.goodfood.utilities.Firestore;
import com.hmir.goodfood.utilities.NutritionalRecord;
import com.hmir.goodfood.utilities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);

        // temporary here for testing
        Firestore firestore = new Firestore();
        firestore.fetchUserInfo().addOnSuccessListener(user -> {
            Log.d("Firestore", "User fetched: " + user.getEmail());
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error fetching user", e);
        });

        firestore.fetchFavouriteRecipes().addOnSuccessListener(recipes -> {
            Log.d("Firestore", "Fetched Favourite Recipes: " + recipes.size());
            for (FavouriteRecipe recipe : recipes) {
                Log.d("Firestore", "Recipe Name: " + recipe.getName());
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error fetching favourite recipes", e);
        });

        firestore.fetchNutritionalRecords().addOnSuccessListener(records -> {
            Log.d("Firestore", "Fetched Nutritional Records: " + records.size());
            for (NutritionalRecord record : records) {
                Log.d("Firestore", "Nutritional Record: " + record.getDateTime() + " Calories: " + record.getCalories());
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error fetching nutritional records", e);
        });

        String username = "John Doe";
        List<String> healthLabel = new ArrayList<>();
        healthLabel.add("halal");
        healthLabel.add("lactose intolerant");
        long age = 25;
        long height = 185;
        long weight = 70;

        firestore.updateUserInfo(username, healthLabel, age, height, weight);

        Map<String, Object> favouriteRecipe = new HashMap<>();
        favouriteRecipe.put("user_id", "test2@gmail.com");
        favouriteRecipe.put("recipe_name", "Banana Pancakes");
        favouriteRecipe.put("calories", 200);
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Flour");
        ingredients.add("Almond Milk");
        ingredients.add("Banana");
        favouriteRecipe.put("ingredients", ingredients);

        firestore.addFavouriteRecipe(favouriteRecipe);

        firestore.deleteFavouriteRecipe("6OqV4Vo7uAglO2UrYnIX");
    }

        public void toTodayStats(View view){
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("code", "today");
            startActivity(intent);
        }

        public void toThisMonthStats (View view){
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("code", "month");
            startActivity(intent);
        }


}