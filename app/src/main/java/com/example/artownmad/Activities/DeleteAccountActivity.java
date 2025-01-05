package com.example.artownmad.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.artownmad.R;
import com.example.artownmad.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccountActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText ETEnterPass;
    private TextView TVStatus2;
    private Button BtnAuthenticate2, BtnDeleteAccount;
    private ProgressBar progressBar7;
    private String userPassword;
    private static final String TAG = "DeleteAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_account);

        ETEnterPass = findViewById(R.id.ETEnterPass);
        TVStatus2 = findViewById(R.id.TVStatus2);
        BtnAuthenticate2 = findViewById(R.id.BtnAuthenticate2);
        BtnDeleteAccount = findViewById(R.id.BtnDeleteAccount);
        progressBar7 = findViewById(R.id.progressBar7);

        //Disable DeleteAccount button
        BtnDeleteAccount.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(DeleteAccountActivity.this, "Something went wrong." +
                    " User details are not available at the moment", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteAccountActivity.this, UserProfile.class);
            startActivity(intent);
        } else {
            reAuthenticateUser(firebaseUser);
        }

    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        BtnAuthenticate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPassword = ETEnterPass.getText().toString();

                if(TextUtils.isEmpty(userPassword)){
                    Toast.makeText(DeleteAccountActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    ETEnterPass.setError("Please enter your current password to authenticate");
                    ETEnterPass.requestFocus();
                } else {
                    progressBar7.setVisibility(View.VISIBLE);

                    //Reauthenticate User now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar7.setVisibility(View.GONE);

                                //Disable EditText for password
                                ETEnterPass.setEnabled(false);

                                BtnAuthenticate2.setEnabled(false);
                                BtnDeleteAccount.setEnabled(true);

                                //Set TVStatus
                                TVStatus2.setText("You are authenticated!\nYou can delete your account now");
                                Toast.makeText(DeleteAccountActivity.this, "Password has been verified." +
                                        "You can delete your account now. Be careful, this action is irreversible", Toast.LENGTH_SHORT).show();

                                BtnDeleteAccount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });
                            } else {
                                try{
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar7.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void showAlertDialog() {
        //Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccountActivity.this);
        builder.setTitle("Delete Account and Related Data?");
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible.");

        //Open E-mail app if users click Continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(firebaseUser);
            }
        });

        //Return to UserProfile if user press cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DeleteAccountActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });

        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        //Show the AlertDialog
        alertDialog.show();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    deleteUserData();
                    authProfile.signOut();
                    Toast.makeText(DeleteAccountActivity.this, "User has been deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteAccountActivity.this, SplashActivity.class);
                    startActivity(intent);
                } else {
                    try{
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar7.setVisibility(View.GONE);
            }
        });
    }

    private void deleteUserData() {
        //Delete photo
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: Photo Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Delete data from Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: User Data Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}