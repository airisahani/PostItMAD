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

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap myMap;
    private SearchView searchView;
    private final HashMap<String, String> markerDetailsMap = new HashMap<>();

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        searchView = view.findViewById(R.id.search_location);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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

        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setZoomGesturesEnabled(true);
        myMap.getUiSettings().setAllGesturesEnabled(true);

        LatLng UM = new LatLng(3.1219, 101.6570);
        myMap.addMarker(new MarkerOptions().position(UM).title("University of Malaya"));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UM, 12f));

        // Set marker click listener
        myMap.setOnMarkerClickListener(marker -> {
            String details = markerDetailsMap.get(marker.getId());
            if (details != null) {
                showDetailsDialog(marker.getTitle(), details);
            }
            return false; // Return false to keep default behavior
        });
    }

    private void searchLocation(String location) {
        if (location == null || location.isEmpty()) return;

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(location, 1);
            if (addressList == null || addressList.isEmpty()) return;

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            myMap.clear();

            myMap.addMarker(new MarkerOptions().position(latLng).title(location));
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));

            addLegendMarkers(latLng);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addLegendMarkers(LatLng center) {
        Random random = new Random();
        String[] crimeTypes = {"Theft", "Assault", "Robbery", "Vandalism"};
        String[] actionsTaken = {"Investigation ongoing", "Suspect arrested", "Case closed"};

        int reportedCrimeCount = random.nextInt(10) + 1;
        int policeStationCount = random.nextInt(5) + 1;
        int hospitalCount = random.nextInt(3) + 1;
        int fireStationCount = random.nextInt(3) + 1;

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        // Reported Crime Markers
        for (int i = 0; i < reportedCrimeCount; i++) {
            LatLng crimeLocation = getRandomNearbyLocation(center);
            String details = generateCrimeDetails(crimeTypes, actionsTaken);

            addMarkerWithDetails(crimeLocation, "Reported Crime", BitmapDescriptorFactory.HUE_VIOLET, details);
        }

        // Police Station Markers
        addLocationMarkers(center, policeStationCount, "Police Station", BitmapDescriptorFactory.HUE_BLUE, geocoder);

        // Hospital Markers
        addLocationMarkers(center, hospitalCount, "Hospital", BitmapDescriptorFactory.HUE_GREEN, geocoder);

        // Fire Station Markers
        addLocationMarkers(center, fireStationCount, "Fire Station", BitmapDescriptorFactory.HUE_ORANGE, geocoder);
    }

    private void addLocationMarkers(LatLng center, int count, String title, float color, Geocoder geocoder) {
        for (int i = 0; i < count; i++) {
            LatLng location = getRandomNearbyLocation(center);
            String address = getAddressFromGeocoder(location, geocoder);
            String phone = generateRandomPhoneNumber();
            String details = "Address: " + address + "\nHours: 24 hours\nPhone: " + phone;

            addMarkerWithDetails(location, title, color, details);
        }
    }

    private String getAddressFromGeocoder(LatLng location, Geocoder geocoder) {
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown address";
    }

    private void addMarkerWithDetails(LatLng location, String title, float color, String details) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(color));

        Marker marker = myMap.addMarker(markerOptions);
        if (marker != null) {
            markerDetailsMap.put(marker.getId(), details);
        }
    }

    private String generateCrimeDetails(String[] crimeTypes, String[] actionsTaken) {
        Random random = new Random();
        String date = generateRandomDate();
        String crimeType = crimeTypes[random.nextInt(crimeTypes.length)];
        String action = actionsTaken[random.nextInt(actionsTaken.length)];
        String notes = generateRandomCrimeNote();

        return "Date: " + date + "\nType: " + crimeType + "\nAction: " + action + "\nNotes: " + notes;
    }

    private LatLng getRandomNearbyLocation(LatLng center) {
        double offsetLat = (Math.random() - 0.5) * 0.05;
        double offsetLng = (Math.random() - 0.5) * 0.05;
        return new LatLng(center.latitude + offsetLat, center.longitude + offsetLng);
    }

    private String generateRandomDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        long now = System.currentTimeMillis();
        long randomDateOffset = (long) (Math.random() * 30 * 24 * 60 * 60 * 1000); // 30 days
        return sdf.format(new Date(now - randomDateOffset));
    }

    private String generateRandomCrimeNote() {
        String[] notes = {
                "Witnesses are being interviewed.",
                "Security footage is being reviewed.",
                "Awaiting forensic results.",
                "Suspect has been identified.",
                "Case referred to the district office."
        };
        return notes[new Random().nextInt(notes.length)];
    }

    private String generateRandomPhoneNumber() {
        Random random = new Random();
        int number = random.nextInt(90000000) + 10000000; // Ensures 8 digits
        return "03-" + number;
    }

    private void showDetailsDialog(String title, String details) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }
}










