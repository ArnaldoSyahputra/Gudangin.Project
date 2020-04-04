package com.kunthu.gudangin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

public class Detail extends AppCompatActivity {

    TextView mTitleTv, mDetailTv;
    ImageView mImageIv;

    Bitmap bitmap;

    Button mShareBtn;

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detail Post");


        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mTitleTv = findViewById(R.id.titleTv);
        mDetailTv = findViewById(R.id.descriptionTv);
        mImageIv = findViewById(R.id.imageView);
        mShareBtn = findViewById(R.id.shareBtn);

        String image = getIntent().getStringExtra("image");
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");

        mTitleTv.setText(title);
        mDetailTv.setText(desc);
        Picasso.get().load(image).into(mImageIv);




        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareImage();

            }
        });





    }




    private void shareImage() {

        try {

            bitmap = ((BitmapDrawable)mImageIv.getDrawable()).getBitmap();

            String s = mTitleTv.getText().toString() + "\n\n" + mDetailTv.getText().toString();

            File file = new File(getExternalCacheDir(), "sample.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, s);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share Via"));

        }catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;
    }


}
