package com.example.artownmad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CallFragment extends Fragment {
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.call_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvCallTitle = view.findViewById(R.id.TVCallTitle);
        Button btnPolice = view.findViewById(R.id.BtnPolice);
        Button btnAmbulance = view.findViewById(R.id.BtnAmbulance);
        Button btnFireDep = view.findViewById(R.id.BtnFireDep);

        btnPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvCallTitle.setText("Calling Police");
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:111"));
                startActivity(intent);

            }
        });
        btnAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCallTitle.setText("Calling Ambulance");
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:112"));
                startActivity(intent);
            }
        });
        btnFireDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCallTitle.setText("Calling Fire Department");
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:113"));
                startActivity(intent);
            }
        });
    }

}
