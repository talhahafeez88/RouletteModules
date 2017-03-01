package com.dhcollator.roulettemodules.send_video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by KHAN on 1/30/2017.
 */

public class Helper {
    /**
     * Key used whenever you want to pass a Member to another activity
     */
    public static final String MEMBER_K = "MEMBER_K";
    /**
     * Key used whenever you want to pass a Page Id to another activity
     */
    public static final String PAGEID_K = "PAGEID_K";
    /**
     * Key for saving the X coordinate of the location for the new Dialog Fragment
     */
    public static final String DF_LOC_X_KEY = "xOrig";
    /**
     * Key for saving the Y coordinate of the location for the new Dialog Fragment
     */
    public static final String DF_LOC_Y_KEY = "yOrig";

    /**
     * Key for saving the Y coordinate of the location for the new Dialog Fragment
     */
    public static final float GENERAL_POP_UP_SIZE = 2.00f;
    public static final float SMALL_POP_UP_SIZE = 1.75f;
    public static String BOOK_ID = "00000000";
    public static final String EVENT_ID = "00000000";// TODO: 2/6/2017 Event Id is related to server

    public static String getCurrentTime() {
        String currentDateTimeString = "-1";
        try {
            currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).format(new Date());
//            currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return currentDateTimeString;
    }

    public static int getScreenWidth(Activity pContext, int divider) {
        Display display = pContext.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        return p.x / divider;
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }


    public static int getScreenWidth(Activity pContext) {
        return getScreenWidth(pContext, 1);
    }

    public static int getOneHalfScreenWidth(Activity pContext) {
        return getScreenWidth(pContext, 2);
    }

    public static int getOneThirdScreenWidth(Activity pContext) {
        return getScreenWidth(pContext, 3);
    }

    /**
     * @return Current time with specified format
     */
    public static String parsedTimeStamp(String format) {
        try {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            Date date = originalFormat.parse(format);
            String formattedDate = targetFormat.format(date);
//            DebugLog.console("[Helper]parsedTimeStamp:" + formattedDate);
            return formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float getDimen(Context context, int dimenResId) {
        return context.getResources().getDimension(dimenResId);
    }

    //    /**
//     * Converts timestamp from server to integer timestamp
//     *
//     * @param serverTimestamp timestamp from server "2016-11-8T12:30:15.000-07:00"
//     * @return
//     */
//    public static long getTimestamp(String serverTimestamp) {
//        try {
//            DateFormat originalFormat = new SimpleDateFormat(FittractionApp.dateFormat);
////            DateFormat targetFormat = new SimpleDateFormat("MMM dd HH:mm a");
//            Date date = originalFormat.parse(serverTimestamp);
//
//            return date.getTime();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
    public static double hypo(double height, double base) {
        return Math.sqrt(Math.pow(height, 2) + Math.pow(base, 2));
    }
}
