package com.example.keeper;

import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;

public class Bill extends LitePalSupport implements Cloneable, Serializable {

    public static final boolean INCOME = true;

    public static final boolean PAYOUT = false;

    public static String[] incomeCategory = {
            "转账", "生活费", "红包", "奖学金", "工资", "其它"
    };

    public static String[] payoutCategory = {
            "消费", "餐饮", "缴费", "转账", "购物", "娱乐", "学习", "出行", "电影", "聚餐", "其它"
    };

    private long id;

    private float price;

    private boolean type;

    private String remark;

    private String category;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private long timeMills;


    public Bill() {
        this.price = 0;
        this.type = PAYOUT;
        this.remark = "";
        this.category = "消费";
        this.setTime(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setTime(long timeMills) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMills);
        this.timeMills = timeMills;
        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH) + 1;
        this.day = c.get(Calendar.DAY_OF_MONTH);
        this.hour = c.get(Calendar.HOUR_OF_DAY);
        this.minute = c.get(Calendar.MINUTE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isIncome() {
        return INCOME == type;
    }

    public boolean isPayout() {
        return PAYOUT == type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public long getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(long timeMills) {
        this.timeMills = timeMills;
    }

}