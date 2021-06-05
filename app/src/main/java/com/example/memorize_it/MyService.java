package com.example.memorize_it;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        PrimeThread p = new PrimeThread(this);
        p.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void notif(int id){
        //Connect to db
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("Notes", null, null, null, null, null, null);

        //Getting values
        c.moveToPosition(id);
        String chanelId = String.valueOf(id);
        String title = c.getString(c.getColumnIndex("name"));
        String text = c.getString(c.getColumnIndex("message"));

        //Creating notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(2, builder.build());

        //Closing connect
        c.close();
        helper.close();
        db.close();
    }
}

class PrimeThread extends Thread {

    MyService service;

    public PrimeThread(MyService service){
        this.service = service;
    }

    public void run(){
        while (true){
            try {
                DBHelper helper = new DBHelper(this.service);
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor c = db.query("Notes", null, null, null, null, null, null);

                for (int i = 0; i < c.getCount(); i++){
                    c.moveToPosition(i);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                    Date date = null;
                    try {
                        date = sdf.parse(c.getString(c.getColumnIndex("time")));
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
                        this.service.notif(i);
                    }
                }
                c.close();
                helper.close();
                db.close();
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Сервис работает");
        }
    }
}
