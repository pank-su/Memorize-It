package com.example.memorize_it;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class NoteAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inf;
    ArrayList<Note> notes;

    NoteAdapter(Context context, ArrayList<Note> objects) {
        ctx = context;
        notes = objects;
        inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        view.setOnClickListener(onClickListener);
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ctx, OpenNote.class);
            intent.putExtra("name", ((TextView) v.findViewById(R.id.Name_note)).getText())
                    .putExtra("message", ((TextView) v.findViewById(R.id.annotation)).getText());
            ctx.startActivity(intent);
        }
    };
}
