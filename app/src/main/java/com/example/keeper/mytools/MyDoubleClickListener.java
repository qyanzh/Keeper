package com.example.keeper.mytools;

import android.view.View;

import java.util.Date;

public abstract class MyDoubleClickListener implements View.OnClickListener {
    private long lastClickTime;
    private static long delayMills = 300;
    public abstract void onSingleClick();
    public abstract void onDoubleClick();
    @Override
    public void onClick(View v) {
        long currentTime = new Date().getTime();
        if(currentTime - lastClickTime < delayMills) {
            onDoubleClick();
        }
        lastClickTime = currentTime;
        onSingleClick();
    }
}
