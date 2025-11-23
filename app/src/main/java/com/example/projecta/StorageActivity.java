package com.example.projecta;

import static com.example.projecta.FBRef.refMyTextFile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;

public class StorageActivity extends MasterActivity {

    private EditText etFileContent;
    private TextView tvOutput;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        etFileContent = findViewById(R.id.etFileContent);
        tvOutput = findViewById(R.id.tvOutput);

        // אתחול ProgressDialog
        pd = new ProgressDialog(this);
    }


    public void uploadFile(View view) {
        String textContent = etFileContent.getText().toString();
        if (textContent.isEmpty()) {
            Toast.makeText(this, "אנא הזן טקסט להעלאה", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] data = textContent.getBytes(StandardCharsets.UTF_8);

        pd.setTitle("מעלה קובץ...");
        pd.setMessage("אנא המתן");
        pd.show();

        refMyTextFile.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Toast.makeText(StorageActivity.this, "הקובץ הועלה בהצלחה!", Toast.LENGTH_SHORT).show();
                    etFileContent.setText("");
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(StorageActivity.this, "ההעלאה נכשלה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public void downloadFile(View view) {
        pd.setTitle("מוריד קובץ...");
        pd.setMessage("אנא המתן");
        pd.show();

        final long FIVE_MEGABYTES = 1024 * 1024 * 5;

        refMyTextFile.getBytes(FIVE_MEGABYTES)
                .addOnSuccessListener(bytes -> {
                    pd.dismiss();
                    String fileContent = new String(bytes, StandardCharsets.UTF_8);

                    tvOutput.setText(fileContent);
                    Toast.makeText(StorageActivity.this, "הקובץ הורד והוצג.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    tvOutput.setText("שגיאה בהורדת הקובץ.");
                    Toast.makeText(StorageActivity.this, "ההורדה נכשלה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}