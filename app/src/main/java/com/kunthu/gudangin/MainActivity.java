package com.kunthu.gudangin;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private home homeFragment;
    private account accountFragment;
    private notloggin aboutFragment;
    private verifacc aboutLoginFragment;
    private account aboutVerifFragment;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        homeFragment = new home();
        accountFragment = new account();
        aboutFragment = new notloggin();
        aboutLoginFragment = new verifacc();
        aboutVerifFragment = new account();

        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        setFragment(homeFragment);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setFragment(homeFragment);
                        return true;

                    case R.id.navigation_add:
                        if (isConnected()) {
                            FirebaseUser cuurentUser = mAuth.getCurrentUser();
                            if (cuurentUser != null) {
                                String userId = mAuth.getCurrentUser().getUid();
                                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            String verif = dataSnapshot.child("verif").getValue().toString();
                                            if (verif.equals("sudah")){
                                                Intent intent = new Intent(MainActivity.this, add.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(MainActivity.this, "Tunggu verifikasi akun anda  dulu", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else {
                                Toast.makeText(MainActivity.this, "Silahkan masuk atau registrasi dulu!", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), "Terjadi kesalahan, cek koneksi internet anda!", Toast.LENGTH_SHORT).show();
                        }
                        return true;

                    case R.id.navigation_account:
                        if (isConnected()) {
                            FirebaseUser cuurentUser1 = mAuth.getCurrentUser();
                            if (cuurentUser1 != null) {
                                String userId = mAuth.getCurrentUser().getUid();
                                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            String verif = dataSnapshot.child("verif").getValue().toString();
                                            if (verif.equals("sudah")){
                                                setFragment (aboutVerifFragment);
                                            } else {
                                                setFragment (aboutLoginFragment);

                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else {
                                setFragment (aboutFragment);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Terjadi kesalahan, cek koneksi internet anda!", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                }
                return false;
            }
        };

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

}
