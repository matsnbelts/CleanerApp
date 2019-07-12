package com.example.matsnbeltsassociate.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matsnbeltsassociate.R;
import com.example.matsnbeltsassociate.utils.InternalStorage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LauncherActivity extends AppCompatActivity {
    public static final String fileName = "matsnbelts";
    public static final String EXTRA_MESSAGE = "com.example.matsnbelts.MESSAGE";
    public static final String ASSOCIATE_ID = "AssociateId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
//        getApplicationContext().deleteFile(fileName);
//        writeUserNametoLocalFile("+919952150922");
        String uuId = readFile();
        if (!uuId.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_MESSAGE, uuId);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, PhoneAuthActivity.class);
            startActivity(intent);
        }
        finish();
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

    private String readFile() {
        StringBuilder temp= new StringBuilder("");
        if (InternalStorage.isFileNotPresent(getApplicationContext(), fileName)) {
            return temp.toString();
        }
        FileInputStream inputStream = null;
        try {
            inputStream = openFileInput(fileName);
            int c;
            while( (c = inputStream.read()) != -1){
                temp.append((char)c);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return temp.toString();
    }
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
