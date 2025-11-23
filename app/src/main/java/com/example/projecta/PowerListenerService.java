package com.example.projecta; // ודא שזה שם החבילה שלך

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
    private static final int NOTIFICATION_ID = 1; // מזהה להתראה של השירות

    @Override
    public void onCreate() {
        super.onCreate();
        // יוצרים ערוץ התראות (כמו באקטיביטי 4, חובה לאנדרואיד 8+)
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // הפיכת השירות ל-Foreground Service כדי שימשיך לרוץ
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("שירות האזנה")
                .setContentText("מאזין לחיבורי חשמל...")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // החלף באייקון משלך
                .build();
        startForeground(NOTIFICATION_ID, notification);

        // --- כאן קורה הקסם (בדיוק כמו בדוגמה של המורה) ---
        // 1. יוצרים את ה-Receiver
        powerReceiver = new PowerReceiver();

        // 2. יוצרים "פילטר" לאירועים שאנו רוצים
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        // 3. רושמים את ה-Receiver (רישום דינמי)
        registerReceiver(powerReceiver, filter);

        Toast.makeText(this, "ההאזנה לטעינה הופעלה", Toast.LENGTH_SHORT).show();

        return START_STICKY; // מבקשים שהשירות ימשיך לרוץ
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // --- מבטלים את הרישום כשהשירות מושמד ---
        // (בדיוק כמו שהמורה עשה ב-onPause או בכפתור)
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

    // פונקציית עזר ליצירת ערוץ התראות
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Power Listener Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}