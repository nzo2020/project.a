package com.example.projecta; // ודא שזה שם החבילה שלך

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

// 1. שם המחלקה תואם לשם הקובץ (MapsActivity.java)
public class MapsActivity extends MasterActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    // משגר מודרני לבקשת הרשאת מיקום
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // אם ניתנה הרשאה, נסה לקבל מיקום
                    getCurrentLocation();
                } else {
                    // אם לא, הצג הודעה
                    Toast.makeText(this, "Permission denied. Cannot show location.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2. שם ה-Layout תואם לקובץ (activity_maps.xml)
        setContentView(R.layout.activity_maps);

        // אתחול ספק שירותי המיקום
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // אתחול ה-Fragment של המפה
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // הגדרת האזנה א-סינכרונית. הפונקציה onMapReady תיקרא כשהמפה תהיה מוכנה.
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * פונקציה זו נקראת אוטומטית כשהמפה מוכנה לשימוש.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // לאחר שהמפה מוכנה, נבקש הרשאה ונקבל מיקום
        checkLocationPermissionAndGetLocation();
    }

    /**
     * בודק אם יש הרשאת מיקום. אם לא, מבקש אותה.
     */
    private void checkLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // אם יש הרשאה, קבל מיקום
            getCurrentLocation();
        } else {
            // אם אין הרשאה, בקש אותה
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * מקבל את המיקום הנוכחי (או האחרון הידוע) ומעדכן את המפה.
     */
    private void getCurrentLocation() {
        // בגלל שבדקנו הרשאה קודם, כאן בטוח להמשיך
        // (הבדיקה הכפולה היא דרישה של אנדרואיד)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // אם הגענו לכאן בטעות בלי הרשאה, צא
            return;
        }

        // מפעיל את הנקודה הכחולה של "המיקום שלי" על המפה
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // בקשת המיקום האחרון הידוע מהמכשיר
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // ייתכן שהמיקום יהיה null אם המכשיר חדש או שירותי המיקום כבויים
                        if (location != null) {
                            // מצאנו מיקום!
                            // 1. יצירת אובייקט LatLng (קו רוחב וקו אורך)
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            // 2. הוספת סמן (מרקר) על המפה
                            mMap.addMarker(new MarkerOptions()
                                    .position(currentLatLng)
                                    .title("המיקום הנוכחי שלי"));

                            // 3. הזזת המצלמה להתמקד במיקום שלנו עם זום של 15
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                        } else {
                            // 3. שיניתי את השורה האדומה שהייתה לך קודם
                            Toast.makeText(MapsActivity.this, "Cannot get location. Make sure location is on.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}