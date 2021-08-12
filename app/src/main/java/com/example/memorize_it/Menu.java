package com.example.memorize_it;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onClicked (View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.Create_button:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.Read_button:
                intent = new Intent(this, ReadActivity.class);
                break;
            case R.id.Exit_button:
                intent = new Intent(this, General.class);
                break;
        }
        startActivity(intent);
    }
}