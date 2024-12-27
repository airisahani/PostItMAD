package com.example.artownmad.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.artownmad.AccountFragment;
import com.example.artownmad.HomeFragment;
import com.example.artownmad.MapFragment;
import com.example.artownmad.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;
import android.widget.Toast;
import android.Manifest;


public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "Post.It";
    private static final int RC_NOTIFICATION = 99;
    private static final String PREF_NAME = "ReportStatusPrefs";
    FloatingActionButton fab;
    public BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Request permission to allow notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, RC_NOTIFICATION);
        }

        createNotificationChannel();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.history) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            } else if (id == R.id.account) {
                replaceFragment(new AccountFragment());
            } else if (id == R.id.map){
                replaceFragment(new MapFragment());
                //Intent intent = new Intent(getApplicationContext(), MapFragment.class);
                //startActivity(intent);
            }
            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddReportActivity.class); // Create an Intent for the ReportActivity
                startActivity(intent); // Start the ReportActivity
            }
        });

        bottomNavigationView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CharSequence name = "Crime";
            String description = "Report status update";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // Create a notification channel with the given ID, name and importance
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);

                // Register the channel with the system
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


//        private void showNotification (String current){
//            Intent intent = new Intent(this, StatusUpdateActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.logo)
//                    .setContentTitle("Report " + current)
//                    .setContentText("The status of your report has been updated.")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent);
//
//            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            manager.notify(0, builder.build());
//        }

        public void onRequestPermissionResult( int requestCode, String[] permissions,
        int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == RC_NOTIFICATION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            }

        }

    private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.fragment_layout, fragment);
    fragmentTransaction.commit();
}

private void loadFragment(HomeFragment homeFragment) {
    }
}