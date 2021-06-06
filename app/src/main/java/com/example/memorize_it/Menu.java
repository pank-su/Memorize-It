package com.example.memorize_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

public class Menu extends AppCompatActivity {
    String TAG = "HO-HO-HO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    public void onClicked (View v){

        switch (v.getId()){
            case R.id.Create_button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.Read_button:
                Log.i(TAG, "Read button");
                break;
            case R.id.Exit_button:
                finish();
                break;

        }
    }
}