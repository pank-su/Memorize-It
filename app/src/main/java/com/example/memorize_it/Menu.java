package com.example.memorize_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
                finish();
                break;
        }
        startActivity(intent);
    }
}