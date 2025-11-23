package com.example.projecta;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class ReminderActivity extends MasterActivity {

    private EditText etMessage;
    private TimePicker timePicker;

    // שם קבוע עבור ערוץ ההתראות (חייב להיות זהה ב-Receiver)
    private static final String CHANNEL_ID = "MyReminderChannel";
    // מזהה ייחודי עבור ה-PendingIntent
    private static final int PENDING_INTENT_REQUEST_CODE = 0;

    // משגר מודרני לבקשת הרשאת התראות (מאנדרואיד 13)
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Permission denied. Cannot show notifications.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder); // ודא שזה שם קובץ ה-XML שלך

        etMessage = findViewById(R.id.etMessage);
        timePicker = findViewById(R.id.timePicker);

        // הגדרת ה-TimePicker למצב 24 שעות
        timePicker.setIs24HourView(true);

        // יצירת ערוץ התראות (חובה מאנדרואיד 8)
        createNotificationChannel();

        // בקשת הרשאה להתראות (חובה מאנדרואיד 13)
        requestNotificationPermission();
    }

    /**
     * פונקציה זו נקראת בלחיצה על הכפתור (שהוגדר ב-XML)
     */
    public void scheduleNotification(View view) {
        String message = etMessage.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "אנא הזן תוכן להתראה", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. קבלת השעה והדקה מה-TimePicker
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // 2. יצירת אובייקט Calendar עם הזמן שנבחר
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // בדיקה: אם הזמן שנבחר כבר עבר היום, קבע אותו למחר
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // 3. יצירת Intent שיפנה אל ה-BroadcastReceiver
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("notification_message", message); // העברת הטקסט שהמשתמש הקליד

        // 4. יצירת PendingIntent
        // זהו "שליח" שנותנים ל-AlarmManager כדי שהוא יוכל להפעיל את ה-Intent שלנו
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                PENDING_INTENT_REQUEST_CODE, // Request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 5. קבלת ה-AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 6. קביעת ה"אזעקה"
        if (alarmManager != null) {
            // שימוש ב-setExact כדי להבטיח הפעלה בזמן (דורש הרשאה)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "התראה נקבעה!", Toast.LENGTH_SHORT).show();
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for scheduling reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH; // עדיפות גבוהה

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // בקשת ההרשאה
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}