package com.example.memorize_it;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    // String TAG = "HO-HO-HO";
    DBHelper helper;
    boolean edit;
    int id;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // getSupportActionBar().hide();
        EditText editText = findViewById(R.id.my_time);
        editText.setOnClickListener(this::showTimePickerDialog);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        // editText.setClickable(false);
        editText.setLongClickable(false);
        editText.setCursorVisible(false);
        createNotificationChannel();
        Intent intent = getIntent();
        edit = intent.getBooleanExtra("edit", false);
        if (edit){
            ((EditText) findViewById(R.id.name_text)).setText(intent.getStringExtra("name"));
            ((EditText) findViewById(R.id.my_time)).setText(intent.getStringExtra("time"));
            ((EditText) findViewById(R.id.message)).setText(intent.getStringExtra("message"));
            id = intent.getIntExtra("id", 0);
        }
        helper = new DBHelper(this);

// notificationId is a unique int for each notification that you must define
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Memorize_it";
            String description = "Для нашего приложения";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("2", name, importance);
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
    public void OnClicked(View v) {

        //Link text objects
        TextView my_time = findViewById(R.id.my_time);
        TextView name = findViewById(R.id.name_text);
        TextView message = findViewById(R.id.message);

        // Object for content
        ContentValues cv = new ContentValues();

        //Connecting to DB
        SQLiteDatabase db = helper.getWritableDatabase();

        //Inserting content
        if (!my_time.getText().toString().isEmpty()) {
            cv.put("name", name.getText().toString());
            cv.put("time", my_time.getText().toString());
            cv.put("message", message.getText().toString());
            cv.put("runned", 0);
            if (edit)
                db.update("Notes", cv, "id = ?", new String[] {Integer.toString(id)});
            else
                db.insert("Notes", null, cv);
            //Close connect to db
            helper.close();
            db.close();
            startService(new Intent(this, MyService.class));
            if (edit) {
                Intent read = new Intent(this, ReadActivity.class);
                startActivity(read);
            }
            this.finish();
        } else{
            Toast.makeText(this, "Введите дату", Toast.LENGTH_LONG).show();
        }
    }
}

