package com.example.artownmad.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artownmad.Adapters.ReportAdapter;
import com.example.artownmad.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatusUpdate extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Reports> reports = new ArrayList<>();
   // private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_status_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewStatusUpdate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Fetch reports
        fetchReports();
    }

    private void fetchReports() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            CollectionReference reportsRef = fStore.collection("reports");
            //Query query = reportsRef.whereEqualTo("user", uid);
            Query query = reportsRef;


            query.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                                Reports report = document.toObject(Reports.class);
//                                reports.add(report);
                            Log.d("TAG", "Number of documents fetched: " + queryDocumentSnapshots.size());
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Reports report = document.toObject(Reports.class);
                                Log.d("TAG", "Fetched report: " + document.getData());
                                if ("pending".equalsIgnoreCase(report.getStatus())) {
                                    reports.add(report);}
                            }
                            Log.d("TAG", "Number of pending reports: " + reports.size());

                            // Set adapter for RecyclerView
                            ReportAdapter adapter = new ReportAdapter(StatusUpdate.this, reports);
                            recyclerView.setAdapter(adapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(StatusUpdate.this, "Error fetching reports", Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error fetching reports: ", e);
                        }
                    });
        } else {
            // Handle the case where the user is not signed in
            Toast.makeText(this, "User is not signed in", Toast.LENGTH_SHORT).show();
            Log.w("TAG", "User is not signed in");
        }
    }
}