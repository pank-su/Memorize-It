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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    DBHelper helper;
    boolean edit;
    int id;
    int selected_item;
    int type = 0;

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
        ((Spinner) findViewById(R.id.when_spinner)).setOnItemSelectedListener(onItemSelectedListener_days_in_week);
        ((Spinner) findViewById(R.id.type_spinner)).setOnItemSelectedListener(onItemSelectedListener_type);
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
    public void OnClicked(View v) throws JSONException {
        TextView my_time = findViewById(R.id.my_time);

        // Object for content
        ContentValues cv = new ContentValues();

        //Connecting to DB
        SQLiteDatabase db = helper.getWritableDatabase();
        //Inserting content
        if (!my_time.getText().toString().isEmpty()) {
            // cv.put("name", name.getText().toString());
            cv.put("time", my_time.getText().toString());
            JSONObject json = new JSONObject();
            switch (selected_item){
                case 0:
                    cv.put("when_type", "one_time");
                    break;
                case 1:
                    cv.put("when_type", "everyday");
                    break;
                case 2:
                    cv.put("when_type", "everyweek");
                    LinearLayout LineLayWeek = (LinearLayout) findViewById(R.id.days_in_week);
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < LineLayWeek.getChildCount(); i++) {
                        // System.out.println(((Button) linearLayout.getChildAt(i)).isSelected());
                        array.put(((Button) LineLayWeek.getChildAt(i)).isSelected());
                    }
                    json.put("days of week", array);
                    break;
                case 3:
                    cv.put("when_type", "calendar_days");
                    break;
            }
            switch (type){
                case 0:
                    cv.put("type", "title&message");
                    json.put("title", ((EditText)findViewById(R.id.name_text)).getText());
                    json.put("message", ((EditText)findViewById(R.id.message)).getText());
                    break;
                case 1:
                    cv.put("type", "title");
                    json.put("title", ((EditText)findViewById(R.id.name_text)).getText());
                    break;
                case 2:
                    cv.put("type", "question");
                    json.put("question", ((EditText)findViewById(R.id.question_mess)).getText());
                    json.put("answer", ((EditText)findViewById(R.id.answer_mess)).getText());
                    break;
            }
            cv.put("info", json.toString());
            // cv.put("message", message.getText().toString());
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
    AdapterView.OnItemSelectedListener onItemSelectedListener_days_in_week = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // String text = (String) ((TextView) view).getText();
            findViewById(R.id.days_in_week).setVisibility(View.GONE);
            selected_item = position;
            switch (position){
                case 0:
                    break;
                case 2:
                    findViewById(R.id.days_in_week).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void OnClicked_day_in_week(View v){
        Button button = (Button) v;
        button.setSelected(!button.isSelected());
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener_type = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            findViewById(R.id.Title_lay).setVisibility(View.GONE);
            findViewById(R.id.message_lay).setVisibility(View.GONE);
            findViewById(R.id.Ask_answer_lay).setVisibility(View.GONE);
            type = position;
            switch (position){
                case 0:
                    findViewById(R.id.Title_lay).setVisibility(View.VISIBLE);
                    findViewById(R.id.message_lay).setVisibility(View.VISIBLE);
                    break;
                case 1:
                    findViewById(R.id.Title_lay).setVisibility(View.VISIBLE);
                    break;
                case 2:
                    findViewById(R.id.Ask_answer_lay).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };
}

