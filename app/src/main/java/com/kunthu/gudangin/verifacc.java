package com.kunthu.gudangin;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class verifacc extends Fragment {

    private Button logout, upload, ambilGambar, ambilGambar2;
    private TextView nama, noIdentitas, textView, tvUnggah, tvIdentitas;
    private DatabaseReference userRef;
    private static final int Gallery_Pick = 1;
    private static final int Gallery_Pick2 = 2;
    private DatabaseReference dbRef;
    private String ref;
    private ProgressDialog loadingBar;
    private Uri ImageUri, ImageUri2;
    private String gambarStr, gambarStr2;
    private ImageView gambarIdentitas, gambarIdentitas2;
    private StorageReference PostsImageReference;
    private String saveCurrentDate, saveCurrentTime, postRandomName;
    private FirebaseAuth mAuth;


    public verifacc() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myMainView;
        myMainView = inflater.inflate(R.layout.fragment_verifacc, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Account");

        loadingBar = new ProgressDialog(getContext());
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        PostsImageReference = FirebaseStorage.getInstance().getReference();
        dbRef = FirebaseDatabase.getInstance().getReference().child("User").child(userID);
        logout = myMainView.findViewById(R.id.logout);
        upload = myMainView.findViewById(R.id.upload);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        nama = myMainView.findViewById(R.id.nama);
        noIdentitas = myMainView.findViewById(R.id.noIdentitas);
        textView = myMainView.findViewById(R.id.verif);
        gambarIdentitas = myMainView.findViewById(R.id.gambarid);
        ambilGambar = myMainView.findViewById(R.id.ambil);
        gambarIdentitas2 = myMainView.findViewById(R.id.gambarid2);
        ambilGambar2 = myMainView.findViewById(R.id.ambil2);

        upload.setVisibility(View.INVISIBLE);
//        tvUnggah.setVisibility(View.INVISIBLE);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoringImageToFirebaseStorage();
            }
        });


        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Keluar dari akun");
                builder.setMessage(" Apakah anda yakin ingin keluar?");
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

        ambilGambar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery2();
            }
        });

        ambilGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String username = dataSnapshot.child("fullname").getValue().toString();
                    nama.setText(username);
                    String identitas = dataSnapshot.child("identitas").getValue().toString();
                    noIdentitas.setText(identitas);
                    String verif = dataSnapshot.child("verif").getValue().toString();
                    String bukti = dataSnapshot.child("buktiidentitas").getValue().toString();
                    if(bukti.equals("belum")){
                        ambilGambar.setVisibility(View.VISIBLE);
                        gambarIdentitas.setVisibility(View.VISIBLE);
                        ambilGambar2.setVisibility(View.VISIBLE);
                        gambarIdentitas2.setVisibility(View.VISIBLE);
//                        textView.setText("Akun anda belum terverifikasi, silahkan upload bukti identitas sesuai dengan nomor identitas saat melakukan registrasi.");
                    }

                    else if(bukti.equals("ubah")){
                        ambilGambar.setVisibility(View.VISIBLE);
                        gambarIdentitas.setVisibility(View.VISIBLE);
                        ambilGambar2.setVisibility(View.VISIBLE);
                        gambarIdentitas2.setVisibility(View.VISIBLE);
//                        textView.setText("Maaf bukti identitas anda tidak sesuai atau gambar rusak, silahkan unggah ulang!");
                    }
                    else {
                        upload.setVisibility(View.INVISIBLE);
                        ambilGambar.setVisibility(View.INVISIBLE);
                        gambarIdentitas.setVisibility(View.INVISIBLE);
                        ambilGambar2.setVisibility(View.INVISIBLE);
                        gambarIdentitas2.setVisibility(View.INVISIBLE);
//                        tvUnggah.setVisibility(View.INVISIBLE);
//                        tvIdentitas.setVisibility(View.INVISIBLE);
//                        textView.setText("Bukti identitas anda sudah di unggah, Silahkan tunggu. Apabila akun anda belum terverifikasi selama 2x24 jam, hubungi kami pada menu beranda.");
                    }
                    if(verif.equals("sudah")) {
                        textView.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return myMainView;
    }

    private void OpenGallery2() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick2);
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null){
            ImageUri = data.getData();

            gambarIdentitas.setImageURI(ImageUri);

            upload.setVisibility(View.VISIBLE);
//            tvUnggah.setVisibility(View.VISIBLE);

        }
        if(requestCode == Gallery_Pick2 && resultCode == RESULT_OK && data!=null){

            ImageUri2 = data.getData();

            gambarIdentitas2.setImageURI(ImageUri2);
            upload.setVisibility(View.VISIBLE);
//            tvUnggah.setVisibility(View.VISIBLE);

        }
    }

    private void StoringImageToFirebaseStorage() {
        loadingBar.setTitle("Upload Foto Ke Database");
        loadingBar.setMessage("Mohon tunggu sebentar...");
        loadingBar.show();
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());
        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());
        postRandomName = saveCurrentDate + saveCurrentTime;
        final StorageReference filepath = PostsImageReference.child("Bukti Identitas").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filepath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            gambarStr = uri.toString();

                            Toast.makeText(getContext(), "Berhasil Menggunggah!", Toast.LENGTH_SHORT).show();
                            dbRef.child("buktiidentitas").setValue(gambarStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Berhasil Memperbaharui", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            loadingBar.dismiss();
                        }
                    });
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(getContext(), "Error occured: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });

        final StorageReference filepath2 = PostsImageReference.child("Bukti Identitas2").child(ImageUri2.getLastPathSegment() + postRandomName + ".jpg");
        filepath2.putFile(ImageUri2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    filepath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            gambarStr2 = uri.toString();
                            Toast.makeText(getContext(), "Berhasil Menggunggah!", Toast.LENGTH_SHORT).show();

                            dbRef.child("buktiidentitas2").setValue(gambarStr2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Berhasil Memperbaharui", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            loadingBar.dismiss();
                        }
                    });
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(getContext(), "Error occured: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SavingPostInformationToDatabase() {



    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(getContext(), MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

}
