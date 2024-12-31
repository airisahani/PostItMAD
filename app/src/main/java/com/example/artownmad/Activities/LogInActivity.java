package com.example.artownmad.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.artownmad.HomeFragment;
import com.example.artownmad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private EditText ETEmail2, ETPassword;
    private Button BtnLogin2, BtnForgot;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LogIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        ETEmail2 = findViewById(R.id.ETEmail2);
        ETPassword = findViewById(R.id.ETPassword);
        progressBar = findViewById(R.id.progressBar2);

        authProfile = FirebaseAuth.getInstance();

        Button BtnForgot = findViewById(R.id.BtnForgot);
        BtnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LogInActivity.this, "You can reset your password now",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LogInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button BtnLogin2 = findViewById(R.id.BtnLogin2);
        BtnLogin2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String textEmail = ETEmail2.getText().toString();
                String textPassword = ETPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LogInActivity.this, "Please enter your e-mail", Toast.LENGTH_SHORT).show();
                    ETEmail2.setError("E-mail is required");
                    ETEmail2.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LogInActivity.this, "Please re-enter your e-mail", Toast.LENGTH_SHORT).show();
                    ETEmail2.setError("Valid e-mail is required");
                    ETEmail2.requestFocus();
                } else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(LogInActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    ETPassword.setError("Password is required");
                    ETPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    logInUser(textEmail, textPassword);
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void logInUser (String email, String password){
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LogInActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();

                    //Get instance of the current user
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    /*//Check if e-mail is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(getContext(), "You are logged in now", Toast.LENGTH_SHORT).show();
                        //Open User Profile
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); //Sing out user
                        showAlertDialog();
                    }*/

                } else {
                    try{
                        throw task.getException();
                    } catch(FirebaseAuthInvalidUserException e){
                        ETEmail2.setError("User does not exists or is no longer valid. Please register again.");
                        ETEmail2.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e){
                        ETEmail2.setError("Invalid credentials. Kindly, check and re-enter.");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /*private void showAlertDialog() {
        //Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("E-mail is not verified");
        builder.setMessage("Please verify your e-mail now. You cannot log in without e-mail verification.");

        //Open E-mail app if users click Continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To e-mail app in new window
                startActivity(intent);
            }
        });

        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        //Show the AlertDialog
        alertDialog.show();
    }*/

}