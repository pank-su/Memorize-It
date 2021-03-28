package com.example.memorize_it;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Сервис запустился", Toast.LENGTH_SHORT).show();
        File directory = getFilesDir(); //or getExternalFilesDir(null); for external storage
        File file = new File(directory, "time_text.json");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null){
            stringBuilder.append(line).append("\n");
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

// This responce will have Json Format String
        String responce = stringBuilder.toString();
        JSONArray array = new JSONArray();
        try {
            array  = new JSONArray(responce);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        for (int i = 0; i < array.length(); i++){
//            try {
//                System.out.println(array.get(i));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        PrimeThread p = new PrimeThread(array, this);
        p.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void notif(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "123")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("123")
                .setContentText("123")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(2, builder.build());
    }


}

class PrimeThread extends Thread {

    JSONArray array;
    MyService service;

    public PrimeThread(JSONArray array, MyService service){
        this.array = array;
        this.service = service;
    }



    public void run() {


        while (true){
            try {
                for (int i = 0; i < array.length(); i++){
                    try {
                        JSONObject obj = (JSONObject) array.get(i);
                        String time_in_obj = obj.keys().next();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        Date date = null;
                        try {
                            date = sdf.parse(time_in_obj);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date_now = new Date();
                        try {
                            date_now = sdf.parse(sdf.format(date_now));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date.compareTo(date_now) == 0){
                            System.out.println(123);
                            this.service.notif();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Сервис работает");
        }
    }
}
