package com.example.matsnbeltsassociate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matsnbeltsassociate.R;


public class NetworkConnectivity extends AppCompatActivity {
    public static String phoneAuth = "PhoneAuth";
    public static String main = "Main";
    private boolean doubleBackToExitPressedOnce = false;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connectivity);
        intent = getIntent();
        final String className = intent.getStringExtra(LauncherActivity.EXTRA_MESSAGE);

        findViewById(R.id.network_msg_label);
        ImageButton refreshButton = findViewById(R.id.network_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PhoneAuthActivity.isNetworkAvailable(getApplicationContext())) {
                    Intent calledIntent = null;
                    if(className.equalsIgnoreCase(phoneAuth)) {
                        calledIntent = new Intent(getApplicationContext(), PhoneAuthActivity.class);
                    }
                    else if(className.equalsIgnoreCase(main)) {
                        calledIntent = new Intent(getApplicationContext(), LauncherActivity.class);
                        calledIntent.putExtra(LauncherActivity.ASSOCIATE_ID, intent.getStringExtra(LauncherActivity.ASSOCIATE_ID));
                    }
                    startActivity(calledIntent);
                    finish();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(this, CloseAppActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
