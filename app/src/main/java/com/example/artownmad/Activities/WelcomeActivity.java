package com.example.artownmad.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.artownmad.HomeFragment;
import com.example.artownmad.R;

public class WelcomeActivity extends AppCompatActivity {

    Button BtnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        String username = getIntent().getStringExtra("username");

        // Find the TextView and set the welcome message
        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        if (username != null) {
            welcomeMessage.setText("Welcome, @" + username);
        } else {
            welcomeMessage.setText("Welcome to Post.it");
        }

        BtnContinue = findViewById(R.id.BtnContinue);
        BtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}