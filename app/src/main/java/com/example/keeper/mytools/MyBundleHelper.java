package com.example.keeper.mytools;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

public class MyBundleHelper {

    public static final int YEAR_MODE = 0;
    public static final int MONTH_MODE = 1;
    public static final int DAY_MODE = 2;
    private static final String[] YEAR_QUERY_CONDITION = {"year"};
    private static final String[] MONTH_QUERY_CONDITION = {"year", "month"};
    private static final String[] DAY_QUERY_CONDITION = {"year", "month", "day"};


    @NotNull
    public static Bundle getQueryBundle(String[] queryConditions, String[] queryArguments) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("queryConditions", queryConditions);
        bundle.putStringArray("queryArguments", queryArguments);
        return bundle;
    }

    @NotNull
    public static Bundle getDateQueryBundle(int mode, int... dateArguments) {
        String[] queryConditions = null;
        switch (mode) {
            case YEAR_MODE:
                queryConditions = YEAR_QUERY_CONDITION;
                break;
            case MONTH_MODE:
                queryConditions = MONTH_QUERY_CONDITION;
                break;
            case DAY_MODE:
                queryConditions = DAY_QUERY_CONDITION;
                break;
        }
        int length = dateArguments.length;
        String[] queryArguments = new String[length];
        for (int i = 0; i < length; ++i) {
            queryArguments[i] = String.valueOf(dateArguments[i]);
        }
        return getQueryBundle(queryConditions, queryArguments);
    }
}
