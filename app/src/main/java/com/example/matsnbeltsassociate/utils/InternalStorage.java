package com.example.matsnbeltsassociate.utils;

import android.content.Context;
import android.util.Log;

import com.example.matsnbeltsassociate.model.Associate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.example.matsnbeltsassociate.utils.CommonUtils.*;

public final class InternalStorage{
    private static final String TAG = "InternalStorageLog";
    private InternalStorage() {}

    public static boolean isFileNotPresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return !file.exists();
    }

    public static boolean checkCacheFileForToday(Context context, final String key) {
        String current_date = todayString();
        if(InternalStorage.isFileNotPresent(context, key)) {
            return false;
        }
        try {
            Associate associate = (Associate) readObject(context, key);
            Log.i(TAG, associate.toString());
            if(associate.getToday() == null || !dateToString(associate.getToday()).equalsIgnoreCase(current_date)) {
                context.deleteFile(key);
                return false;
            }
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            Log.i(TAG, e.getMessage());
            return false;
        }
        return true;
    }
    static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        ois.close();
        fis.close();
        return object;
    }
}