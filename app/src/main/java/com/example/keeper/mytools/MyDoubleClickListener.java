package com.example.keeper.mytools;

import android.view.View;

import java.util.Date;

public abstract class MyDoubleClickListener implements View.OnClickListener {
    private long lastClickTime;
    private long delayMills ;

    public void onSingleClick(){}
    public abstract void onDoubleClick();

    public MyDoubleClickListener(long delayMills) {
        this.delayMills = delayMills;
    }

    @Override
    public final void onClick(View v) {
        long currentTime = new Date().getTime();
        if(currentTime - lastClickTime < delayMills) {
            onDoubleClick();
        } else {
            onSingleClick();
        }
        lastClickTime = currentTime;
    }
}
