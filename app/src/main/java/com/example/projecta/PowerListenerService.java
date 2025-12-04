package com.example.projecta;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

public class PowerListenerService extends Service {

    private PowerReceiver powerReceiver;
    private static final String CHANNEL_ID = "PowerServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // הפיכת השירות ל-Foreground Service
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("שירות האזנה")
                .setContentText("מאזין לחיבורי חשמל...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        // 1. יוצרים את ה-Receiver
        if (powerReceiver == null) {
            powerReceiver = new PowerReceiver();
        }

        // 2. יוצרים "פילטר" לאירועים שאנו רוצים
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        // 3. רושמים את ה-Receiver
        registerReceiver(powerReceiver, filter);

        Toast.makeText(this, "ההאזנה לטעינה הופעלה", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (powerReceiver != null) {
            unregisterReceiver(powerReceiver);
            powerReceiver = null;
        }
        Toast.makeText(this, "ההאזנה לטעינה בוטלה", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Power Listener Service Channel",
                    // ⭐️ --- התיקון נמצא כאן --- ⭐️
                    // שיניתי מ-IMPORTANCE_DEFAULT ל-IMPORTANCE_LOW
                    // זה יציב יותר עבור התראות קבועות של שירות
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}