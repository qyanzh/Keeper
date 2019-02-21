package com.example.keeper;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateFormat {
    private static SimpleDateFormat todayFormatter = new SimpleDateFormat("今天 HH:mm");
    private static SimpleDateFormat yesterdayFormatter = new SimpleDateFormat("昨天 HH:mm");
    private static SimpleDateFormat sameYearFormatter = new SimpleDateFormat("MM/dd HH:mm");
    private static SimpleDateFormat longFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    public String format(long timeMills) {
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        int curDay = c.get(Calendar.DAY_OF_MONTH);
        c.setTimeInMillis(timeMills);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Date d = c.getTime();
        String ret;
        if(year == curYear) {
            if(month == curMonth) {
                if(day == curDay) {
                    ret = todayFormatter.format(d);
                } else if( day == curDay-1 ) {
                    ret = yesterdayFormatter.format(d);
                } else {
                    ret = sameYearFormatter.format(d);
                }
            } else {
                ret = sameYearFormatter.format(d);
            }
        } else {
            ret = longFormatter.format(d);
        }
        return ret;
    }
}
