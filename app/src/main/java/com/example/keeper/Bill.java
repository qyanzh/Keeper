package com.example.keeper;

import org.litepal.crud.LitePalSupport;


public class Bill extends LitePalSupport {

    public static final boolean INCOME = true;

    public static final boolean PAYOUT = false;

    public static String[] incomeCategory = {
            "收入","生活费","奖学金","兼职"
    };

    public static String[] payoutCategory = {
            "消费","三餐","夜宵","食物","转账","购物","娱乐","学习","出行","电影","聚餐"
    };


    public Bill(boolean type, float price, String category,String remark, int year, int month, int day, int hour, int minute) {
        this.type = type;
        this.price = price;
        this.category = category;
        this.remark=remark;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    boolean type;

    float price;

    String category;

    int year,month,day,hour,minute;

    String remark;

    long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isINCOME() {
        return INCOME==type;
    }

    public boolean isPAYOUT() {
        return PAYOUT==type;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}