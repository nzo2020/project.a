package com.example.projecta;

import static com.example.projecta.FBRef.refMessages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends MasterActivity {

    private EditText etId, etContent;
    private ListView lvMessages;

    private ArrayList<String> messageList;
    private ArrayAdapter<String> messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // קישור המשתנים לרכיבי ה-XML
        etId = findViewById(R.id.etId);
        etContent = findViewById(R.id.etContent);
        lvMessages = findViewById(R.id.lvMessages);

        // אתחול הרשימה וה-Adapter
        messageList = new ArrayList<>();
        messageAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                messageList);
        lvMessages.setAdapter(messageAdapter);

        setupDatabaseListener();
    }

    public void saveData(View view) {
        String id = etId.getText().toString();
        String content = etContent.getText().toString();

        if (id.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        refMessages.child(id).setValue(content)
                .addOnSuccessListener(aVoid -> {
                    // הצלחה
                    Toast.makeText(MainActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    // נקה את השדות לאחר שמירה
                    etId.setText("");
                    etContent.setText("");
                })
                .addOnFailureListener(e -> {
                    // כישלון
                    Toast.makeText(MainActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupDatabaseListener() {
        refMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = data.getKey();
                    String content = data.getValue(String.class);

                    messageList.add("ID: " + id + "\nContent: " + content);
                }

                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MainActivity", "Failed to read data.", error.toException());
                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}