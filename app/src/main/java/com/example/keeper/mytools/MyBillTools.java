package com.example.keeper.mytools;

import android.content.res.Resources;

import com.example.keeper.BillItem;
import com.example.keeper.R;

import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class MyBillTools {

//    private static String[] incomeCategory = {
//            "转账", "生活费", "红包", "奖学金", "工资", "其它"
//    };
//
//    private static String[] payoutCategory = {
//            "消费", "餐饮", "缴费", "转账", "购物", "娱乐", "学习", "出行", "电影", "聚餐", "其它"
//    };

    public static class CompareBillByTime implements Comparator<BillItem> {
        @Override
        public int compare(BillItem o1, BillItem o2) {
            return Long.compare(o2.getTimeMills(), o1.getTimeMills());
        }

    }

    @TestOnly
    public static List<BillItem> getBillListRandomly(int amounts, Resources r) {
        String[] incomeCategory = r.getStringArray(R.array.incomeCategory);
        String[] payoutCategory = r.getStringArray(R.array.payoutCategory);
        List<BillItem> billItemList = new ArrayList<>();
        for (int i = 0; i < amounts; ++i) {
            BillItem billItem = new BillItem();
            billItem.setPrice(new Random().nextInt() % 200);
            if (billItem.getPrice() < 0) billItem.setType(BillItem.PAYOUT);
            else billItem.setType(BillItem.INCOME);
            if (billItem.isIncome()) {
                billItem.setCategory(incomeCategory[Math.abs(new Random().nextInt() % incomeCategory.length)]);
            } else {
                billItem.setCategory(payoutCategory[Math.abs(new Random().nextInt() % payoutCategory.length)]);
            }
            Calendar c = Calendar.getInstance();
            Random random = new Random();
            int year = c.get(YEAR) - Math.abs(random.nextInt(2));
            int month = Math.abs(random.nextInt((year == c.get(YEAR) ? c.get(MONTH) + 1 : 12)));
            int day = Math.abs(random.nextInt(28));
            int hour = Math.abs(random.nextInt(24));
            int minute = Math.abs(random.nextInt(60));
            c.set(year, month, day, hour, minute);
            billItem.setTime(c.getTimeInMillis());
            billItemList.add(billItem);
        }
        return billItemList;
    }

    public static String[] getYearStrings() {
        Calendar c = Calendar.getInstance();
        List<String> ret = new ArrayList<>();
        for (int i = 2018; i <= c.get(YEAR); ++i) {
            ret.add(String.valueOf(i));
        }
        return ret.toArray(new String[ret.size()]);
    }
}
