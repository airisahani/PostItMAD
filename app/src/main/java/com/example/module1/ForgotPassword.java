package com.example.module1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPassword extends Fragment {

    private EditText ETEmailForgot;
    private Button BtnResetPassword;
    private ProgressBar progressBar4;
    private FirebaseAuth authProfile;
    private final static String TAG = "ForgotPassword";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForgotPassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPassword.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPassword newInstance(String param1, String param2) {
        ForgotPassword fragment = new ForgotPassword();
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
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        ETEmailForgot = view.findViewById(R.id.ETEmailForgot);
        BtnResetPassword = view.findViewById(R.id.BtnResetPassword);
        progressBar4 = view.findViewById(R.id.progressBar4);

        BtnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ETEmailForgot.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getContext(), "Please enter your registered e-mail", Toast.LENGTH_SHORT).show();
                    ETEmailForgot.setError("E-mail is required");
                    ETEmailForgot.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(getContext(), "Please enter a valid e-mail", Toast.LENGTH_SHORT).show();
                    ETEmailForgot.setError("Valid e-mail is required");
                    ETEmailForgot.requestFocus();
                } else {
                    progressBar4.setVisibility(View.VISIBLE);
                    resetPassword(email);
                    Navigation.findNavController(view).navigate(R.id.splash);
                }
            }
        });

        return view;
    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        ETEmailForgot.setError("User does not exists or is no longer valid. Kindly register again");
                    } catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}