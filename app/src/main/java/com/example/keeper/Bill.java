package com.example.keeper;

import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Bill extends LitePalSupport implements Cloneable{

    public static final boolean INCOME = true;

    public static final boolean PAYOUT = false;

    public static String[] incomeCategory = {
            "收入","生活费","奖学金","兼职"
    };

    public static String[] payoutCategory = {
            "消费","三餐","夜宵","食物","转账","购物","娱乐","学习","出行","电影","聚餐"
    };


    public long getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(long timeMills) {
        this.timeMills = timeMills;
    }

    public void setTime(long timeMills) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMills);
        this.timeMills = timeMills;
        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH)+1;
        this.day = c.get(Calendar.DAY_OF_MONTH);
        this.hour = c.get(Calendar.HOUR_OF_DAY);
        this.minute = c.get(Calendar.MINUTE);
    }



    public Bill() {
        this.price = 0;
        this.type = PAYOUT;
        this.category = "消费";
        this.remark = "";
        this.setTime(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Bill bill = null;
        try{
            bill = (Bill)super.clone();   //浅复制
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bill;
    }

    boolean type;

    float price;

    String category;

    int year,month,day,hour,minute;

    String remark;

    long timeMills;

    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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