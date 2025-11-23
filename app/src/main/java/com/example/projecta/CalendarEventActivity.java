package com.example.projecta; // ⭐️ ודא שזו החבילה שלך

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// ⭐️ שים לב: הוא יורש מ-AppCompatActivity (בינתיים)
// בשלב 4 נשנה את זה
public class CalendarEventActivity extends MasterActivity {

    private EditText etEventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_event);

        // אתחול שדה הקלט
        etEventTitle = findViewById(R.id.etEventTitle);
    }

    /**
     * פונקציה זו נקראת אוטומטית בלחיצה על הכפתור
     * (בגלל שכתבנו android:onClick ב-XML)
     */
    public void createCalendarEvent(View view) {
        // 1. קבלת הטקסט מהשדה
        String title = etEventTitle.getText().toString();

        // 2. בדיקה שהטקסט לא ריק
        if (title.isEmpty()) {
            Toast.makeText(this, "אנא הזן כותרת לאירוע", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. יצירת Intent (כוונה) לפתיחת לוח השנה
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);

        // 4. הוספת הפרטים (הכותרת) ל-Intent
        intent.putExtra(CalendarContract.Events.TITLE, title);

        // (אופציונלי: אפשר להוסיף עוד פרטים, כמו מיקום או תיאור)
        // intent.putExtra(CalendarContract.Events.DESCRIPTION, "תיאור האירוע");
        // intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "מיקום האירוע");

        // 5. הפעלת ה-Intent
        // נבדוק קודם שיש אפליקציית לוח שנה בטלפון
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "לא נמצאה אפליקציית לוח שנה", Toast.LENGTH_SHORT).show();
        }
    }
}