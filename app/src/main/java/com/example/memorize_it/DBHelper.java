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
        //Василий, в sql есть тлько TEXT
        Log.i("HO-HO-HO", "Create table");
        db.execSQL("Create table Notes(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, when_type TEXT, info TEXT, time TEXT, runned BOOLEAN)"); // info are json
        db.execSQL("Create table working_notes(note_id INTEGER, times TEXT, info TEXT, FOREIGN KEY (note_id) REFERENCES Notes(id))"); // times and info are json
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
