package com.haibin.calendarviewproject.custom;
import android.content.Intent;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.haibin.calendarviewproject.entity.Schedule;
import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.adapter.DBAdapter;
import com.haibin.calendarviewproject.base.activity.BaseActivity;
import com.haibin.calendarviewproject.utils.ShortCutUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import top.defaults.view.DateTimePickerView;

public class AddActivity extends BaseActivity {

    private ShortCutUtil shortCutUtil;

    private TextView cancel_button;
    private TextView finish_button;
    private EditText things;
    private RadioGroup statement;
    private RadioButton event;
    private RadioButton leave;
    private RadioButton birthday;
    private RadioButton meeting;

    private RadioButton yes;
    private RadioButton no;


    private TextView start_time;
    private DateTimePickerView startDateTimePickerView;
    private TextView end_time;
    private DateTimePickerView endDateTimePickerView;

    private String date;

    private static String start;
    private static String end;

    private DBAdapter dbAdapter;
    private static final String TAG = "AddActivity";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_schedule_add;
    }

    @Override
    protected void initView() {
        Intent intent =getIntent();
        date = intent.getStringExtra("date");

        dbAdapter = new DBAdapter(AddActivity.this);
        dbAdapter.open();
        cancel_button = findViewById(R.id.cancel_button);
        finish_button = findViewById(R.id.finish_button);
        things = findViewById(R.id.things);
        start_time = findViewById(R.id.start_time);
        startDateTimePickerView = findViewById(R.id.pickerViewStart);
        end_time = findViewById(R.id.end_time);
        endDateTimePickerView = findViewById(R.id.pickerViewEnd);
        statement = findViewById(R.id.statement);
        event = findViewById(R.id.event);
        leave = findViewById(R.id.leave);
        birthday = findViewById(R.id.birthday);
        meeting = findViewById(R.id.meeting);

        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        finish_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String info = things.getText().toString();
                if("".equals(info)){
                    return;
                }
                if(no.isChecked() == true) {
                    //存入数据库操作
                    addData();
                }else if(yes.isChecked() == true){
                    //创建快捷方式并存入数据库
                    createShortCut();
                }
                //跳转回主页面
                finish();
            }
        });

        startDateTimePickerView.setSelectedDate(ConvertStringToCalendar(date));
        endDateTimePickerView.setSelectedDate(ConvertStringToCalendar(date));

        startDateTimePickerView.setOnSelectedDateChangedListener(new DateTimePickerView.OnSelectedDateChangedListener() {
            @Override
            public void onSelectedDateChanged(Calendar calendar) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String dateString = String.format(Locale.getDefault(), "%d年%02d月%02d日 %02d时%02d分", year, month + 1, dayOfMonth, hour, minute);
                start = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d", year, month + 1, dayOfMonth, hour, minute);
                start_time.setText(dateString);
                Log.d(TAG, "new date: " + dateString);
            }
        });
        endDateTimePickerView.setOnSelectedDateChangedListener(new DateTimePickerView.OnSelectedDateChangedListener() {
            @Override
            public void onSelectedDateChanged(Calendar calendar) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String dateString = String.format(Locale.getDefault(), "%d年%02d月%02d日 %02d时%02d分", year, month + 1, dayOfMonth, hour, minute);
                end = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d", year, month + 1, dayOfMonth, hour, minute);
                end_time.setText(dateString);
                Log.d(TAG, "new date: " + dateString);
            }
        });
    }

    @Override
    protected void initData() {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(1970, 0, 1, 0, 0);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(2122, 0, 1, 0, 0);
        startDateTimePickerView.setStartDate(calendarStart);
        startDateTimePickerView.setEndDate(calendarEnd);
        endDateTimePickerView.setStartDate(calendarStart);
        endDateTimePickerView.setEndDate(calendarEnd);

        shortCutUtil = ShortCutUtil.getInstance().init(this);
    }

    //不是重要日
    private void addData(){
        String info = things.getText().toString();
        //类型选择
        int real_state = statement.getCheckedRadioButtonId();
        int state = -1;
        if(real_state == event.getId()){
            state = 1;
        } else if(real_state == leave.getId()){
            state = 2;
        } else if(real_state == birthday.getId()){
            state = 3;
        } else if(real_state == meeting.getId()){
            state = 4;
        }
        long start_time = ConvertStringToLong(start);
        long end_time = ConvertStringToLong(end);
        Schedule schedule = new Schedule(info, start_time, end_time, state, 5);
        if(start_time > end_time){
            showToast("日程添加失败！——开始日期不可晚于结束日期");
        }
        else if(dbAdapter.insert(schedule) > 0) {
            showToast("日程已添加");
        }else{
            showToast("日程添加失败！——原因未知");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createShortCut(){
        String info = things.getText().toString();
        //类型选择
        int real_state = statement.getCheckedRadioButtonId();
        int state = -1;
        if(real_state == event.getId()){
            state = 1;
        } else if(real_state == leave.getId()){
            state = 2;
        } else if(real_state == birthday.getId()){
            state = 3;
        } else if(real_state == meeting.getId()){
            state = 4;
        }
        long start_time = ConvertStringToLong(start);
        long end_time = ConvertStringToLong(end);
        Schedule schedule = new Schedule(info, start_time, end_time, state, 6);
        if(start_time > end_time){
            showToast("重要日添加失败！——开始日期不可晚于结束日期");
        }
        else if(dbAdapter.insert(schedule) > 0) {

            int id = dbAdapter.selectLastItemId();
            shortCutUtil.AddShortCut(this, ImpDayDetailActivity.class, ImpDayDetailActivity.class, id, R.mipmap.ic_launcher);
            shortCutUtil.updItem(this, ImpDayDetailActivity.class, id, R.mipmap.ic_launcher, "重要日");
            showToast("重要日已添加");
        }else{
            showToast("重要日添加失败！——原因未知");
        }
    }

    private void showToast(String msg){
        Toast.makeText(AddActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    private long ConvertStringToLong(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateTime = date.getTime();
        return dateTime;
    }
    private String ConvertLongToString(long dateLong){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分");
        Date date = new Date(dateLong);
        String res = simpleDateFormat.format(date);
        return res;
    }
    private Date ConvertLongToDate(long dateLong){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(dateLong);
        return date;
    }
    private Calendar ConvertStringToCalendar(String date){
       String[] times = date.split("-");
       Calendar calendar = Calendar.getInstance();
       int year = Integer.parseInt(times[0]);
       int month = Integer.parseInt(times[1]);
       int day = Integer.parseInt(times[2]);
       calendar.set(year, month - 1, day);
       return calendar;
    }
}
