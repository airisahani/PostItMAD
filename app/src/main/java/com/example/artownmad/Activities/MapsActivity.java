package com.example.artownmad.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.artownmad.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.gms.common.api.Status;

import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        Places.initialize(getApplicationContext(), getString(R.string.my_map_api_key));



        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up the search bar for location search
        setupSearchBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Allow users to select a location by tapping on the map
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLocation = latLng;
        });
    }

    private void setupSearchBar() {
        // Find the search bar in the layout
        TextInputEditText searchBar = findViewById(R.id.search_bar);

        // Set up a click listener to launch the Places Autocomplete widget
        searchBar.setOnClickListener(v -> {
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            ).build(this);
            startActivityForResult(intent, 1001);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if (resultCode == RESULT_OK && data != null) {
                // Get the selected place from the Autocomplete widget
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place.getLatLng() != null) {
                    LatLng latLng = place.getLatLng();

                    // Clear the map and add a marker for the selected location
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    // Save the selected location
                    selectedLocation = latLng;
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
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
