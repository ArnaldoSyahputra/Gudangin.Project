package com.kunthu.gudangin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class reset extends AppCompatActivity {

    private EditText passwordEmail;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);  passwordEmail = findViewById(R.id.editTextpw);
        Button resetPassword = findViewById(R.id.buttonpwreset);
        firebaseAuth = FirebaseAuth.getInstance();


        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String useremail = passwordEmail.getText().toString().trim();

                if(useremail.equals("")){
                    Toast.makeText(reset.this,"enter your registered id",Toast.
                            LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(useremail).
                            addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(reset.this,
                                                "sent!",Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(
                                                reset.this, login.class));
                                    }
                                    else {
                                        Toast.makeText(reset.this,
                                                "error!",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }

            }
        });
    }
}

