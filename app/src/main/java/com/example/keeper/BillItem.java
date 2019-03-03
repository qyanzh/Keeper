
package com.example.keeper;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

import java.util.Calendar;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class BillItem extends LitePalSupport implements Cloneable, Parcelable, Comparable<BillItem> {

    public static final int INCOME = 0;

    public static final int PAYOUT = 1;

    private long id;

    private float price;

    private int type;

    private String remark;

    private String category;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private long timeMills;


    public BillItem() {
        this.price = 0;
        this.type = PAYOUT;
        this.remark = "";
        this.category = "消费";
        this.setTime(Calendar.getInstance().getTimeInMillis());
    }

    protected BillItem(Parcel in) {
        id = in.readLong();
        price = in.readFloat();
        type = in.readInt();
        remark = in.readString();
        category = in.readString();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        timeMills = in.readLong();
    }

    public static final Creator<BillItem> CREATOR = new Creator<BillItem>() {
        @Override
        public BillItem createFromParcel(Parcel in) {
            return new BillItem(in);
        }

        @Override
        public BillItem[] newArray(int size) {
            return new BillItem[size];
        }
    };

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int compareTo(BillItem o) {
        return Long.compare(o.getTimeMills(), this.getTimeMills());
    }

    public void setTime(long timeMills) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMills);
        this.timeMills = timeMills;
        this.year = c.get(YEAR);
        this.month = c.get(MONTH) + 1;
        this.day = c.get(DAY_OF_MONTH);
        this.hour = c.get(HOUR_OF_DAY);
        this.minute = c.get(MINUTE);
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeFloat(price);
        dest.writeInt(type);
        dest.writeString(remark);
        dest.writeString(category);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeLong(timeMills);
    }
}