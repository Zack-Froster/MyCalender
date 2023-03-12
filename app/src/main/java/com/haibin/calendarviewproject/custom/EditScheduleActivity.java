package com.haibin.calendarviewproject.custom;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarviewproject.entity.Schedule;
import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.adapter.DBAdapter;
import com.haibin.calendarviewproject.base.activity.BaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import top.defaults.view.DateTimePickerView;

public class EditScheduleActivity extends BaseActivity {

    private TextView cancel_button;
    private TextView finish_button;
    private EditText things;
    private RadioGroup statement;
    private RadioButton event;
    private RadioButton leave;
    private RadioButton birthday;
    private RadioButton meeting;
    private TextView start_time;
    private DateTimePickerView startDateTimePickerView;
    private TextView end_time;
    private DateTimePickerView endDateTimePickerView;
    private static String start;
    private static String end;
    private DBAdapter dbAdapter;

    private Schedule schedule;

    private final String TAG = "EditScheduleActivity";

    @Override
    protected int getLayoutId() {
        return R.layout.edit_impday;
    }

    @Override
    protected void initView() {
        dbAdapter = new DBAdapter(EditScheduleActivity.this);
        dbAdapter.open();
        Intent intent = getIntent();
        schedule = intent.getParcelableExtra("schedule");

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

        things.setText(schedule.getInfo());
        switch (schedule.getStatement()){
            case 1:
                event.setChecked(true);
                break;
            case 2:
                leave.setChecked(true);
                break;
            case 3:
                birthday.setChecked(true);
                break;
            case 4:
                meeting.setChecked(true);
                break;
            case -1:
                break;
        }
        startDateTimePickerView.setSelectedDate(ConvertLongToCalendar(schedule.getStartDateTime()));
        endDateTimePickerView.setSelectedDate(ConvertLongToCalendar(schedule.getEndDateTime()));


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String info = things.getText().toString();
                if("".equals(info)){
                    return;
                }
                //更新数据库操作
                updateData();
                //跳转上一页面
                finish();
            }
        });
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



        int real_state = schedule.getStatement();
        if(real_state == 1){
            event.setChecked(true);
        } else if(real_state == 2){
            leave.setChecked(true);
        } else if(real_state == 3){
            birthday.setChecked(true);
        } else if(real_state == 4){
            meeting.setChecked(true);
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
    }

    private void updateData(){
        int id = schedule.getId();
        String info = things.getText().toString();
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
        Schedule schedule = new Schedule(id, info, start_time, end_time, state, this.schedule.getImpday());
        if(start_time > end_time){
            showToast("日程更新失败！——开始日期不可晚于结束日期");
        }
        else if(dbAdapter.update(id, schedule) > 0) {
            showToast("日程已更新");
        }else{
            showToast("日程更新失败！");
        }
    }

    private void showToast(String msg){
        Toast.makeText(EditScheduleActivity.this, msg, Toast.LENGTH_SHORT).show();
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
    private Calendar ConvertLongToCalendar(long dateLong){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateLong);
        return calendar;
    }
}
