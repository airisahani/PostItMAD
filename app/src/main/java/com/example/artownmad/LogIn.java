package com.example.artownmad;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogIn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogIn extends Fragment {

    private EditText ETEmail2, ETPassword;
    private Button BtnLogin2, BtnForgot;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LogIn";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LogIn() {
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
    public static LogIn newInstance(String param1, String param2) {
        LogIn fragment = new LogIn();
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
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        ETEmail2 = view.findViewById(R.id.ETEmail2);
        ETPassword = view.findViewById(R.id.ETPassword);
        progressBar = view.findViewById(R.id.progressBar2);

        authProfile = FirebaseAuth.getInstance();

        Button BtnForgot = view.findViewById(R.id.BtnForgot);
        BtnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You can reset your password now",Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.forgotPassword);            }
        });

        //Login User
        Button BtnLogin2 = view.findViewById(R.id.BtnLogin2);
        BtnLogin2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String textEmail = ETEmail2.getText().toString();
                String textPassword = ETPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(getContext(), "Please enter your e-mail", Toast.LENGTH_SHORT).show();
                    ETEmail2.setError("E-mail is required");
                    ETEmail2.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(getContext(), "Please re-enter your e-mail", Toast.LENGTH_SHORT).show();
                    ETEmail2.setError("Valid e-mail is required");
                    ETEmail2.requestFocus();
                } else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    ETPassword.setError("Password is required");
                    ETPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    logInUser(textEmail, textPassword);
                }
            }
        });

        return view;
    }

    private void logInUser (String email, String password){
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Navigation.findNavController(requireView()).navigate(R.id.userProfile);
                    Toast.makeText(getContext(), "You are logged in now", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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