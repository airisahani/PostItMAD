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
import androidx.navigation.Navigation;

import com.example.artownmad.LogIn;
import com.example.artownmad.R;
import com.example.artownmad.SignUp;
import com.example.artownmad.UserProfile;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons and set click listeners
        Button BtnLogIn = findViewById(R.id.BtnLogin);
        BtnLogIn.setOnClickListener(v -> {
            // Navigate to the login fragment
            Navigation.findNavController(this, R.id.nav).navigate(R.id.LogInLayout);
        });

        Button BtnSignUp = findViewById(R.id.BtnRegister);
        BtnSignUp.setOnClickListener(v -> {
            // Navigate to the signup fragment
            Navigation.findNavController(this, R.id.nav).navigate(R.id.SignUpLayout);
        });

    }
}