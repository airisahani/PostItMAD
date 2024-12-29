package com.example.artownmad;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePassword extends Fragment {

    private FirebaseAuth authProfile;
    private EditText ETCurrentPass, ETNewPass, ETConfirmPass;
    private TextView TVStatus;
    private Button BtnAuthenticate, BtnChangePass;
    private ProgressBar progressBar6;
    private String userPasswordCurrent;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangePassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePassword.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePassword newInstance(String param1, String param2) {
        ChangePassword fragment = new ChangePassword();
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
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        ETCurrentPass = view.findViewById(R.id.ETCurrentPass);
        ETNewPass = view.findViewById(R.id.ETNewPass);
        ETConfirmPass = view.findViewById(R.id.ETConfirmPass);
        TVStatus = view.findViewById(R.id.TVStatus);
        BtnAuthenticate = view.findViewById(R.id.BtnAuthenticate);
        BtnChangePass = view.findViewById(R.id.BtnChangePass);
        progressBar6 = view.findViewById(R.id.progressBar6);

        //Disable EditText for new password
        ETNewPass.setEnabled(false);
        ETConfirmPass.setEnabled(false);
        BtnChangePass.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.userProfile);
        } else {
            reAuthenticateUser(firebaseUser);
        }

        return view;
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        BtnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPasswordCurrent = ETCurrentPass.getText().toString();

                if(TextUtils.isEmpty(userPasswordCurrent)){
                    Toast.makeText(getContext(), "Password is needed", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), "Password has been verified" + "Change password now", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "New password is needed", Toast.LENGTH_SHORT).show();
            ETNewPass.setError("Please enter your new password");
            ETNewPass.requestFocus();
        } else if(TextUtils.isEmpty(userConfirmPasswordNew)){
            Toast.makeText(getContext(), "New password confirmation is needed", Toast.LENGTH_SHORT).show();
            ETConfirmPass.setError("Please confirm your new password");
            ETConfirmPass.requestFocus();
        } else if(!userPasswordNew.matches(userConfirmPasswordNew)){
            Toast.makeText(getContext(), "Password did not match", Toast.LENGTH_SHORT).show();
            ETConfirmPass.setError("Please re-enter the same password");
            ETConfirmPass.requestFocus();
        } else if(userPasswordCurrent.matches(userPasswordNew)){
            Toast.makeText(getContext(), "New password cannot be same as the old password", Toast.LENGTH_SHORT).show();
            ETNewPass.setError("Please enter a new password");
            ETNewPass.requestFocus();
        } else {
            progressBar6.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Password has been changed", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.userProfile);
                    } else {
                        try{
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar6.setVisibility(View.GONE);
                }
            });
        }
    }
}