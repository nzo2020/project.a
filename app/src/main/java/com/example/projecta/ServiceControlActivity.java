package com.example.projecta;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ServiceControlActivity extends MasterActivity {

    // משתנה לבדוק אם כבר נרשמנו, כדי לא להירשם פעמיים
    private static boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_control);
    }

    public void startListening(View view) {
        if (!isRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

            // הטריק של החברה: getApplicationContext()
            // זה גורם למאזין לעבוד גם כשיוצאים מהמסך הזה למסכים אחרים באפליקציה
            getApplicationContext().registerReceiver(PowerReceiver.getInstance(), filter);

            isRegistered = true;
            Toast.makeText(this, "האזנה לטעינה הופעלה", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ההאזנה כבר פועלת", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopListening(View view) {
        if (isRegistered) {
            try {
                // ביטול הרישום
                getApplicationContext().unregisterReceiver(PowerReceiver.getInstance());
                isRegistered = false;
                Toast.makeText(this, "האזנה לטעינה הופסקה", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                // למקרה שמנסים לבטל משהו שלא קיים
            }
        } else {
            Toast.makeText(this, "ההאזנה כבויה כרגע", Toast.LENGTH_SHORT).show();
        }
    }
}