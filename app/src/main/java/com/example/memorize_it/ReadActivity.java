package com.example.memorize_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;


import org.json.JSONObject;

import java.util.ArrayList;

public class ReadActivity extends AppCompatActivity {
    String TAG = "HO-HO-HO";
    ArrayList<Note> notes = new ArrayList<>();
    NoteAdapter adapter;
    boolean selection_mode = false;
    Menu menu;
    ArrayList<Integer> selected_ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Log.i(TAG, "Start");
        findViewById(R.id.i_havent_text).setVisibility(View.INVISIBLE);
        fillData();
        adapter = new NoteAdapter(this, notes, this);
        ListView list = findViewById(R.id.List_of_notes);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        System.out.println(item.getItemId());
//        if (item.getItemId() == 0){
//            System.out.println(selected_ids);
//        }
        return true;
    }

    void zero_items(){
        findViewById(R.id.i_havent_text).setVisibility(View.VISIBLE);
    }

    void fillData() {
        Log.i(TAG, "Filldata");
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("Notes", null, null, null, null, null, null);
        c.moveToFirst();
        String annot, name, time;
        int runned, id;
        do{
            try {
                id = c.getInt(c.getColumnIndex("id"));
                JSONObject info = new JSONObject(c.getString(c.getColumnIndex("info")));
                switch (c.getString(c.getColumnIndex("type"))){
                    case "title&message":
                        name = info.getString("title");
                        annot = info.getString("message");
                        break;
                    case "title":
                        name = info.getString("title");
                        annot = "";
                        break;
                    case "question":
                        name = info.getString("question");
                        annot = info.getString("answer");
                        break;
                    default:
                        name = "";
                        annot = "";
                        break;
                }
                time = c.getString(c.getColumnIndex("time"));
                runned = c.getInt(c.getColumnIndex("runned"));
            } catch (Exception e){
                zero_items();
                e.printStackTrace();
                break;
            }

            try {
                annot = annot.substring(0, 20);
            } catch (Exception e){
                e.printStackTrace();
            }

            Note n = new Note(name, annot, time, runned, id);
            Log.i(TAG, name + " " + annot + " " + time);
            notes.add(n);
        }while (c.moveToNext());

        helper.close();
        db.close();
        c.close();
    }

    public void create_note(View v){
        Intent create = new Intent(this, MainActivity.class);
        startActivity(create);
        finish();
    }

    public void change_selection_mode(){
        menu.getItem(0).setChecked(selection_mode);
        adapter.selection_mode = selection_mode;
        adapter.notifyDataSetChanged();
        selected_ids.clear();
    }

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            selection_mode = !selection_mode;
            change_selection_mode();
            if (selection_mode)
                ((CheckBox) v.findViewById(R.id.selection_button)).setChecked(true);
            return true;
        }
    };

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
            if (isChecked)
                selected_ids.add((Integer) v.getTag());
            else {
                selected_ids.remove(selected_ids.indexOf((Integer) v.getTag()));
                if (selected_ids.size() == 0){
                    selection_mode = false;
                    change_selection_mode();
                }
            }
        }
    };
}