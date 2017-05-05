package com.example.alleghenybus.Utils;




import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import java.util.*;

/**
 * Created by alabhyafarkiya on 05/05/17.
 */


public class Utils {


    /*public static String getCurrentBgUnit(){
        return "mg/dL"; //TODO get this value from Setting page
    }*/

    /*public static boolean isBGmgdlUnit(){
        return getCurrentBgUnit().equalsIgnoreCase("mg/dL");
    }*/

    /**
     * convert dp into pixel
     *
     * @param context
     * @param dpValue value in dp
     * @return value in pixel
     */
    public static float convertToPx(Context context, float dpValue) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (float) (dpValue * scale + 0.5f);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }


    public static boolean isApiGreaterThan10()
    {
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB;
    }
    public static boolean isApiGreaterThan20()
    {
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP;
    }
    public static boolean isApiGreaterThan15()
    {
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }
    public static void log(String tag,String msg){
        Log.e("Allegheny_Bus_App"+"_"+tag,msg);
    }

    public static String getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = "GMT"+(offsetInMillis >= 0 ? "+" : "-") + offset;

        return offset;
    }

    public static void openAppRating(Context context) {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+context.getPackageName()));
            context.startActivity(webIntent);
        }
    }
    public static void sendEmail(Context context, String[] to, String subject, String body)
    {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
    }

    public static int getRandomMs(int min , int max) {
        // Usually this can be a field rather than a method variable
        Random rand = new Random();


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;


    }
}
