package com.example.keeper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateFormat {
    public enum FORMATTYPE{
        DAY,NORMAL
    }
    public static SimpleDateFormat sameYearFormatter = new SimpleDateFormat("MM月dd日");
    public static SimpleDateFormat normalDateFormatter = new SimpleDateFormat("yyyy年MM月dd日");
    public static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    public static String format(long timeMills,FORMATTYPE type) {
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        int curDay = c.get(Calendar.DAY_OF_MONTH);
        c.setTimeInMillis(timeMills);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Date d = c.getTime();
        StringBuilder ret = new StringBuilder();
        if(year == curYear) {
            if(month == curMonth) {
                if(day == curDay) {
                    ret.append("今天");
                } else if( day == curDay-1 ) {
                    ret.append("昨天");
                } else {
                    ret.append(sameYearFormatter.format(d));
                }
            } else {
                ret.append(sameYearFormatter.format(d));
            }
        } else {
            ret.append(normalDateFormatter.format(d));
        }
        if(type == FORMATTYPE.NORMAL) {
            ret.append(" "+timeFormatter.format(d));
        }
        return ret.toString();
    }
}
