package com.example.artownmad;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap myMap;
    private SearchView searchView;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the SearchView
        searchView = view.findViewById(R.id.search_location);

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

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // Enable zoom controls and gestures
        myMap.getUiSettings().setZoomControlsEnabled(true); // Adds the zoom buttons
        myMap.getUiSettings().setZoomGesturesEnabled(true); // Enables pinch-to-zoom gestures
        myMap.getUiSettings().setAllGesturesEnabled(true);  // Ensures all gestures are enabled

        // Add marker in Kuala Lumpur and move camera
        LatLng UM = new LatLng(3.1219, 101.6570);
        myMap.addMarker(new MarkerOptions().position(UM).title("University of Malaya"));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UM, 12f)); // Start with zoom level 12
    }

    private void searchLocation(String location) {
        if (location == null || location.isEmpty()) return;

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList;

        try {
            // Get list of addresses matching the search query
            addressList = geocoder.getFromLocationName(location, 1);
            if (addressList == null || addressList.isEmpty()) return;

            // Extract the first address and get its LatLng
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            // Add a marker at the searched location and move the camera
            myMap.clear(); // Clear previous markers
            myMap.addMarker(new MarkerOptions().position(latLng).title(location));
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
