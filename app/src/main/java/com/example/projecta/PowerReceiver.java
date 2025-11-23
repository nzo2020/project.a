package com.example.projecta; // ודא שזה שם החבילה שלך

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) return;

        // בדיקה איזה אירוע קרה (בדיוק כמו שביקשת)
        if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
            Toast.makeText(context, "מטען חובר", Toast.LENGTH_SHORT).show();
        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Toast.makeText(context, "מטען נותק", Toast.LENGTH_SHORT).show();
        }
    }
}