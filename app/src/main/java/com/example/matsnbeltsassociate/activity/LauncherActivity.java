package com.example.matsnbeltsassociate.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matsnbeltsassociate.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LauncherActivity extends AppCompatActivity {
    public static final String fileName = "matsnbelts";
    public static final String EXTRA_MESSAGE = "com.example.matsnbelts.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getApplicationContext().deleteFile(fileName);
        //writeUserNametoLocalFile("9952150922");
        String uuId = readFile();
        if (!uuId.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_MESSAGE, uuId);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, PhoneAuthActivity.class);
            startActivity(intent);
        }
    }
    private void writeUserNametoLocalFile(String mobile) {
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(LauncherActivity.fileName, Context.MODE_PRIVATE);
            Log.i("WriteFileLogin", "Writing in : " + LauncherActivity.fileName);
            outputStream.write((mobile).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isFilePresent(String fileName) {
        String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    private String readFile() {
        String temp="";
        if(!isFilePresent(fileName)) {
            return temp;
        }
        FileInputStream inputStream;
        try {
            inputStream = openFileInput(fileName);
            int c;
            while( (c = inputStream.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            System.out.println(";;;;;;;;;;" + temp);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return temp;
        }
    }
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
