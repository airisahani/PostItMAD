package com.example.artownmad.Activities;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.artownmad.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the SearchView
        searchView = findViewById(R.id.search_location);

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when the user submits a query
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable zoom controls and gestures
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        // Add marker in University of Malaya and move camera
        LatLng UM = new LatLng(3.1219, 101.6570);
        mMap.addMarker(new MarkerOptions().position(UM).title("University of Malaya"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UM, 12f));

        // Allow users to select a location by tapping on the map
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLocation = latLng;
        });
    }

    private void searchLocation(String location) {
        if (location == null || location.isEmpty()) return;

        // Create a background thread executor
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList;

            try {
                // Perform Geocoding in the background thread
                addressList = geocoder.getFromLocationName(location, 1);
                if (addressList == null || addressList.isEmpty()) {
                    // Post result back to the main thread
                    handler.post(() -> Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Extract the first address and get its LatLng
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                // Post result back to the main thread
                handler.post(() -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                });
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, "Error finding location", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (selectedLocation != null) {
            // Return the selected location to the calling activity
            Intent intent = new Intent();
            intent.putExtra("selected_location", "Lat: " + selectedLocation.latitude + ", Lng: " + selectedLocation.longitude);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
