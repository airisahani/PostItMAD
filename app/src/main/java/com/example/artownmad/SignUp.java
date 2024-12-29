package com.example.artownmad;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUp extends Fragment {

    private EditText ETUsername2, ETFullName, ETEmail, ETBirthday, ETPassword2, ETConfirmPassword;
    private ProgressBar progressBar;
    private Button BtnSignUp;
    private static final String TAG = "SignUp";
    private DatePickerDialog picker;
    private EditText usernameE;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUp.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUp newInstance(String param1, String param2) {
        SignUp fragment = new SignUp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        ETUsername2 = view.findViewById(R.id.ETUsername2);
        ETFullName = view.findViewById(R.id.ETFullName);
        ETEmail = view.findViewById(R.id.ETEmail);
        ETBirthday = view.findViewById(R.id.ETBirthday);
        ETPassword2 = view.findViewById(R.id.ETPassword2);
        ETConfirmPassword = view.findViewById(R.id.ETConfirmPassword);
        BtnSignUp = view.findViewById(R.id.BtnSignUp);
        progressBar = view.findViewById(R.id.progressBar);

        //Set up DatePicker on EditText
        ETBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //DatePicker Dialog
                picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
                    Toast.makeText(getContext(), "Please enter your username", Toast.LENGTH_SHORT).show();
                    ETUsername2.setError("Username is required");
                    ETUsername2.requestFocus();
                } else if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(getContext(), "Please enter your full name", Toast.LENGTH_SHORT).show();
                    ETFullName.setError("Full name is required");
                    ETFullName.requestFocus();
                } else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(getContext(), "Please enter your e-mail", Toast.LENGTH_SHORT).show();
                    ETEmail.setError("E-mail is required");
                    ETEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(getContext(), "Please re-enter your e-mail", Toast.LENGTH_SHORT).show();
                    ETEmail.setError("Valid e-mail is required");
                    ETEmail.requestFocus();
                } else if(TextUtils.isEmpty(textBirthday)) {
                    Toast.makeText(getContext(), "Please enter your birth date", Toast.LENGTH_SHORT).show();
                    ETBirthday.setError("Birth date is required");
                    ETBirthday.requestFocus();
                } else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    ETPassword2.setError("Password is required");
                    ETPassword2.requestFocus();
                } else if(textPassword.length() < 8){
                    Toast.makeText(getContext(), "Password should be at least 8 characters", Toast.LENGTH_SHORT).show();
                    ETPassword2.setError("Password too weak");
                    ETPassword2.requestFocus();
                } else if(TextUtils.isEmpty(textConfirmPassword)){
                    Toast.makeText(getContext(), "Please confirm your password", Toast.LENGTH_SHORT).show();
                    ETConfirmPassword.setError("Password confirmation is required");
                    ETConfirmPassword.requestFocus();
                } else if(!textPassword.equals(textConfirmPassword)){
                    Toast.makeText(getContext(), "Password not matched", Toast.LENGTH_SHORT).show();
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

        return view;
    }

    //Register User using the credentials given
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

                    // Use NavController to navigate to WelcomeFragment
                    NavController navController = NavHostFragment.findNavController(SignUp.this);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", textUsername);

                    navController.navigate(R.id.action_signUp_to_welcome, bundle);

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
                                Toast.makeText(getContext(), "User registered successfully. Please verify your email.", Toast.LENGTH_SHORT).show();

                                /*//Open User Profile after successful registration
                                Intent intent = new Intent(getContext(), Welcome.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //to close SignUp*/
                            } else {
                                Toast.makeText(getContext(), "User register failed. Please try again.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}