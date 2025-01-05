package com.example.artownmad.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.artownmad.R;
import com.example.artownmad.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText ETCurrentPass, ETNewPass, ETConfirmPass;
    private TextView TVStatus;
    private Button BtnAuthenticate, BtnChangePass;
    private ProgressBar progressBar6;
    private String userPasswordCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        ETCurrentPass = findViewById(R.id.ETCurrentPass);
        ETNewPass = findViewById(R.id.ETNewPass);
        ETConfirmPass = findViewById(R.id.ETConfirmPass);
        TVStatus = findViewById(R.id.TVStatus);
        BtnAuthenticate = findViewById(R.id.BtnAuthenticate);
        BtnChangePass = findViewById(R.id.BtnChangePass);
        progressBar6 = findViewById(R.id.progressBar6);

        //Disable EditText for new password
        ETNewPass.setEnabled(false);
        ETConfirmPass.setEnabled(false);
        BtnChangePass.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfile.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }

    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        BtnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPasswordCurrent = ETCurrentPass.getText().toString();

                if(TextUtils.isEmpty(userPasswordCurrent)){
                    Toast.makeText(ChangePasswordActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    ETCurrentPass.setError("Please enter your current password to authenticate");
                    ETCurrentPass.requestFocus();
                } else {
                    progressBar6.setVisibility(View.VISIBLE);

                    //Reauthenticate User now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPasswordCurrent);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar6.setVisibility(View.GONE);

                                //Disable EditText for current password
                                ETCurrentPass.setEnabled(false);
                                ETNewPass.setEnabled(true);
                                ETConfirmPass.setEnabled(true);

                                BtnAuthenticate.setEnabled(false);
                                BtnChangePass.setEnabled(true);

                                //Set TVStatus
                                TVStatus.setText("You are authenticated!\nYou can change your password now");
                                Toast.makeText(ChangePasswordActivity.this, "Password has been verified" + "Change password now", Toast.LENGTH_SHORT).show();

                                BtnChangePass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePassword(firebaseUser);
                                    }
                                });
                            } else {
                                try{
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar6.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser) {
        String userPasswordNew = ETNewPass.getText().toString();
        String userConfirmPasswordNew = ETConfirmPass.getText().toString();

        if(TextUtils.isEmpty(userPasswordNew)){
            Toast.makeText(ChangePasswordActivity.this, "New password is needed", Toast.LENGTH_SHORT).show();
            ETNewPass.setError("Please enter your new password");
            ETNewPass.requestFocus();
        } else if(TextUtils.isEmpty(userConfirmPasswordNew)){
            Toast.makeText(ChangePasswordActivity.this, "New password confirmation is needed", Toast.LENGTH_SHORT).show();
            ETConfirmPass.setError("Please confirm your new password");
            ETConfirmPass.requestFocus();
        } else if(!userPasswordNew.matches(userConfirmPasswordNew)){
            Toast.makeText(ChangePasswordActivity.this, "Password did not match", Toast.LENGTH_SHORT).show();
            ETConfirmPass.setError("Please re-enter the same password");
            ETConfirmPass.requestFocus();
        } else if(userPasswordCurrent.matches(userPasswordNew)){
            Toast.makeText(ChangePasswordActivity.this, "New password cannot be same as the old password", Toast.LENGTH_SHORT).show();
            ETNewPass.setError("Please enter a new password");
            ETNewPass.requestFocus();
        } else {
            progressBar6.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, UserProfile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try{
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar6.setVisibility(View.GONE);
                }
            });
        }
    }

}