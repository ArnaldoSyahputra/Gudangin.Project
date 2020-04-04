package com.kunthu.gudangin;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class account extends Fragment {

    private View myMainView;
    private Button logout;
    private TextView nama, noIdentitas,noTelepon;
    private DatabaseReference userRef;
    private DatabaseReference dbRef;
    private String ref;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    public account() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myMainView = inflater.inflate(R.layout.fragment_account, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Account");

        loadingBar = new ProgressDialog(getContext());
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("User").child(userID);
        logout = myMainView.findViewById(R.id.logout);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        nama = myMainView.findViewById(R.id.nama);
        noIdentitas = myMainView.findViewById(R.id.noIdentitas);


        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Keluar dari akun");
                builder.setMessage("Apakah anda yakin ingin keluar?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadingBar.setTitle("Keluar Akun");
                        loadingBar.setMessage("Mohon tunggu sebentar...");
                        loadingBar.show();
                        mAuth.signOut();
                        SendUserToLoginActivity();
                    }
                })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.create().show();
            }
        });


        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("fullname").getValue().toString();
                nama.setText(username);
                String identitas = dataSnapshot.child("identitas").getValue().toString();
                noIdentitas.setText(identitas);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return myMainView;
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(getContext(), login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

}
