package com.example.matsnbeltsassociate.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommonUtils {
    public static Calendar today() {
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate.setTimeZone(TimeZone.getTimeZone("IST"));
        //return currentDate.format(new Date());
        return Calendar.getInstance();
    }

    public static String todayString() {
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate.setTimeZone(TimeZone.getTimeZone("IST"));
        return currentDate.format(new Date());
    }

    public static String dateToString(Date date) {
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate.setTimeZone(TimeZone.getTimeZone("IST"));
        return currentDate.format(date);
    }
}
