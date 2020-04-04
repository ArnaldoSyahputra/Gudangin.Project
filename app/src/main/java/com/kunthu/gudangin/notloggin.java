package com.kunthu.gudangin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class notloggin extends Fragment {


    public notloggin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notloggin, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Account");

        Button buttonlog = view.findViewById(R.id.loginfirst);
        Button buttonreg = view.findViewById(R.id.regisfirst);

        buttonlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentl = new Intent(getContext(),login.class);
                startActivity(intentl);
            }
        });

        buttonreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentr = new Intent(getContext(),register.class);
                startActivity(intentr);
            }
        });


        return view;
    }

}
