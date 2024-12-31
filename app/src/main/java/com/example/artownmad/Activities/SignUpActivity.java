package com.example.artownmad.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.artownmad.R;
import com.example.artownmad.ReadWriteUserDetails;
import com.example.artownmad.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private EditText ETUsername2, ETFullName, ETEmail, ETBirthday, ETPassword2, ETConfirmPassword;
    private ProgressBar progressBar;
    private Button BtnSignUp;
    private static final String TAG = "SignUp";
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ETUsername2 = findViewById(R.id.ETUsername2);
        ETFullName = findViewById(R.id.ETFullName);
        ETEmail = findViewById(R.id.ETEmail);
        ETBirthday = findViewById(R.id.ETBirthday);
        ETPassword2 = findViewById(R.id.ETPassword2);
        ETConfirmPassword = findViewById(R.id.ETConfirmPassword);
        BtnSignUp = findViewById(R.id.BtnSignUp);
        progressBar = findViewById(R.id.progressBar);

        //Set up DatePicker on EditText
        ETBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //DatePicker Dialog
                picker = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day){
                        ETBirthday.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtain the entered data
                String textUsername = ETUsername2.getText().toString();
                String textFullName = ETFullName.getText().toString();
                String textEmail = ETEmail.getText().toString();
                String textBirthday = ETBirthday.getText().toString();
                String textPassword = ETPassword2.getText().toString();
                String textConfirmPassword = ETConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(textUsername)){
                    Toast.makeText(SignUpActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    ETUsername2.setError("Username is required");
                    ETUsername2.requestFocus();
                } else if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(SignUpActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    ETFullName.setError("Full name is required");
                    ETFullName.requestFocus();
                } else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(SignUpActivity.this, "Please enter your e-mail", Toast.LENGTH_SHORT).show();
                    ETEmail.setError("E-mail is required");
                    ETEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(SignUpActivity.this, "Please re-enter your e-mail", Toast.LENGTH_SHORT).show();
                    ETEmail.setError("Valid e-mail is required");
                    ETEmail.requestFocus();
                } else if(TextUtils.isEmpty(textBirthday)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your birth date", Toast.LENGTH_SHORT).show();
                    ETBirthday.setError("Birth date is required");
                    ETBirthday.requestFocus();
                } else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(SignUpActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    ETPassword2.setError("Password is required");
                    ETPassword2.requestFocus();
                } else if(textPassword.length() < 8){
                    Toast.makeText(SignUpActivity.this, "Password should be at least 8 characters", Toast.LENGTH_SHORT).show();
                    ETPassword2.setError("Password too weak");
                    ETPassword2.requestFocus();
                } else if(TextUtils.isEmpty(textConfirmPassword)){
                    Toast.makeText(SignUpActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    ETConfirmPassword.setError("Password confirmation is required");
                    ETConfirmPassword.requestFocus();
                } else if(!textPassword.equals(textConfirmPassword)){
                    Toast.makeText(SignUpActivity.this, "Password not matched", Toast.LENGTH_SHORT).show();
                    ETConfirmPassword.setError("Password confirmation is required");
                    ETConfirmPassword.requestFocus();
                    //Clear the entered password
                    ETPassword2.clearComposingText();
                    ETConfirmPassword.clearComposingText();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    signUp(textUsername, textFullName, textEmail, textBirthday, textPassword);
                }
            }
        });

    }

    private void signUp(String textUsername, String textFullName, String textEmail, String textBirthday, String textPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Create user profile
        auth.createUserWithEmailAndPassword(textEmail, textPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //Update display name of user
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textUsername).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
                            intent.putExtra("username", textUsername);
                            startActivity(intent);
                            finish();

                            progressBar.setVisibility(View.GONE);

                            //Enter user data into the Firebase Realtime Database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textEmail, textBirthday);

                            //Extracting user difference from Database for "Registered Users"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {
                                                //Send Verification E-mail
                                                firebaseUser.sendEmailVerification();
                                                Toast.makeText(SignUpActivity.this, "User registered successfully. Please verify your email.", Toast.LENGTH_SHORT).show();

                                /*//Open User Profile after successful registration
                                Intent intent = new Intent(getContext(), Welcome.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //to close SignUp*/
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "User register failed. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                ETPassword2.setError("Your password is weak. Kindly use mix of alphabets, numbers and special characters.");
                                ETPassword2.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                ETPassword2.setError("Your e-mail is invalid or already in use. Kindly re-enter.");
                                ETPassword2.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e){
                                ETPassword2.setError("User is already registered with this e-mail.");
                                ETPassword2.requestFocus();
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

}