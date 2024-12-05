package com.hmir.goodfood;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "goodfood.db";
    private static final int DATABASE_VERSION = 3;  // Increment version to trigger onUpgrade

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE items (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "image TEXT, " + // Store the drawable resource name
                "fat TEXT, " +
                "calories TEXT, " +
                "protein TEXT, " +
                "description TEXT, " +
                "ingredients TEXT" +
                ")";
        db.execSQL(createTable);

        // Insert initial data with drawable resource names as image references
        db.execSQL("INSERT INTO items (name, image, fat, calories, protein, description, ingredients) " +
                "VALUES ('Burger', 'burger_image', '20g', '300', '15g', 'A delicious burger', 'Bun, Patty, Lettuce')");

        db.execSQL("INSERT INTO items (name, image, fat, calories, protein, description, ingredients) " +
                "VALUES ('Hamburger', 'hamburger_image', '18g', '280', '12g', 'Classic hamburger', 'Bun, Patty, Pickles')");
        db.execSQL("INSERT INTO items (name, image, fat, calories, protein, description, ingredients) " +
                "VALUES ('Cheese Burger', 'cheese_burger_image', '25g', '400', '20g', 'Cheesy and tasty', 'Bun, Patty, Cheese, Lettuce')");
        db.execSQL("INSERT INTO items (name, image, fat, calories, protein, description, ingredients) " +
                "VALUES ('Vegetarian Burger', 'veggie_burger_image', '15g', '250', '10g', 'Healthy vegetarian burger', 'Bun, Veggie Patty, Lettuce')");
        db.execSQL("INSERT INTO items (name, image, fat, calories, protein, description, ingredients) " +
                "VALUES ('Pizza', 'pizza_image', '30g', '500', '22g', 'Cheesy pizza', 'Dough, Tomato Sauce, Cheese')");
        db.execSQL("INSERT INTO items (name, image, fat, calories, protein, description, ingredients) " +
                "VALUES ('Pasta', 'pasta_image', '10g', '350', '8g', 'Italian pasta', 'Pasta, Sauce, Cheese')");
    }

    private String getColumnValue(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
            return cursor.getString(columnIndex);
        }
        return null; // Return null if column doesn't exist or value is null
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS items");
            onCreate(db);
        }
    }


    // Method to search items by name
    public List<Item> searchItems(String query) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM items WHERE name LIKE ?", new String[]{"%" + query + "%"});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Check if the column indices are valid before accessing them
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int imageIndex = cursor.getColumnIndex("image");
                int fatIndex = cursor.getColumnIndex("fat");
                int caloriesIndex = cursor.getColumnIndex("calories");
                int proteinIndex = cursor.getColumnIndex("protein");
                int descriptionIndex = cursor.getColumnIndex("description");
                int ingredientsIndex = cursor.getColumnIndex("ingredients");

                // Handle invalid column index cases
                int id = (idIndex >= 0) ? cursor.getInt(idIndex) : -1;
                String name = (nameIndex >= 0) ? cursor.getString(nameIndex) : "Unknown Name";
                String image = (imageIndex >= 0) ? cursor.getString(imageIndex) : "Unknown Image";
                String fat = (fatIndex >= 0) ? cursor.getString(fatIndex) : "Unknown Fat Content";
                String calories = (caloriesIndex >= 0) ? cursor.getString(caloriesIndex) : "Unknown Calories";
                String protein = (proteinIndex >= 0) ? cursor.getString(proteinIndex) : "Unknown Protein Content";
                String description = (descriptionIndex >= 0) ? cursor.getString(descriptionIndex) : "No Description";
                String ingredients = (ingredientsIndex >= 0) ? cursor.getString(ingredientsIndex) : "Unknown Ingredients";

                // Add the item to the list
                items.add(new Item(id, name, image, fat, calories, protein, description, ingredients));
            }
            cursor.close();
        }

        return items;
    }




    // Method to fetch item by ID
    // Method to fetch item by ID
    public Item getItemById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "items",
                null,
                "id = ?",
                new String[]{String.valueOf(itemId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Check column indices before accessing
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int imageIndex = cursor.getColumnIndex("image");
            int fatIndex = cursor.getColumnIndex("fat");
            int caloriesIndex = cursor.getColumnIndex("calories");
            int proteinIndex = cursor.getColumnIndex("protein");
            int descriptionIndex = cursor.getColumnIndex("description");
            int ingredientsIndex = cursor.getColumnIndex("ingredients");

            // Validate column indices
            int id = (idIndex >= 0) ? cursor.getInt(idIndex) : -1;
            String name = (nameIndex >= 0) ? cursor.getString(nameIndex) : "Unknown Name";
            String image = (imageIndex >= 0) ? cursor.getString(imageIndex) : "Unknown Image";
            String fat = (fatIndex >= 0) ? cursor.getString(fatIndex) : "Unknown Fat Content";
            String calories = (caloriesIndex >= 0) ? cursor.getString(caloriesIndex) : "Unknown Calories";
            String protein = (proteinIndex >= 0) ? cursor.getString(proteinIndex) : "Unknown Protein Content";
            String description = (descriptionIndex >= 0) ? cursor.getString(descriptionIndex) : "No Description";
            String ingredients = (ingredientsIndex >= 0) ? cursor.getString(ingredientsIndex) : "Unknown Ingredients";
            Log.d("DatabaseHelper", "Fetched Item: " + name + " (ID: " + id + ")");
            Log.d("DatabaseHelper", "Image: " + image);
            cursor.close();
            return new Item(id, name, image, fat, calories, protein, description, ingredients);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }



    // Method to fetch all items as a List<Item>
    // Method to fetch all items as a List<Item>
    // Method to fetch all items as a List<Item>
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM items", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Check if the column indices are valid before accessing them
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int imageIndex = cursor.getColumnIndex("image");
                int fatIndex = cursor.getColumnIndex("fat");
                int caloriesIndex = cursor.getColumnIndex("calories");
                int proteinIndex = cursor.getColumnIndex("protein");
                int descriptionIndex = cursor.getColumnIndex("description");
                int ingredientsIndex = cursor.getColumnIndex("ingredients");

                // Validate column indices
                int id = (idIndex >= 0) ? cursor.getInt(idIndex) : -1;
                String name = (nameIndex >= 0) ? cursor.getString(nameIndex) : "Unknown Name";
                String image = (imageIndex >= 0) ? cursor.getString(imageIndex) : "Unknown Image";
                String fat = (fatIndex >= 0) ? cursor.getString(fatIndex) : "Unknown Fat Content";
                String calories = (caloriesIndex >= 0) ? cursor.getString(caloriesIndex) : "Unknown Calories";
                String protein = (proteinIndex >= 0) ? cursor.getString(proteinIndex) : "Unknown Protein Content";
                String description = (descriptionIndex >= 0) ? cursor.getString(descriptionIndex) : "No Description";
                String ingredients = (ingredientsIndex >= 0) ? cursor.getString(ingredientsIndex) : "Unknown Ingredients";

                // Log the retrieved values to verify if data is fetched
                Log.d("Database", "id: " + id + ", name: " + name + ", description: " + description);

                // Add the item to the list
                items.add(new Item(id, name, image, fat, calories, protein, description, ingredients));
            }
            cursor.close();
        }

        return items;
    }




}
