package com.example.keeper.mytools;


import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class MyDateFormat {

    private static SimpleDateFormat sameYearFormatter = new SimpleDateFormat("M月d日 EE", Locale.CHINA);
    private static SimpleDateFormat normalDateFormatter = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
    public static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm",Locale.CHINA);

    public static String formatForTimely(Date d, boolean[] mode) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        StringBuilder sb = new StringBuilder();
        int length = mode.length;
        if(length>0 && mode[0]) {
            sb.append(c.get(YEAR)).append("年");
        }
        if(length>1 && mode[1]) {
            sb.append(c.get(MONTH)+1).append("月");
        }
        if(length>2 &&mode[2]) {
            sb.append(c.get(DAY_OF_MONTH)).append("日");
        }
        return sb.toString();
    }

    public static String format(long timeMills, boolean changeRecent) {
        Calendar c = Calendar.getInstance();
        int curYear = c.get(YEAR);
        int curMonth = c.get(MONTH);
        int curDay = c.get(DAY_OF_MONTH);
        c.setTimeInMillis(timeMills);
        int year = c.get(YEAR);
        int month = c.get(MONTH);
        int day = c.get(DAY_OF_MONTH);
        String ret;
       // if (year == curYear) {
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
//        } else {
//            ret = normalDateFormatter.format(c.getTime());
//        }
        return ret;
    }

    @NotNull
    public static String formatWithTime(long timeMills, boolean changeRecent) {
        return format(timeMills, changeRecent) + " " + timeFormatter.format(timeMills);
    }
}
