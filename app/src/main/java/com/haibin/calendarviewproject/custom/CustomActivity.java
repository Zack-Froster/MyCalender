package com.haibin.calendarviewproject.custom;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.core.app.ActivityCompat;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import com.haibin.calendarviewproject.entity.Schedule;
import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.adapter.DBAdapter;
import com.haibin.calendarviewproject.adapter.ScheduleAdapter;
import com.haibin.calendarviewproject.base.activity.BaseActivity;
import com.haibin.calendarviewproject.receiver.MessageBroadcastReceiver;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CustomActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener {

    private MessageBroadcastReceiver msgReceiver;

    private DBAdapter dbAdapter;
    private ScheduleAdapter scheduleAdapter;
    private Schedule schedule;
    private List<Schedule> scheduleList;

    private List<Schedule> allSchedule;

    private ListView lv_show;
    private TextView mTextMonthDay;
    private TextView mTextYear;
    private TextView mTextLunar;
    private TextView mTextCurrentDay;
    private TextView today;
    private CalendarView mCalendarView;


    private RelativeLayout mRelativeTool;
    private int mYear;
    private CalendarLayout mCalendarLayout;

    private ImageView fab_add;
    private ImageView search_image;

    private int myYear;
    private int myMonth;
    private int myDay;


    private static Map<String, Calendar> map = new HashMap<>();

    public static void show(Context context) {
        context.startActivity(new Intent(context, CustomActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom;
    }

    @Override
    protected void initView() {
        setStatusBarDarkMode();

        scheduleList = new ArrayList<Schedule>();
        dbAdapter = new DBAdapter(CustomActivity.this);
        dbAdapter.open();

        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        fab_add = findViewById(R.id.fab_add);
        search_image = findViewById(R.id.search_image);
        today = findViewById(R.id.today);
        lv_show = findViewById(R.id.lv_show);

        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });
        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        myYear = mCalendarView.getCurYear();
        myMonth = mCalendarView.getCurMonth();
        myDay = mCalendarView.getCurDay();

        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomActivity.this, AddActivity.class);
                intent.putExtra("date", myYear+"-"+myMonth+"-"+myDay);
                startActivity(intent);
            }
        });
        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        today.setText(mTextYear.getText().toString()+"年 " + mTextMonthDay.getText().toString());

        scheduleList = dbAdapter.selectAllByDate(java.util.Calendar.getInstance().getTimeInMillis());
        scheduleAdapter = new ScheduleAdapter(CustomActivity.this, scheduleList);
        lv_show.setAdapter(scheduleAdapter);

        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                schedule = scheduleList.get(position);
                Intent intent = new Intent(CustomActivity.this, ScheduleDetailActivity.class);
                intent.putExtra("schedule", schedule);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        myYear = mCalendarView.getCurYear();
        myMonth = mCalendarView.getCurMonth();
        myDay = mCalendarView.getCurDay();


        if (ActivityCompat.checkSelfPermission(CustomActivity.this,

                Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED||

                ActivityCompat.checkSelfPermission(CustomActivity.this, Manifest.permission.RECEIVE_SMS)

                        !=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(CustomActivity.this,

                    new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.INSTALL_SHORTCUT},

                    1);

        }//动态申请权限
        msgReceiver = new MessageBroadcastReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);
        registerReceiver(msgReceiver, filter);


    }


    @Override
    public void onClick(View v) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        java.util.Calendar myCalendar= java.util.Calendar.getInstance();
        myCalendar.set(myYear, myMonth-1, myDay);
        scheduleList = dbAdapter.selectAllByDate(myCalendar.getTimeInMillis());
        RefreshView();

        allSchedule = dbAdapter.selectAll();
        map.clear();
        for(int i = 0; i < allSchedule.size(); i++) {
            long start = getStart(allSchedule.get(i).getStartDateTime());
            long end = getEnd(allSchedule.get(i).getEndDateTime());
            java.util.Calendar tmpCalendar = java.util.Calendar.getInstance();
            for(long j = start; j <= end; j+=86400){
                tmpCalendar = ConvertLongToCalendar(j * 1000);
                switch (allSchedule.get(i).getStatement()){
                    case 1:
                        String tmpKey1 = getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFF13acf0, "事").toString();
                        if(map.containsKey(tmpKey1)){
                            map.put(tmpKey1, getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                    tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFFbc13f0, "多"));
                        }else {
                            map.put(tmpKey1, getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                    tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFF13acf0, "事"));
                        }
                        break;
                    case 2:
                        String tmpKey2 = getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFFe69138, "假").toString();
                        if(map.containsKey(tmpKey2)){
                            map.put(tmpKey2, getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                    tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFFbc13f0, "多"));
                        }else {
                            map.put(tmpKey2, getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                    tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFFe69138, "假"));
                        }
                        break;
                    case 3:
                        String tmpKey3 = getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFFdf1356, "生").toString();
                        if(map.containsKey(tmpKey3)){
                            map.put(tmpKey3, getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                    tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFFbc13f0, "多"));
                        }else {
                            map.put(tmpKey3, getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                    tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFFdf1356, "生"));
                        }
                        break;
                    case 4:
                        String tmpKey4 = getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFF40db25, "会").toString();
                        if(map.containsKey(tmpKey4)){
                            map.put(tmpKey4, getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                    tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFFbc13f0, "多"));
                        }else {
                            map.put(tmpKey4, getSchemeCalendar(tmpCalendar.get(java.util.Calendar.YEAR),
                                    tmpCalendar.get(java.util.Calendar.MONTH) + 1, tmpCalendar.get(java.util.Calendar.DATE), 0xFF40db25, "会"));
                        }
                        break;
                }
            }
        }
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }
    private void RefreshView(){
        scheduleAdapter.setScheduleList(scheduleList);
        scheduleAdapter.notifyDataSetChanged();
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        today.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();

        myYear = calendar.getYear();
        myMonth = calendar.getMonth();
        myDay = calendar.getDay();
        today.setText(mTextYear.getText().toString()+"年 " + mTextMonthDay.getText().toString());

        java.util.Calendar myCalendar= java.util.Calendar.getInstance();
        myCalendar.set(myYear, myMonth-1, myDay);
        scheduleList = dbAdapter.selectAllByDate(myCalendar.getTimeInMillis());
        scheduleAdapter.setScheduleList(scheduleList);
        scheduleAdapter.notifyDataSetChanged();


        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
    }

    private long getStart(long time){
        return (time + 28800000) / 1000 - (time + 28800000) / 1000 % 86400;
    }

    private long getEnd (long time){
        return (time + 28800000) / 1000 - (time + 28800000) / 1000 % 86400 + 86399;
    }

    private java.util.Calendar ConvertLongToCalendar(long dateLong){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(dateLong);
        return calendar;
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomActivity.this.unregisterReceiver(msgReceiver);
    }


}
