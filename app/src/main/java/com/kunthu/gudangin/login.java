package com.kunthu.gudangin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private TextView tvRegister;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private TextView forgotpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        tvRegister = (TextView)findViewById(R.id.regis);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(login.this,
                        register.class);
                startActivity(loginIntent);
            }
        });
        final TextInputEditText emailEt = findViewById(R.id.email_ed);
        final TextInputEditText passwordEt = findViewById(R.id.password_ed);
        Button btnLogin = findViewById(R.id.btnLogin);
        forgotpw = findViewById(R.id.resetpw);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(emailEt.getText().toString())) {
                    Toast.makeText(login.this, "Email kosong",
                            Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passwordEt.getText().toString())) {
                    Toast.makeText(login.this, "Password kosong",
                            Toast.LENGTH_SHORT).show();
                } else {

                    String email = emailEt.getText().toString();
                    String password = passwordEt.getText().toString();

                    LoginAccount(email, password);
                }
            }
        });


        forgotpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, reset.class));
            }
        });
    }

    private void LoginAccount(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Mohon tunggu sebentar...");
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent mainInten = new Intent(login.this, MainActivity.class);
                                mainInten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainInten);
                                finish();
                            } else {
                                Toast.makeText(login.this, "Gagal login, coba lagi", Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    //Auth User
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cuurentUser = mAuth.getCurrentUser();
        if (cuurentUser != null){
            String currentUserId = mAuth.getCurrentUser().getUid();

            Intent mainInten = new Intent(login.this, MainActivity.class);
            mainInten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainInten);
            finish();


        }
    }
}