package com.haibin.calendarviewproject.app;

import android.app.Application;

import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.custom.ImpDayDetailActivity;
import com.haibin.calendarviewproject.utils.ShortCutUtil;

public class MyCalendar extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        /**
         * 创建快捷方式
         */
        ShortCutUtil.getInstance().init(this);
    }
}
