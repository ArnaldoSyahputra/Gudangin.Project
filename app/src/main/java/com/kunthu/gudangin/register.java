package com.kunthu.gudangin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register extends AppCompatActivity {

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultDataReference;
    private String valid_email;
    private TextInputEditText email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initilizeUI();

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);


        final TextInputEditText fullname = findViewById(R.id.fullname);
        final TextInputEditText identitas = findViewById(R.id.noIdentitas);
        final TextInputEditText email = findViewById(R.id.email);
        final TextInputEditText pw = findViewById(R.id.password);
        final TextInputEditText noTelepon = findViewById(R.id.noTelepon);

        Button register = findViewById(R.id.btn_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fullname.getText().toString())) {
                    Toast.makeText(register.this, "Nama Lengkap kosong",
                            Toast.LENGTH_SHORT).show();
                }  else if (TextUtils.isEmpty(identitas.getText().toString())) {
                    Toast.makeText(register.this, "No Identitas Kosong",
                            Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(noTelepon.getText().toString())) {
                    Toast.makeText(register.this, "Nomor telepon kosong",
                            Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(register.this, "Email kosong",
                            Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(pw.getText().toString())) {
                    Toast.makeText(register.this, "Password kosong",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    String fullnameStr = fullname.getText().toString();
                    String shortNameStr = identitas.getText().toString();
                    String emailStr = email.getText().toString();
                    String pass = pw.getText().toString();
                    String telepon = noTelepon.getText().toString();

                    RegisterAccount(fullnameStr, shortNameStr, emailStr, pass, telepon );

                }
            }
        });
    }

    private void initilizeUI() {
        // TODO Auto-generated method stub

        email = (TextInputEditText) findViewById(R.id.email);

        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                // TODO Auto-generated method stub
                Is_Valid_Email(email); // pass your EditText Obj here.
            }

            public void Is_Valid_Email(TextInputEditText edt) {
                if (!isEmailValid(edt.getText().toString())) {
                    edt.setError("Format Email Salah");
                    valid_email = null;
                } else {
                    valid_email = edt.getText().toString();
                }
            }

            boolean isEmailValid(CharSequence email) {
                String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email);
                return matcher.matches();
            } // end of TextWatcher (email)
        });

    }


    private void RegisterAccount(final String fullnameStr, final String shortNameStr, final String emailStr, String pass, final String telepon) {
        loadingBar.setTitle("Membuat Akun Baru");
        loadingBar.setMessage("Mohon tunggu sebentar...");
        loadingBar.show();
        mAuth.createUserWithEmailAndPassword(emailStr, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            String current_user_id = mAuth.getCurrentUser().getUid();
                            storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("User").child(current_user_id);
                            storeUserDefaultDataReference.child("email").setValue(emailStr);
                            storeUserDefaultDataReference.child("fullname").setValue(fullnameStr);
                            storeUserDefaultDataReference.child("verif").setValue("belum");
                            storeUserDefaultDataReference.child("buktiidentitas").setValue("belum");
                            storeUserDefaultDataReference.child("identitas").setValue(shortNameStr);
                            storeUserDefaultDataReference.child("noTelepon").setValue(telepon)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Intent mainInten = new Intent(register.this, MainActivity.class);
                                            startActivity(mainInten);


                                        }
                                    });

                        } else {
                            Toast.makeText(register.this, "Terjadi Kesalahan, coba lagi..", Toast.LENGTH_LONG).show();
                        }
                        loadingBar.dismiss();

                    }
                });

    }
}