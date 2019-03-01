package com.example.keeper.mytools;


import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class MyDateFormat {

    private static SimpleDateFormat sameYearFormatter = new SimpleDateFormat("MM月dd日", Locale.CHINA);
    private static SimpleDateFormat normalDateFormatter = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
    public static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm",Locale.CHINA);

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

    @NotNull
    public static String formatWithTime(long timeMills, boolean changeRecent) {
        return format(timeMills, changeRecent) + " " + timeFormatter.format(timeMills);
    }
}
