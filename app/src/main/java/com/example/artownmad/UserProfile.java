package com.example.artownmad;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {

    private TextView ProfileName, UserFullName, UserEmail, UserBirthday;
    private ProgressBar progressBar3;
    private String username, fullname, email, birthday;
    private ImageView ProfilePic;
    private FirebaseAuth authProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfile newInstance(String param1, String param2) {
        UserProfile fragment = new UserProfile();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ProfileName = view.findViewById(R.id.ProfileName);
        UserFullName = view.findViewById(R.id.UserFullName);
        UserEmail = view.findViewById(R.id.UserEmail);
        UserBirthday = view.findViewById(R.id.UserBirthday);
        progressBar3 = view.findViewById(R.id.progressBar3);

        Button BtnChangePassword = view.findViewById(R.id.BtnChangePassword);
        BtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.changePassword);
            }
        });

        Button BtnDeleteAccount = view.findViewById(R.id.BtnDeleteAccount);
        BtnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.deleteAccount);
            }
        });

        //Set OnClickListener on ImageView
        ProfilePic = view.findViewById(R.id.ProfilePic);
        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.uploadImage);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(getContext(), "Something went wrong. User's details are not available at the moment.", Toast.LENGTH_LONG).show();
        } else {
            //checkifEmailVerified(firebaseUser);
            progressBar3.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        return view;
    }

    /*private void checkifEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
            //Set up the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("E-mail is not verified");
            builder.setMessage("Please verify your e-mail now. You cannot log in without e-mail verification next time.");

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

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting User Reference
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    username = firebaseUser.getDisplayName();
                    fullname = readUserDetails.fullname;
                    email = firebaseUser.getEmail();
                    birthday = readUserDetails.birthdate;

                    ProfileName.setText(username);
                    UserFullName.setText(fullname);
                    UserEmail.setText(email);
                    UserBirthday.setText(birthday);

                    //Set user DP after uploaded
                    Uri uri = firebaseUser.getPhotoUrl();
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
                progressBar3.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                progressBar3.setVisibility(View.GONE);
            }
        });
    }

}