package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private DatabaseHelper dbHelper;

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

        dbHelper = new DatabaseHelper(this);

        // Initialize itemList and populate it from the database
        itemList = dbHelper.getAllItems(); // Get the list of items directly

        filteredList = new ArrayList<>();
        adapter = new SearchAdapter(filteredList);
        searchResults.setLayoutManager(new LinearLayoutManager(this));
        searchResults.setAdapter(adapter);

        // Initially hide the RecyclerView
        searchResults.setVisibility(View.GONE);
        graphicMenu.setVisibility(View.VISIBLE);
        captionTag.setVisibility(View.VISIBLE);

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

        adapter.setOnItemClickListener(id -> {
            Intent intent = new Intent(SearchActivity.this, DisplayActivity.class);
            intent.putExtra("ITEM_ID", id); // Send item ID
            startActivity(intent);
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
                    // Add item to filtered list, passing all required parameters
                    filteredList.add(new Item(
                            item.getId(),
                            item.getName(),
                            item.getImageResourceName(),
                            item.getFatContent(),
                            item.getCalories(),
                            item.getProteinContent(),
                            item.getDescription(),
                            item.getIngredients()
                    ));
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

