package com.example.memorize_it;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, MyService.class);
        context.startService(startServiceIntent);
    }
}
