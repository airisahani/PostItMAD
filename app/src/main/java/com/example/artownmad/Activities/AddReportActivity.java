package com.example.artownmad.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.artownmad.R;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import androidx.core.app.ActivityCompat;

public class AddReportActivity extends AppCompatActivity {
    private static final int IMAGE_PICK_CODE = 1000;
    private Uri selectedImageUri;
    private EditText Title_et, et_description, name2;
    private Spinner spinner_category;
    private Switch switch_anonymity;
    private Button btn_post_report;
    private ImageButton imageButton;
    private Button btn_add_location;

    private Button btn_add_current_location;
    private String locationString = "No location selected";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private LocationManager locationManager;

    private static final int MAP_REQUEST_CODE = 2000;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView tvSelectedLocation;

    private FirebaseFirestore db;
    private StorageReference storageRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_report);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Title2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize views
        Title_et = findViewById(R.id.Title_et);
        et_description = findViewById(R.id.et_description);
        name2 = findViewById(R.id.name2);
        spinner_category = findViewById(R.id.spinner_category);
        switch_anonymity = findViewById(R.id.switch_anonymity);
        btn_post_report = findViewById(R.id.btn_post_report);
        imageButton = findViewById(R.id.imageButton);
        btn_add_current_location= findViewById(R.id.btn_add_current_location);
        btn_add_location = findViewById(R.id.btn_add_location);

        // spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(adapter);
        spinner_category.setSelection(0); // Set default selection

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });

//anonymity switch
        btn_post_report.setOnClickListener(v -> {
            String title = Title_et.getText().toString().trim();
            String description = et_description.getText().toString().trim();
            String name = name2.getText().toString().trim();
            String category = spinner_category.getSelectedItem().toString();
            boolean isAnonymous = switch_anonymity.isChecked();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Title and description are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> reportData = new HashMap<>();
            reportData.put("title", title);
            reportData.put("description", description);
            reportData.put("name", isAnonymous ? "Anonymous" : name);
            reportData.put("category", category);
            reportData.put("location", locationString);
            reportData.put("timestamp", System.currentTimeMillis());

            if (selectedImageUri != null) {
                StorageReference imageRef = storageRef.child("reports/" + System.currentTimeMillis() + ".jpg");
                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    reportData.put("imageUrl", uri.toString());
                                    saveReportToFirestore(reportData);
                                })
                                .addOnFailureListener(e -> {
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                    Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }))
                        .addOnFailureListener(e -> {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                saveReportToFirestore(reportData);
            }
        });
        // location components

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btn_add_location.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            locationString = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                            Toast.makeText(this, "Location added: " + locationString, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        tvSelectedLocation = findViewById(R.id.tv_location);
        Button btnSelectLocation = findViewById(R.id.btn_add_location);
        Button btnAddCurrentLocation = findViewById(R.id.btn_add_current_location);

        // Open map to select location
        btnSelectLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivityForResult(intent, MAP_REQUEST_CODE);
        });
        // Add current location
        btnAddCurrentLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            locationString = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                            tvSelectedLocation.setText(locationString);
                        } else {
                            Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CODE) {
                selectedImageUri = data.getData();
                // Optionally, display the image in an ImageView or store it
            } else if (requestCode == MAP_REQUEST_CODE && data != null) {
                locationString = data.getStringExtra("selected_location");
                tvSelectedLocation.setText(locationString);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted. Please click 'Add Location' again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveReportToFirestore(Map<String, Object> reportData) {
        db.collection("reports")
                .add(reportData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Report posted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Toast.makeText(this, "Failed to post report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
