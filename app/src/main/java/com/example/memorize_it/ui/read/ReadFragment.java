package com.example.memorize_it.ui.read;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.memorize_it.DBHelper;
import com.example.memorize_it.Note;
import com.example.memorize_it.NoteAdapter;
import com.example.memorize_it.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class ReadFragment extends Fragment {
    ArrayList<Note> notes = new ArrayList<>();
    NoteAdapter adapter;
    boolean selection_mode = false;
    Menu menu;
    View view;
    ArrayList<Integer> selected_ids = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_read, container, false);
        view.findViewById(R.id.i_havent_text).setVisibility(View.INVISIBLE);
        fillData();
        adapter = new NoteAdapter(getContext(), notes, this);
        ListView list = view.findViewById(R.id.List_of_notes);
        list.setAdapter(adapter);
        return view;
    }

    void zero_items(){
        view.findViewById(R.id.i_havent_text).setVisibility(View.VISIBLE);
    }

    void fillData() {
        DBHelper helper = new DBHelper(getContext());
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
            notes.add(n);
        }while (c.moveToNext());

        helper.close();
        db.close();
        c.close();
    }
}
