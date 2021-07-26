package com.example.memorize_it;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.selection:
                selection_mode = !selection_mode;
                change_selection_mode();
                break;
            case R.id.delete_menu:
                DBHelper helper = new DBHelper(getApplicationContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                for (int id: selected_ids) {
                    db.delete("Notes", "id = ?", new String[] {Integer.toString((int) id)});
                    adapter.notes.removeIf(note -> note.id == (int) id);
                }
                if (adapter.notes.size() == 0){
                    findViewById(R.id.i_havent_text).setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                break;
        }
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

    public void change_selection_mode() {
        System.out.println(selected_ids.toString());
        menu.getItem(0).setChecked(selection_mode);
        adapter.selection_mode = selection_mode;
        adapter.notifyDataSetChanged();
        menu.getItem(1).setEnabled(selection_mode);
        selected_ids.clear();
    }

    View.OnLongClickListener onLongClickListener = v -> {
        selection_mode = !selection_mode;
        change_selection_mode();
        if (selection_mode)
            ((CheckBox) v.findViewById(R.id.selection_button)).setChecked(true);
        return true;
    };

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (v, isChecked) -> {
        if (isChecked)
            selected_ids.add((Integer) v.getTag());
        else {
            if (selected_ids.indexOf((Integer) v.getTag()) != -1)
                selected_ids.remove(selected_ids.indexOf((Integer) v.getTag()));
            if (selected_ids.size() == 0) {
                selection_mode = false;
                change_selection_mode();
            }
        }
    };
}