package com.kunthu.gudangin;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class inbox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Inbox");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;
    }
}
