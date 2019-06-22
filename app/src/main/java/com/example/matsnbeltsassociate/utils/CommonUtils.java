package com.example.matsnbeltsassociate.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommonUtils {
    public static String today() {
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate.setTimeZone(TimeZone.getTimeZone("IST"));
        return currentDate.format(new Date());
    }
}
