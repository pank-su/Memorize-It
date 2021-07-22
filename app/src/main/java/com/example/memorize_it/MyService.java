package com.example.memorize_it;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.util.Pair;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MyService extends Service {
    public static final String CHANNEL_ID = "2";
    PrimeThread p;
    ContentValues cv;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            p.update_table();
            p.set_dates(false);
        } catch (Exception e){
            p = new PrimeThread(this);
            p.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void open_app(int id){
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("Notes", null, "id = ?", new String[] {Integer.toString(id)}, null, null, null);

        String title = c.getString(c.getColumnIndex("name"));
        String text = c.getString(c.getColumnIndex("message"));
        String id_in_table = String.valueOf(c.getInt(c.getColumnIndex("id")));

        //Closing connect
        c.close();
        db.close();

        //Updating worked notifications
        db = helper.getWritableDatabase();
        cv = new ContentValues();
        cv.put("runned", 1);
        // db.delete("Notes", "id = ?", new String[] {id_in_table});
        db.update("Notes", cv, "id = ?", new String[] {id_in_table});
        db.close();
        helper.close();

        Intent intent = new Intent(this, OpenNote.class);
        intent.putExtra("name", title);
        intent.putExtra("message", text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void notif(int id){
        //Connect to db
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("working_notes", null, "note_id = ?", new String[] {Integer.toString(id)}, null, null, null);
        c.moveToPosition(0);

        String title = null;
        String text = null;
        try {
            JSONObject info = new JSONObject(c.getString(c.getColumnIndex("info")));
            System.out.println(c.getString(c.getColumnIndex("info")));
            String type = info.getString("type");
            switch (type){
                case "title&message":
                    title = info.getString("title");
                    text = info.getString("message");
                    break;
                case "title":
                    title = info.getString("title");
                    text = "";
                    break;
                case "question":
                    title = info.getString("question");
                    text = info.getString("answer");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Creating notification
        Intent intent = new Intent(this, OpenNote.class);
        intent.putExtra("name", title);
        intent.putExtra("message", text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, UUID.randomUUID().hashCode(), intent, 0);

        //Creating notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, builder.build());

        //Closing connect
        c.close();
        db.close();

        //Updating worked notifications
        db = helper.getWritableDatabase();
        cv = new ContentValues();
        cv.put("runned", 1);
        // db.delete("Notes", "id = ?", new String[] {id_in_table});
        db.update("Notes", cv, "id = ?", new String[] {Integer.toString(id)});
        db.close();
        helper.close();

    }
}

@RequiresApi(api = Build.VERSION_CODES.O)
class PrimeThread extends Thread {
    boolean modif = false;
    Date date_now;
    MyService service;
    List<Pair<Date, Integer>> dates = new ArrayList();
    int today =  LocalDate.now().getDayOfMonth();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public PrimeThread(MyService service){
        this.service = service;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run(){
        update_table();
        set_dates(true);
        while (true) {
            try {
                date_now = new Date();
                try {
                    date_now = sdf.parse(sdf.format(date_now));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Это надо исправлять, но пока это лучший выход
                for (Pair<Date, Integer> pair : dates) {
                    if (date_now != null && date_now.compareTo(pair.first) == 0) {
                        this.service.notif(pair.second);
                        // set_dates();
                        modif = true;
                    }
                }
                if (modif)
                    set_dates(true);
                date_now = new Date();
                int dayofmonth = LocalDate.now().getDayOfMonth();
                if (dayofmonth != today) {
                    update_table();
                    today = dayofmonth;
                }

                Thread.sleep(60000 - date_now.getSeconds() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Сервис работает");
        }
    }

    public void set_dates(boolean from_run) {
        DBHelper helper = new DBHelper(this.service);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("working_notes", null, null, null, null, null, null);
        // long min_dif = Long.MAX_VALUE;
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            Date date = null;
            try {
                JSONArray times = new JSONArray(c.getString(c.getColumnIndex("times")));
                JSONObject info = new JSONObject(c.getString(c.getColumnIndex("info")));
                date = sdf.parse(times.getString(0));
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

            dates.add(Pair.create(date, c.getInt(c.getColumnIndex("note_id"))));
            if (!from_run){
                date_now = new Date();
                try {
                    date_now = sdf.parse(sdf.format(date_now));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date_now != null && date_now.compareTo(date) == 0){
                    this.service.notif(c.getInt(c.getColumnIndex("note_id")));
                }
            }
        }
        c.close();
        helper.close();
        db.close();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update_table(){
        DBHelper helper = new DBHelper(this.service);
        SQLiteDatabase db_notes = helper.getReadableDatabase();
        Cursor c = db_notes.query("Notes", null, null, null, null, null, null);
        SQLiteDatabase db_working_notes = helper.getWritableDatabase();
        db_working_notes.delete("working_notes", "?", new String[] {"TRUE"});
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            if (c.getInt(c.getColumnIndex("runned")) == 1) {
                continue;
            }
            JSONObject info = null;
            String when_type;
            boolean cont = false;
            try {
                info = new JSONObject(c.getString(c.getColumnIndex("info")));
                when_type = c.getString(c.getColumnIndex("when_type"));
                switch (when_type){
                    case "everyday":
                        break;
                    case "everyweek":
                        JSONArray days_of_week = new JSONArray(info.getJSONArray("days of week"));
                        LocalDate localDate = LocalDate.now();
                        if (!(boolean) days_of_week.get(localDate.getDayOfWeek().getValue() - 1)){
                            cont = true;
                        }
                        break;
                }
                info.put("when_type", when_type);
                info.put("type", c.getString(c.getColumnIndex("type")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (cont){
                continue;
            }
            ContentValues cv = new ContentValues();
            cv.put("note_id", c.getInt(c.getColumnIndex("id")));
            cv.put("info", info.toString());
            JSONArray jsonArray = new JSONArray().put(c.getString(c.getColumnIndex("time")));
            cv.put("times", jsonArray.toString());
            db_working_notes.insert("working_notes", null, cv);
        }
        c.close();
        helper.close();
        db_notes.close();
        db_working_notes.close();
    }
}
