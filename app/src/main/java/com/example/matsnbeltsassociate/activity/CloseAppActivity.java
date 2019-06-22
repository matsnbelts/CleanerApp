package com.example.matsnbeltsassociate.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matsnbeltsassociate.R;

public class CloseAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_app);
        finishAffinity();
    }
}
