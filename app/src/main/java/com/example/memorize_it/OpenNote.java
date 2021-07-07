package com.example.memorize_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class OpenNote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_note2);
        Intent intent = getIntent();
        TextView name = findViewById(R.id.name_note);
        name.setText(intent.getStringExtra("name"));
        TextView message = findViewById(R.id.message_note);
        message.setText(intent.getStringExtra("message"));
    }
}