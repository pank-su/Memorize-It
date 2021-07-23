package com.example.memorize_it;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inf;
    ArrayList<Note> notes;
    ReadActivity readActivity;
    boolean selection_mode = false;

    NoteAdapter(Context context, ArrayList<Note> objects, ReadActivity readActivity) {
        ctx = context;
        notes = objects;
        inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.readActivity = readActivity;
    }


    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    public Note getNote(int position){
        return (Note)getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = inf.inflate(R.layout.noteitem, parent, false);
        }
        Note n = getNote(position);
        ((TextView) view.findViewById(R.id.Name_note)).setText(n.name);
        ((TextView) view.findViewById(R.id.annotation)).setText(n.annotation);
        ((TextView) view.findViewById(R.id.time_note)).setText(n.date);
        Switch switch_ = (Switch) view.findViewById(R.id.switch1);
        (switch_).setChecked(n.runned == 0);
        switch_.setTag(n.id);
        switch_.setOnCheckedChangeListener(onCheckedChangeListener);
        Button btn = view.findViewById(R.id.delete);
        btn.setTag(n.id);
        btn.setOnClickListener(onClickListener_btn);
        view.setTag(n.id);
        view.setOnClickListener(onClickListener);
        view.setLongClickable(true);
        view.setOnLongClickListener(readActivity.onLongClickListener);
        view.findViewById(R.id.delete).setVisibility(View.GONE);
        view.findViewById(R.id.selection_button).setVisibility(View.GONE);

        if (selection_mode){
            view.findViewById(R.id.selection_button).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.delete).setVisibility(View.VISIBLE);
        }
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ctx, MainActivity.class);
            intent.putExtra("name", ((TextView) v.findViewById(R.id.Name_note)).getText())
                    .putExtra("edit", true)
                    .putExtra("id", (int) v.getTag())
                    .putExtra("time", ((TextView) v.findViewById(R.id.time_note)).getText())
                    .putExtra("message", ((TextView) v.findViewById(R.id.annotation)).getText());
            ctx.startActivity(intent);
            readActivity.finish();
        }
    };

    Switch.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
            DBHelper helper = new DBHelper(ctx);
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("runned", (isChecked) ? 0 : 1);
            db.update("Notes", cv, "id = ?", new String[] {Integer.toString((int) v.getTag())});
            db.close();
            helper.close();
        }
    };

    View.OnClickListener onClickListener_btn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DBHelper helper = new DBHelper(ctx);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("Notes", "id = ?", new String[] {Integer.toString((int) v.getTag())});
            int deleted = 0;
            for (int i = 0; i <= notes.size(); i++){
                if (notes.get(i).id == (int) v.getTag()){
                    deleted = i;
                    break;
                }
            }
            notes.remove(deleted);
            notifyDataSetChanged();
            if (notes.size() == 0)
                readActivity.zero_items();
        }
    };
}
