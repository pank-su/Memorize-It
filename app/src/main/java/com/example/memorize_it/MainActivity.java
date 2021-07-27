package com.example.memorize_it;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    DBHelper helper;
    boolean edit;
    int id;
    int selected_item = 0;
    int type = 0;
    String[] types;
    String[] when_types;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        types = getResources().getStringArray(R.array.dbTypes);
        when_types = getResources().getStringArray(R.array.dbWhenTypes);
        Date date_now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        EditText editText = findViewById(R.id.my_time);
        if (!edit)
            editText.setText(sdf.format(date_now));
        editText.setOnClickListener(this::showTimePickerDialog);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setLongClickable(false);
        editText.setCursorVisible(false);
        createNotificationChannel();
        Intent intent = getIntent();
        edit = intent.getBooleanExtra("edit", false);
        helper = new DBHelper(this);
        Cursor c;
        if (edit) {
            SQLiteDatabase db = helper.getReadableDatabase();
            id = intent.getIntExtra("id", 0);
            c = db.query("Notes", null, "id = ?", new String[]{Integer.toString(id)}, null, null, null);
            c.moveToPosition(0);
            type = Arrays.asList(types).indexOf(c.getString(c.getColumnIndex("type")));
            selected_item = Arrays.asList(when_types).indexOf(c.getString(c.getColumnIndex("when_type")));
            try {
                JSONObject info = new JSONObject(c.getString(c.getColumnIndex("info")));
                switch (type) {
                    case 0:
                        ((TextView) findViewById(R.id.name_text)).setText((String) info.get("title"));
                        ((TextView) findViewById(R.id.message)).setText((String) info.get("message"));
                        break;
                    case 1:
                        ((TextView) findViewById(R.id.name_text)).setText((String) info.get("title"));
                        break;
                    case 2:
                        ((TextView) findViewById(R.id.question_mess)).setText((String) info.get("question"));
                        ((TextView) findViewById(R.id.answer_mess)).setText((String) info.get("answer"));
                        break;
                }
                switch (selected_item) {
                    case 2:
                        JSONArray array = info.getJSONArray("days of week");
                        LinearLayout LineLayWeek = ((LinearLayout) findViewById(R.id.days_in_week));
                        for (int i = 0; i < LineLayWeek.getChildCount(); i++) {
                            ((Button) LineLayWeek.getChildAt(i)).setSelected((boolean) array.get(i));
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            c.close();
            db.close();
        }
        ((Spinner) findViewById(R.id.when_spinner)).setOnItemSelectedListener(onItemSelectedListener_days_in_week);
        ((Spinner) findViewById(R.id.type_spinner)).setOnItemSelectedListener(onItemSelectedListener_type);
        ((Spinner) findViewById(R.id.type_spinner)).setSelection(type);
        ((Spinner) findViewById(R.id.when_spinner)).setSelection(selected_item);
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
    }
    public void OnClicked(View v) throws JSONException {
        TextView my_time = findViewById(R.id.my_time);

        // Object for content
        ContentValues cv = new ContentValues();

        //Connecting to DB
        SQLiteDatabase db = helper.getWritableDatabase();
        //Inserting content
        if (!my_time.getText().toString().isEmpty()) {
            cv.put("time", my_time.getText().toString());
            JSONObject json = new JSONObject();
            cv.put("when_type", when_types[selected_item]);
            switch (selected_item){
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    LinearLayout LineLayWeek = (LinearLayout) findViewById(R.id.days_in_week);
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < LineLayWeek.getChildCount(); i++) {
                        array.put(((Button) LineLayWeek.getChildAt(i)).isSelected());
                    }
                    json.put("days of week", array);
                    break;
                case 3:
                    break;
            }
            cv.put("type", types[type]);
            switch (type){
                case 0:
                    json.put("title", ((EditText)findViewById(R.id.name_text)).getText());
                    json.put("message", ((EditText)findViewById(R.id.message)).getText());
                    break;
                case 1:
                    json.put("title", ((EditText)findViewById(R.id.name_text)).getText());
                    break;
                case 2:
                    json.put("question", ((EditText)findViewById(R.id.question_mess)).getText());
                    json.put("answer", ((EditText)findViewById(R.id.answer_mess)).getText());
                    break;
            }
            cv.put("info", json.toString());
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
            type = position;
            switch (position){
                case 0:
                    findViewById(R.id.Ask_answer_lay).setVisibility(View.GONE);

                    findViewById(R.id.Title_lay).setVisibility(View.VISIBLE);
                    findViewById(R.id.message_lay).setVisibility(View.VISIBLE);
                    break;
                case 1:
                    findViewById(R.id.message_lay).setVisibility(View.GONE);
                    findViewById(R.id.Ask_answer_lay).setVisibility(View.GONE);

                    findViewById(R.id.Title_lay).setVisibility(View.VISIBLE);
                    break;
                case 2:
                    findViewById(R.id.Title_lay).setVisibility(View.GONE);
                    findViewById(R.id.message_lay).setVisibility(View.GONE);

                    findViewById(R.id.Ask_answer_lay).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };
}

