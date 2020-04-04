package com.kunthu.gudangin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class add extends AppCompatActivity {

    EditText mTitleEt, mDescEt;
    ImageView mPostIv;

    Button mUploadBtn;

    String mStoragePath = "All_Image_Uploads/ ";
    String mDatabasePath = "Data";

    Uri mFilePathUri;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    ProgressDialog mProgressDialog;


    int IMAGE_REQUEST_CODE = 5;

    String cImage, cTitle, cDescr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Building");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mTitleEt = findViewById(R.id.pTitleEt);
        mDescEt = findViewById(R.id.pDescEt);
        mPostIv = findViewById(R.id.pImageIv);
        mUploadBtn = findViewById(R.id.pUploadBtn);



        Bundle intent = getIntent().getExtras();
        if (intent != null){

            cTitle = intent.getString("cTitle");
            cDescr = intent.getString("cDescr");
            cImage = intent.getString("cImage");

            mTitleEt.setText(cTitle);
            mDescEt.setText(cDescr);
            Picasso.get().load(cImage).into(mPostIv);

            actionBar.setTitle("Update Post");
            mUploadBtn.setText("Update");

        }

        mPostIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Image"), IMAGE_REQUEST_CODE);


            }
        });

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadBtn.getText().equals("upload")){

                    UploadDataToFirabase();

                }
                else {

                    beginUpdate();

                }



            }
        });

        mStorageReference = getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);

        mProgressDialog = new ProgressDialog(add.this);


    }

    private void beginUpdate() {

        mProgressDialog.setMessage("Updating ...");
        mProgressDialog.show();

        deletePreviousImage();

    }

    private void deletePreviousImage() {

        StorageReference mPictureRef = getInstance().getReferenceFromUrl(cImage);
        mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(add.this, "Previous image deleted!", Toast.LENGTH_SHORT).show();

                uploadNewImage();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(add.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();

            }
        });

    }

    private void uploadNewImage() {

        String imageName = System.currentTimeMillis() + ".png";

        StorageReference storageReference2 = mStorageReference.child(mStoragePath + imageName);

        Bitmap bitmap = ((BitmapDrawable)mPostIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] data = baos.toByteArray();

        UploadTask uploadTask = storageReference2.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(add.this, "New image uploaded!", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                updateDatabase(downloadUri.toString());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(add.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void updateDatabase(final String toString) {

        final String title = mTitleEt.getText().toString();
        final String descr = mDescEt.getText().toString();

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mFirebaseDatabase.getReference("Data");

        Query query = mRef.orderByChild("title").equalTo(cTitle);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().child("title").setValue(title);
                    ds.getRef().child("search").setValue(title.toLowerCase());
                    ds.getRef().child("description").setValue(descr);
                    ds.getRef().child("image").setValue(toString);
                }
                mProgressDialog.dismiss();
                Toast.makeText(add.this, "Database updated!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(add.this, MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UploadDataToFirabase() {

        if (mFilePathUri != null){

            mProgressDialog.setTitle("Uploading...");
            mProgressDialog.show();

            StorageReference storageReference2nd = mStorageReference.child(mStoragePath +
                    System.currentTimeMillis()+ "." + getFileExtension(mFilePathUri));

            storageReference2nd.putFile(mFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadUri = uriTask.getResult();

                            String mPostTitle = mTitleEt.getText().toString().trim();
                            String mPostDesc = mDescEt.getText().toString().trim();
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            String UID = mAuth.getCurrentUser().getUid();

                            mProgressDialog.dismiss();

                            Toast.makeText(add.this, "Image Uploaded!",
                                    Toast.LENGTH_SHORT).show();

                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(mPostTitle,
                                    mPostDesc, downloadUri.toString(), mPostTitle.toLowerCase(), UID);

                            String imageUploadId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            
                            mProgressDialog.dismiss();

                            Toast.makeText(add.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            mProgressDialog.setTitle("Uploading ...");

                        }
                    });
        }

        else {
            Toast.makeText(this, "Please select image or add image name ", Toast.LENGTH_SHORT).show();
        }


    }

    private String getFileExtension(Uri mFilePathUri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();



        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(mFilePathUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                data!= null && data.getData() != null){

            mFilePathUri = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilePathUri);

                mPostIv.setImageBitmap(bitmap);


            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;
    }
}
