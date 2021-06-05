package com.example.memorize_it;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, "Notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("HO-HO-HO", "Create table");
        db.execSQL("Create table Notes(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name string, time string, message string)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
