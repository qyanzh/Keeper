package com.example.keeper.mytools;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyDateFormat {
    //TODO:getDateTimeInstance();
    public static SimpleDateFormat sameYearFormatter = new SimpleDateFormat("MM月dd日");
    public static SimpleDateFormat normalDateFormatter = new SimpleDateFormat("yyyy年MM月dd日");
    public static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    public static String format(long timeMills, boolean changeRecent) {
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        int curDay = c.get(Calendar.DAY_OF_MONTH);
        c.setTimeInMillis(timeMills);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String ret;
        if (year == curYear) {
            if (month == curMonth && changeRecent) {
                if (day == curDay) {
                    ret = "今天";
                } else if (day == curDay - 1) {
                    ret = "昨天";
                } else {
                    ret = sameYearFormatter.format(c.getTime());
                }
            } else {
                ret = sameYearFormatter.format(c.getTime());
            }
        } else {
            ret = normalDateFormatter.format(c.getTime());
        }
        return ret;
    }

    public static String formatWithTime(long timeMills, boolean changeRecent) {
        return format(timeMills, changeRecent) + " " + timeFormatter.format(timeMills);
    }
}
