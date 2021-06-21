package com.example.memorize_it;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ReadActivity extends AppCompatActivity {
    String TAG = "HO-HO-HO";
    ArrayList<Note> notes = new ArrayList<Note>();
    NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Log.i(TAG, "Start");
        findViewById(R.id.i_havent_text).setVisibility(View.INVISIBLE);
        fillData();
        adapter = new NoteAdapter(this, notes);

        ListView list = (ListView) findViewById(R.id.List_of_notes);
        list.setAdapter(adapter);
    }

    void fillData() {
        Log.i(TAG, "Filldata");
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("Notes", null, null, null, null, null, null);
        c.moveToFirst();
        do{
            String annot, name, time;

            try {
                name = c.getString(c.getColumnIndex("name"));
                time = c.getString(c.getColumnIndex("time"));
                annot = c.getString(c.getColumnIndex("message"));
            } catch (Exception e){
                findViewById(R.id.i_havent_text).setVisibility(View.VISIBLE);
                break;
            }
            try {
                annot = annot.substring(0, 20);
            } catch (Exception e){

            }

            Note n = new Note(name, annot, time);
            Log.i(TAG, name + " " + annot + " " + time);
            notes.add(n);
        }while (c.moveToNext());

        helper.close();
        db.close();
        c.close();
    }


}