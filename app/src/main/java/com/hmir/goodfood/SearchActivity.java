package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hmir.goodfood.utilities.FavouriteRecipeHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchText;
    private RecyclerView searchResults;
    private SearchAdapter adapter;
    private List<Item> itemList; // This should be a list of Item objects

    private List<Item> filteredList; // List to store items with IDs
    private ImageView graphicMenu;
    private TextView captionTag;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchText = findViewById(R.id.searchText);
        searchResults = findViewById(R.id.listResults);
        graphicMenu = findViewById(R.id.graphicMenu);
        captionTag = findViewById(R.id.captionTag);

        db = FirebaseFirestore.getInstance(); // Initialize Firebase Firestore

        // Initialize itemList and populate it from Firebase
        itemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new SearchAdapter(filteredList);
        searchResults.setLayoutManager(new LinearLayoutManager(this));
        searchResults.setAdapter(adapter);

        // Initially hide the RecyclerView
        searchResults.setVisibility(View.GONE);
        graphicMenu.setVisibility(View.VISIBLE);
        captionTag.setVisibility(View.VISIBLE);

        // Fetch the user's favorite recipes from Firebase
        fetchFavoriteRecipes();

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
                if (s.length() > 0) {
                    graphicMenu.setVisibility(View.GONE);
                    captionTag.setVisibility(View.GONE);
                } else {
                    graphicMenu.setVisibility(View.VISIBLE);
                    captionTag.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        adapter.setOnItemClickListener(recipeRef -> {
            Intent intent = new Intent(SearchActivity.this, DisplayActivity.class);
            intent.putExtra("recipe_id", recipeRef); // Send recipe reference as the ID
            startActivity(intent);
            Log.d("SearchActivity", "Passing recipe_id: " + recipeRef);

        });

    }

    private void fetchFavoriteRecipes() {
        FavouriteRecipeHelper favouriteRecipeHelper = new FavouriteRecipeHelper();
        favouriteRecipeHelper.fetchUserFavoriteRecipes(new FavouriteRecipeHelper.RecipeFetchCallback() {
            @Override
            public void onRecipesFetched(List<Item> items) {
                itemList.clear();
                itemList.addAll(items); // Populate the itemList with fetched recipes
                filter(searchText.getText().toString()); // Apply the search filter
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }



    private void filter(String text) {
        if (filteredList == null) {
            filteredList = new ArrayList<>();
        }

        filteredList.clear();
        if (text.isEmpty()) {
            searchResults.setVisibility(View.GONE); // Hide the RecyclerView when input is empty
        } else {
            for (Item item : itemList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    // Add item to filtered list
                    filteredList.add(item);
                }
            }
            // Show the RecyclerView if there are results
            if (filteredList.isEmpty()) {
                searchResults.setVisibility(View.GONE); // Hide if no results
            } else {
                searchResults.setVisibility(View.VISIBLE); // Show if there are results
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle back button click
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
