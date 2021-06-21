package com.example.memorize_it;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {
    String TAG = "HO-HO-HO";
    public static final String CHANNEL_ID = "1";
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

    public void open_app(int id){
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("Notes", null, null, null, null, null, null);

        //Getting values
        c.moveToPosition(id);
        String title = c.getString(c.getColumnIndex("name"));
        String text = c.getString(c.getColumnIndex("message"));
        String id_in_table = String.valueOf(c.getInt(c.getColumnIndex("id")));

        //Closing connect
        c.close();
        db.close();

        //Deleting worked notifications
        db = helper.getWritableDatabase();
        db.delete("Notes", "id = ?", new String[] {id_in_table});
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
        Cursor c = db.query("Notes", null, null, null, null, null, null);

        //Getting values
        c.moveToPosition(id);
        String title = c.getString(c.getColumnIndex("name"));
        String text = c.getString(c.getColumnIndex("message"));

        //Creating notification
        Intent intent = new Intent(this, OpenNote.class);
        intent.putExtra("name", title);
        intent.putExtra("message", text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //Creating notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, builder.build());
        String id_in_table = String.valueOf(c.getInt(c.getColumnIndex("id")));

        //Closing connect
        c.close();
        db.close();

        //Deleting worked notifications
        db = helper.getWritableDatabase();
        db.delete("Notes", "id = ?", new String[] {id_in_table});
        db.close();
        helper.close();

    }
}

class PrimeThread extends Thread {

    MyService service;
    Date near_date;
    int this_i;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public PrimeThread(MyService service){
        this.service = service;
    }

    public void run(){
        set_near_date();
        Date date_now;
        while (true){
            try {
                date_now = new Date();
                try {
                    date_now = sdf.parse(sdf.format(date_now));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date_now.compareTo(near_date) == 0) {
                    this.service.notif(this_i);
                    // this.service.open_app(this_i);
                    set_near_date();
                }
                date_now = new Date();
                Thread.sleep(60000 - date_now.getSeconds() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Сервис работает");
        }
    }

    public void set_near_date(){
        DBHelper helper = new DBHelper(this.service);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("Notes", null, null, null, null, null, null);
        long min_dif = Long.MAX_VALUE;
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);


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
            long dif = date.getTime() - date_now.getTime();

            if (date.compareTo(date_now) == 0) {
                this.service.notif(i);
            } else if (dif < min_dif) {
                min_dif = dif;
                near_date = date;
                this_i = i;
            }
        }
        c.close();
        helper.close();
        db.close();

    }
}
