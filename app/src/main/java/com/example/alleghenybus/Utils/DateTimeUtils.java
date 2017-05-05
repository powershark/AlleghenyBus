package com.example.alleghenybus.Utils;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for date-time related methods.
 */
public class DateTimeUtils {

    public static final long	ONE_DAY_MILLIS	= 24 * 60 * 60 * 1000;

    /**
     * Constants to be used in
     * {@link DateTimeUtils#getFormattedDate(long, String)} method.
     */
    public interface Format {
        String	DD_Mmm_YYYY			= "dd MMM yyyy";
        String	DD_Mmm_YYYY_Dow		= "dd MMM yyyy, E";
        String	DayWeek_DD_Mmm_YYYY	= "EEEE, dd MMM yyyy";
        String	day_of_week_3_chars	= "E";
        String	DD					= "dd";
        String	MMM					= "MMM";
        String	Dow					= "E";
        String	Mmmm_YYYY			= "MMMM yyyy";
        String	DD_Mmmm_YYYY_HH_MM	= "dd MMM yyy kk:mm";
        String	dd_mm_yyy	        = "dd/MM/yyyy";
    }

    /**
     * @param pEpochMillis
     * @param pFormat
     *            Possible values for pFormat are:
     *            <ul>
     *            <li>{@link DateTimeUtils.Format#DD_Mmm_YYYY_Dow}</li>
     *            <li>{@link DateTimeUtils.Format#day_of_week_3_chars}</li>
     *            </ul>
     * @return date string as per pEpochMillis, formatted as per pFormat.
     */
    public static String getFormattedDate(long pEpochMillis, String pFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pEpochMillis);
        //calendar.add(Calendar.HOUR_OF_DAY, -8);
        return DateFormat.format(pFormat, calendar).toString();
    }

    /**
     * @param pEpochMillis
     * @param pFormat
     *            Possible values for pFormat are:
     *            <ul>
     *            <li>{@link DateTimeUtils.Format#DD_Mmm_YYYY_Dow}</li>
     *            <li>{@link DateTimeUtils.Format#day_of_week_3_chars}</li>
     *            </ul>
     * @return date string as per calendar, formatted as per pFormat.
     */
    public static String getFormattedDate(Calendar calendar, String pFormat) {
        return DateFormat.format(pFormat, calendar).toString();
    }

    /**
     * @param pDaysOffset
     * @return mid-night epoch millis of start of today+pDaysOffset
     */
    public static long getMidNightMillis(int pDaysOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime() + pDaysOffset * ONE_DAY_MILLIS);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    /**
     * @return mid-night epoch millis of current day
     */
    public static long getTodayMidNightMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getMidNightMsFromLongDate(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @return mid-night epoch millis of current day
     */
    public static long getDateMidNightMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @return mid-night epoch millis of current day
     */
    public static long getSystemCurrentTimeInMills() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((new Date()).getTime());
        return calendar.getTimeInMillis();
    }

    /**
     * @return mid-night epoch millis as per parameters
     * @param pYear
     * @param pMonthIndex
     * @param pDayOfMonth
     */
    public static long getMidNightMillis(int pYear, int pMonthIndex, int pDayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, pYear);
        calendar.set(Calendar.MONTH, pMonthIndex);
        calendar.set(Calendar.DAY_OF_MONTH, pDayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }



    public static long getMidNightMillis(String dateString){
        //ex : 2014-12-20

        String split[] = dateString.split("-");

        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int day = Integer.parseInt(split[2]);

        long milis = getMidNightMillis(year, month-1, day);
        Log.e("Mid night milisec", milis+"");
        return milis;
    }

    public static long getOneMonthMilisec(String dateString){
        String split[] = dateString.split("-");

        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int day = Integer.parseInt(split[2]);

        long milis = getMidNightMillis(year, month-1, day);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milis + noOfDay(month,year) * ONE_DAY_MILLIS);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param pEpochMillis
     * @return int array with year, monthIndex and day-of-month
     */
    public static int[] getDateFields(long pEpochMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pEpochMillis);
        int[] fields = new int[3];
        fields[0] = calendar.get(Calendar.YEAR);
        fields[1] = calendar.get(Calendar.MONTH);
        fields[2] = calendar.get(Calendar.DAY_OF_MONTH);
		/*
		 * fields[3] = calendar.get(Calendar.HOUR_OF_DAY); fields[4] =
		 * calendar.get(Calendar.MINUTE); fields[5] =
		 * calendar.get(Calendar.SECOND); fields[6] =
		 * calendar.get(Calendar.MILLISECOND); fields[7] =
		 * calendar.get(Calendar.DAY_OF_WEEK);
		 */
        return fields;
    }

    public static int[] getTimeFields(long pEpochMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pEpochMillis);
        int[] fields = new int[4];
        fields[0] = calendar.get(Calendar.HOUR_OF_DAY);
        fields[1] = calendar.get(Calendar.MINUTE);
        fields[2] = calendar.get(Calendar.SECOND);
        fields[3] = calendar.get(Calendar.MILLISECOND);
		/*int[] fields = new int[3];
		fields[0] = calendar.get(Calendar.YEAR);
		fields[1] = calendar.get(Calendar.MONTH);
		fields[2] = calendar.get(Calendar.DAY_OF_MONTH);*/
		/*
		 * fields[3] = calendar.get(Calendar.HOUR_OF_DAY); fields[4] =
		 * calendar.get(Calendar.MINUTE); fields[5] =
		 * calendar.get(Calendar.SECOND); fields[6] =
		 * calendar.get(Calendar.MILLISECOND); fields[7] =
		 * calendar.get(Calendar.DAY_OF_WEEK);
		 */
        return fields;
    }


    public static String getDayOfMonth(long millis){
        return getDateFields(millis)[2]+"";
    }

    public static String getFormattedDate(long millis){
        int dateFields[] = getDateFields(millis);
        return dateFields[2]+"/"+(dateFields[1]+1)+"/"+dateFields[0];
    }


    /**
     * Gets epoch millis for a string representing a date in
     * dd/MM/yyyy hh:mm:ss:aa format
     *
     * @param dateStr
     *            date in format yyyy-MM-ddTHH:mm:ss Any one-char separator can
     *            be used in place of '-', 'T' and ':'
     */

    public static long getMillisecondsFromString(String dateStr){

        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss aa");
        Date mDate = new Date();

        try {
            mDate = dateFormat.parse(dateStr);
        } catch (java.text.ParseException e) {

            e.printStackTrace();
        }

        return mDate.getTime();
    }

    /**
     * Input time in milliseconds and returns string in
     * dd/MM/yyyy hh:mm:ss:aa format
     *
     *
     */
    public static String getDateFormatFromMills(String milliseconds){

        if(milliseconds==null || milliseconds.trim().length()==0)
        {
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");

        Date date = new Date(Long.parseLong(milliseconds));
        String dateString=dateFormat.format(date);

        return dateString;
    }


    /**
     * Gets epoch millis for a string representing a date in
     * yyyy-MM-ddTHH:mm:ssZ format
     *
     * @param dateStr
     *            date in format yyyy-MM-ddTHH:mm:ss Any one-char separator can
     *            be used in place of '-', 'T' and ':'
     */
    public static long parseExtendedDate(String dateStr) {
        /**
         * 2013-01-01 0123456789
         */
        int year = StringUtils.parseInt(dateStr, 0, 4);
        int month = StringUtils.parseInt(dateStr, 5, 7);
        int day = StringUtils.parseInt(dateStr, 8, 10);
        int hour = StringUtils.parseInt(dateStr, 11, 13);
        int minute = StringUtils.parseInt(dateStr, 14, 16);
        int second = StringUtils.parseInt(dateStr, 17, 19);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Jan is 1 but jan-index is 0
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
    public static String changeDateInddMMyyyy(String inputDate){
        String outputDateStr =null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
            java.text.DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.US);
            Date date = inputFormat.parse(inputDate);
            outputDateStr = outputFormat.format(date);

        } catch (Exception e) {
            return null;
        }
        return outputDateStr;
    }
    public static String changeDate(String inputDate){
        String outputDateStr =null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            java.text.DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
            Date date = inputFormat.parse(inputDate);
            outputDateStr = outputFormat.format(date);

        } catch (Exception e) {
            return null;
        }
        return outputDateStr;
    }

    public static Date stringToDate(String inputDate){
        Date date  =null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            //java.text.DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
            date = inputFormat.parse(inputDate);
            // outputDateStr = outputFormat.format(date);

        } catch (Exception e) {
            return null;
        }
        return date;
    }
    public static String getSeperateDate(String inputDate){
        String outputDateStr=null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
            java.text.DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
            Date date = inputFormat.parse(inputDate);
            outputDateStr = outputFormat.format(date);
        } catch (Exception e) {
            return null;
        }
        return outputDateStr;
    }
    public static String getSeperateTime(String inputDate){
        String outputTimeStr = null;
        try {
            java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
            java.text.DateFormat outputFormat = new SimpleDateFormat("HH:mm:ss",Locale.US);
            Date date = inputFormat.parse(inputDate);
            outputTimeStr = outputFormat.format(date);
        } catch (Exception e) {
            return null;
        }
        return outputTimeStr;
    }

    private static int noOfDay(int month,int year){

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if(year%4==0)
                    return 29;
                else
                    return 28;
            default:
                return 30;
        }

    }

    //App Utility methods

    public static boolean isYesterdayDate(Date date){

        // Create a calendar object with today date. Calendar is in java.util pakage.
        Calendar calendar = Calendar.getInstance();

        // Move calendar to yesterday
        calendar.add(Calendar.DATE, -1);

        // Get current date of calendar which point to the yesterday now
        Date yesterday = calendar.getTime();

        return isDatesDaysEquals(yesterday,date);
    }

    public static boolean isTodaysDate(Date date){
        // Create a calendar object with today date. Calendar is in java.util pakage.
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        return isDatesDaysEquals(today,date);
    }

    /* Return true if dates are equals */
    public static boolean isDatesDaysEquals(Date date1, Date date2){
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return fmt.format(date1).equals(fmt.format(date2));

    }

    public static boolean isTodaysDate(long timeInMills){

        return  (getTodayMidNightMillis()==timeInMills);
    }

    public static String getTimeInFormat(long millis){
        int[] field = getTimeFields(millis);
        return addZeoInPrefix(field[0]+"")+":"+addZeoInPrefix(field[1]+"");
        //return millis+"";
    }

    /**
     * @return mid-night epoch millis of current day
     */
    public static long getHourMinuteMillis(int hour,int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getSystemCurrentTimeInMills());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        return calendar.getTimeInMillis();
    }

    public static long getMsTillHourFromCurrentTime(int hourOfTheDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfTheDay);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }
    public static String getBgTimeInFormat(long mills){
        int[] field = getTimeFields(mills);
        return (addZeoInPrefix(field[0]+"")+":"+addZeoInPrefix(field[1]+""));
    }

    public static String getTakenTimeInFormat(int hour,int minute){
        return (addZeoInPrefix(hour+"")+":"+addZeoInPrefix(minute+""));
    }

    private static String addZeoInPrefix(String s){
        if(s.length()==1)
            s = "0"+s;
        return s;
    }


    public static int[] getDateFields(String dateString){
        //ex : 2014-12-20
        int[] d = new  int[3];
        String split[] = dateString.split("/");

        d[0] = Integer.parseInt(split[0]);
        d[1] = Integer.parseInt(split[1]);
        d[2] = Integer.parseInt(split[2]);

        return d;
    }


    public static int[] getTimesFields(String timeString){
        //ex : 2014-12-20
        if(timeString.equalsIgnoreCase("select")) {
            return null;
        }
        int[] t = new  int[2];
        String split[] = timeString.split(":");

        t[0] = Integer.parseInt(split[0]);
        t[1] = Integer.parseInt(split[1]);

        return t;
    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

}
