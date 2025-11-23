package com.example.projecta; // ודא שזה שם החבילה שלך

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class ServiceControlActivity extends MasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_control);
    }

    /**
     * נקראת על ידי הכפתור הראשון ("הפעל האזנה")
     */
    public void startListening(View view) {
        Intent serviceIntent = new Intent(this, PowerListenerService.class);

        // באנדרואיד 8 ומעלה, חובה להשתמש ב-startForegroundService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    /**
     * נקראת על ידי הכפתור השני ("הפסק האזנה")
     */
    public void stopListening(View view) {
        Intent serviceIntent = new Intent(this, PowerListenerService.class);
        stopService(serviceIntent);
    }
}