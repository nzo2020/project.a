package com.example.projecta;

// ⭐️ ייבוא הקבצים שהעתקת מהמורה
import com.example.projecta.GeminiCallback;
import com.example.projecta.GeminiManager;
// import com.example.projecta.Prompts; // אופציונלי

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioAnalysisActivity extends MasterActivity {

    private static final String LOG_TAG = "AudioAnalysisTest";
    private String outputFilePath = null; // ⭐️ משתנה יחיד שמחזיק את הנתיב

    // רכיבי UI
    private Button btnRecord;
    private Button btnStopAndPlay;
    private Button btnAnalyze;
    private ProgressBar progressBarAnalysis;
    private TextView tvAnalysisResult;

    // רכיבי לוגיקה
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private GeminiManager geminiManager; // ⭐️ המנהל של המורה

    // משגר בקשת הרשאה (רק להקלטה)
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Permission denied. Cannot record audio.", Toast.LENGTH_SHORT).show();
                    btnRecord.setEnabled(false);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_analysis);

        // אתחול רכיבי UI
        btnRecord = findViewById(R.id.btnRecord);
        btnStopAndPlay = findViewById(R.id.btnStopAndPlay);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        progressBarAnalysis = findViewById(R.id.progressBarAnalysis);
        tvAnalysisResult = findViewById(R.id.tvAnalysisResult);

        // אתחול לוגיקה
        geminiManager = GeminiManager.getInstance();
        outputFilePath = getExternalCacheDir().getAbsolutePath() + "/audio_record.3gp";

        // בקשת הרשאה
        checkAndRequestPermission();

        // מצב התחלתי
        btnRecord.setEnabled(true);
        btnStopAndPlay.setEnabled(false);
        btnAnalyze.setEnabled(false);
    }

    // --- פונקציות שלב 1: הקלטה (אקטיביטי 7) ---

    public void startRecording(View view) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

            // עדכון כפתורים: רק "עצור" פעיל
            setButtonState(false, true, false);
            tvAnalysisResult.setText(""); // ניקוי תוצאה קודמת
            Toast.makeText(this, "ההקלטה החלה...", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed: " + e.getMessage());
        }
    }

    public void stopAndPlayRecording(View view) {
        // 1. עצירת ההקלטה
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            // עדכון כפתורים: כלום לא פעיל בזמן הניגון
            setButtonState(false, false, false);
            Toast.makeText(this, "ההקלטה הסתיימה, מנגן...", Toast.LENGTH_SHORT).show();

            // 2. הפעלת הניגון (כמו שביקשת)
            playRecording();

        } catch (RuntimeException stopException) {
            Log.e(LOG_TAG, "Stop failed: " + stopException.getMessage());
            if (mediaRecorder != null) mediaRecorder.release();
            setButtonState(true, false, false); // איפוס במקרה של שגיאה
        }
    }

    private void playRecording() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(outputFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            // הגדרת מאזין לסיום הניגון
            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.release();
                mediaPlayer = null;

                // ⭐️ סיום הניגון: הפעל הקלטה חדשה ו-הפעל ניתוח
                setButtonState(true, false, true);
                Toast.makeText(this, "הניגון הסתיים", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            Log.e(LOG_TAG, "play() failed: " + e.getMessage());
            setButtonState(true, false, false); // איפוס במקרה של שגיאה
        }
    }

    // --- פונקציות שלב 2: ניתוח (אקטיביטי 8) ---

    public void analyzeAudioFile(View view) {
        setLoadingState(true);
        tvAnalysisResult.setText("קורא קובץ...");

        // 1. קריאת הקובץ והמרתו למערך בתים (byte[])
        byte[] audioBytes = readFileToBytes(outputFilePath);
        if (audioBytes == null) {
            tvAnalysisResult.setText("נכשל בקריאת הקובץ");
            setLoadingState(false);
            return;
        }

        tvAnalysisResult.setText("שולח קובץ לניתוח AI...");

        // 2. הכנת הפרומפט (הבקשה לג'מיני)
        String prompt = "אנא תמלל את קובץ הקול המצורף. לאחר מכן, סכם את הנאמר במשפט אחד בעברית.";

        // 3. ⭐️ שליחה באמצעות השיטה של המורה ⭐️
        geminiManager.sendTextWithFilePrompt(prompt, audioBytes, "audio/3gpp", new GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                tvAnalysisResult.setText(result);
                setLoadingState(false);
            }

            @Override
            public void onFailure(Throwable error) {
                tvAnalysisResult.setText("שגיאה בניתוח: " + error.getMessage());
                setLoadingState(false);
            }
        });
    }

    // --- פונקציות עזר ---

    private byte[] readFileToBytes(String filePath) {
        File file = new File(filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBarAnalysis.setVisibility(View.VISIBLE);
            setButtonState(false, false, false); // נטרל הכל בזמן טעינה
        } else {
            progressBarAnalysis.setVisibility(View.GONE);
            // הפעל כפתורים רלוונטיים (הקלטה וניתוח)
            setButtonState(true, false, true);
        }
    }

    private void setButtonState(boolean record, boolean stop, boolean analyze) {
        btnRecord.setEnabled(record);
        btnStopAndPlay.setEnabled(stop);
        btnAnalyze.setEnabled(analyze);
    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}