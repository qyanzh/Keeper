package com.example.keeper.mytools;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

public class MyBundleHelper {


    @NotNull
    public static Bundle getQueryBundle(String[] queryConditions, String[] queryArguments) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("queryConditions", queryConditions);
        bundle.putStringArray("queryArguments", queryArguments);
        return bundle;
    }

    @NotNull
    public static Bundle getDateQueryBundle(int... dateArguments) {
        String[] queryConditions = new String[]{"year","month","day"};
        int length = dateArguments.length;
        String[] queryArguments = new String[length];
        for (int i = 0; i < length; ++i) {
            queryArguments[i] = String.valueOf(dateArguments[i]);
        }
        return getQueryBundle(queryConditions, queryArguments);
    }
}
