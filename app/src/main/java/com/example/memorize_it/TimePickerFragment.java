package com.example.memorize_it;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TextView view;

    public TimePickerFragment(TextView v){
        this.view = v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    @SuppressLint("SetTextI18n")
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (hourOfDay < 10 && minute < 10){
            this.view.setText("0" + hourOfDay + ":0"  + minute);
        } else if (hourOfDay < 10) {
            this.view.setText("0" + hourOfDay + ":" + minute);
        } else if (minute < 10) {
            this.view.setText("" + hourOfDay + ":0"  + minute);
        } else {
            this.view.setText("" + hourOfDay + ":"  + minute);
        }

    }
}
