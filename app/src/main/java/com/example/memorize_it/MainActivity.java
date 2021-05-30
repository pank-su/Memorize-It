package com.example.memorize_it;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();


// notificationId is a unique int for each notification that you must define

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "123";
            String description = "123";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(findViewById(R.id.my_time));
        newFragment.show(getSupportFragmentManager(), "timePicker");
        System.out.println(newFragment.getDialog());
    }
    public void OnClicked(View v) throws JSONException {
        JSONArray time_message = new JSONArray();
        JSONObject obj = new JSONObject();
        TextView my_time = findViewById(R.id.my_time);
        obj.put(my_time.getText().toString(), "123");
        time_message.put(obj);
        File directory = getFilesDir(); //or getExternalFilesDir(null); for external storage
        File file = new File(directory, "time_text.json");
        System.out.println(time_message.toString());
        System.out.println(directory.toString());
        try (FileWriter file_ = new FileWriter(file)) {
            //We can write any JSONArray or JSONObject instance to the file
            file_.write(time_message.toString());
            file_.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        startService(new Intent(this, MyService.class));
    }


}

