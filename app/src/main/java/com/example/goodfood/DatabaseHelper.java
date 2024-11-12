package com.example.goodfood;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "goodfood.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE items (id INTEGER PRIMARY KEY, name TEXT)";
        db.execSQL(createTable);

        // Insert initial data (replace this with your actual data insertion)
        db.execSQL("INSERT INTO items (name) VALUES ('Burger')");
        db.execSQL("INSERT INTO items (name) VALUES ('Cheese Burger')");
        db.execSQL("INSERT INTO items (name) VALUES ('Hamburger')");
        db.execSQL("INSERT INTO items (name) VALUES ('Vegetarian Burger')");
        db.execSQL("INSERT INTO items (name) VALUES ('Pizza')");
        db.execSQL("INSERT INTO items (name) VALUES ('Pasta')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }

    // Method to search items by text
    public Cursor searchItems(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM items WHERE name LIKE ?", new String[]{"%" + query + "%"});
    }
}

