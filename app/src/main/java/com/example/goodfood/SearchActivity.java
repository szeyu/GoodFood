package com.example.goodfood;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchText;
    private RecyclerView searchResults;
    private SearchAdapter adapter;
    private List<String> itemList;
    private List<String> filteredList;
    private ImageView graphicMenu; // Declare the ImageView variable
    private TextView captionTag;


    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page); // Ensure this matches your layout filename

        searchText = findViewById(R.id.searchText);
        searchResults = findViewById(R.id.searchResults);
        graphicMenu = findViewById(R.id.graphicMenu);
        captionTag = findViewById(R.id.captionTag);

        // Sample data
        itemList = new ArrayList<>();
        itemList.add("Burger");
        itemList.add("Cheese Burger");
        itemList.add("Hamburger");
        itemList.add("Vegetarian Burger");
        itemList.add("Pizza");
        itemList.add("Pasta");

        // Initialize filteredList as a copy of itemList
        filteredList = new ArrayList<>(itemList);

        adapter = new SearchAdapter(filteredList);
        searchResults.setLayoutManager(new LinearLayoutManager(this));
        searchResults.setAdapter(adapter);

        // Initially hide the RecyclerView
        searchResults.setVisibility(View.GONE);
        graphicMenu.setVisibility(View.VISIBLE);
        captionTag.setVisibility(View.VISIBLE);

        // Add a TextWatcher to the search input
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
                if (s.length() > 0) {
                    graphicMenu.setVisibility(View.GONE);
                    captionTag.setVisibility(View.GONE); // Hide ImageView
                } else {
                    graphicMenu.setVisibility(View.VISIBLE);
                    captionTag.setVisibility(View.VISIBLE); // Show ImageView if no input
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
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
            for (String item : itemList) {
                if (item.toLowerCase().contains(text.toLowerCase())) {
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
        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
    }


}
