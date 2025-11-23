package com.example.projecta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MasterActivity - משמש כתבנית לכל שאר המסכים
 * כדי לספק תפריט ניווט אחיד.
 */
public class MasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // אין צורך ב-setContentView כאן,
        // כי כל אקטיביטי ש"יורש" מזה יקרא ל-setContentView משלו.
    }

    /**
     * הפונקציה הזו "מנפחת" (טוענת) את קובץ התפריט
     * שיצרנו ב- res/menu/main.xml
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * הפונקציה הזו מופעלת כשהמשתמש לוחץ על פריט בתפריט.
     * היא בודקת איזה ID נלחץ ומנווטת למסך המתאים.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // ⭐️ כאן נמצא כל הניווט שלך ⭐️

        if (id == R.id.menuActivity1) {
            // TODO: שנה את 'RegisterActivity.class' לשם הנכון של אקטיביטי 1
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menuActivity2) {
            // TODO: שנה את 'DatabaseActivity.class' לשם הנכון של אקטיביטי 2
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menuActivity3) {
            // TODO: שנה את 'StorageActivity.class' לשם הנכון של אקטיביטי 3
            Intent intent = new Intent(this, StorageActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menuActivity4) {
            // TODO: שנה את 'NotificationActivity.class' לשם הנכון של אקטיביטי 4
            Intent intent = new Intent(this, NotificationReceiver.class);
            startActivity(intent);
        }
        else if (id == R.id.menuActivity5) {
            // TODO: שנה את 'ReceiverActivity.class' לשם הנכון של אקטיביטי 5
            Intent intent = new Intent(this,ServiceControlActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menuActivity6) {
            // זה מ-MapsActivity שכבר עשינו
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menuActivity7_8) {
            // זה מ-AudioAnalysisActivity שכבר עשינו
            Intent intent = new Intent(this, AudioAnalysisActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menuActivity9) {
            // זה מ-CalendarEventActivity שכבר עשינו
            Intent intent = new Intent(this, CalendarEventActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}