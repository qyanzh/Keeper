package com.example.keeper.mytools;

import com.example.keeper.Bill;

import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MyBillTools {

    public static class CompareBillByTime implements Comparator<Bill> {
        @Override
        public int compare(Bill o1, Bill o2) {
            return Long.compare(o2.getTimeMills(), o1.getTimeMills());
        }

    }

    @TestOnly
    public static List<Bill> getBillListRandomly(int amounts) {
        List<Bill> billList = new ArrayList<>();
        for (int i = 0; i < amounts; ++i) {
            Bill bill = new Bill();
            bill.setPrice(new Random().nextInt() % 200);
            if (bill.getPrice() < 0) bill.setType(Bill.PAYOUT);
            else bill.setType(Bill.INCOME);
            if (bill.isIncome()) {
                bill.setCategory(Bill.incomeCategory[Math.abs(new Random().nextInt() % Bill.incomeCategory.length)]);
            } else {
                bill.setCategory(Bill.payoutCategory[Math.abs(new Random().nextInt() % Bill.payoutCategory.length)]);
            }
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            c.set(year, month - Math.abs(new Random().nextInt() % 12), day - new Random().nextInt() % 30, new Random().nextInt() % 24 + 1, new Random().nextInt() % 60);
            bill.setTime(c.getTimeInMillis());
            billList.add(bill);
        }
        return billList;
    }
}
