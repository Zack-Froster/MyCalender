package com.haibin.calendarviewproject.custom;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.adapter.DBAdapter;
import com.haibin.calendarviewproject.base.activity.BaseActivity;
import com.haibin.calendarviewproject.entity.Schedule;
import com.haibin.calendarviewproject.utils.ShortCutUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ImpDayDetailActivity extends BaseActivity {
    private TextView schedule_edit;
    private TextView things;
    private TextView item_start_time;
    private TextView item_end_time;
    private RelativeLayout types;
    private TextView count_down;
    private TextView before_or_after;
    private RelativeLayout delete_button;
    private TextView show_type;

    private DBAdapter dbAdapter;
    private Schedule schedule;
    private ShortCutUtil shortCutUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.impday_detail;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void initView() {
        shortCutUtil = ShortCutUtil.getInstance().init(this);

        dbAdapter = new DBAdapter(ImpDayDetailActivity.this);
        dbAdapter.open();

        Intent intent = getIntent();
        schedule = dbAdapter.selectOneById(Integer.parseInt(intent.getStringExtra("scheduleId")));

        schedule_edit = findViewById(R.id.schedule_edit);
        things = findViewById(R.id.things);
        item_start_time = findViewById(R.id.item_start_time);
        item_end_time = findViewById(R.id.item_end_time);
        types = findViewById(R.id.types);

        count_down = findViewById(R.id.count_down);
        before_or_after = findViewById(R.id.before_or_after);
        show_type = findViewById(R.id.show_type);
        delete_button = findViewById(R.id.delete);


        things.setText(schedule.getInfo());
        item_start_time.setText(ConvertLongToString(schedule.getStartDateTime()));
        item_end_time.setText(ConvertLongToString(schedule.getEndDateTime()));
        show_type.setText(ConvertIntToString(schedule.getStatement())[0]);

        if(getStart(schedule.getStartDateTime()) < getStart(Calendar.getInstance().getTimeInMillis())){
            count_down.setText(CalculateDaysBefore(Calendar.getInstance().getTimeInMillis(), schedule.getStartDateTime()) + "天");
            before_or_after.setText("前");
        }else if(getStart(schedule.getStartDateTime()) > getStart(Calendar.getInstance().getTimeInMillis())){
            count_down.setText(CalculateDaysAfter(Calendar.getInstance().getTimeInMillis(), schedule.getStartDateTime()) + "天");
            before_or_after.setText("后");
        }else{
            count_down.setText("今天");
            before_or_after.setText("");
        }

        schedule_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImpDayDetailActivity.this, ImpDayEditActivity.class);
                intent.putExtra("schedule", schedule);
                startActivity(intent);
            }
        });
        types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImpDayDetailActivity.this, TypeActivity.class);
                int statements = dbAdapter.selectStatementsByTd(schedule.getId());
                String[] states = ConvertIntToString(statements);
                intent.putExtra("states", states);
                startActivity(intent);
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                showSetDeBugDialog();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        schedule = dbAdapter.selectOneById(schedule.getId());
        RefreshView();
    }


    private String[] ConvertIntToString(int statement){
        String[] states = new String[1];
        switch (statement){
            case 1:
                states[0] = "要事";
                break;
            case 2:
                states[0] = "请假";
                break;
            case 3:
                states[0] = "生日";
                break;
            case 4:
                states[0] = "会议";
                break;
            case -1:
                states[0] = "无";
                break;
        }
        return states;
    }
    private void RefreshView(){
        things.setText(schedule.getInfo());
        item_start_time.setText(ConvertLongToString(schedule.getStartDateTime()));
        item_end_time.setText(ConvertLongToString(schedule.getEndDateTime()));
        show_type.setText(ConvertIntToString(schedule.getStatement())[0]);
        if(getStart(schedule.getStartDateTime()) < getStart(Calendar.getInstance().getTimeInMillis())){
            count_down.setText(CalculateDaysBefore(Calendar.getInstance().getTimeInMillis(), schedule.getStartDateTime()) + "天");
            before_or_after.setText("前");
        }else if(getStart(schedule.getStartDateTime()) > getStart(Calendar.getInstance().getTimeInMillis())){
            count_down.setText(CalculateDaysAfter(Calendar.getInstance().getTimeInMillis(), schedule.getStartDateTime()) + "天");
            before_or_after.setText("后");
        }else{
            count_down.setText("今天");
            before_or_after.setText("");
        }

    }
    private String ConvertLongToString(long dateLong){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(dateLong);
        String res = simpleDateFormat.format(date);
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showSetDeBugDialog(){
        AlertDialog.Builder setDeBugDialog = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.confirm_view, null);
        setDeBugDialog.setView(dialogView);
        TextView but_cancel = dialogView.findViewById(R.id.but_cancel);
        TextView but_confirm = dialogView.findViewById(R.id.but_confirm);

        setDeBugDialog.setCancelable(false);
        setDeBugDialog.create();
        final AlertDialog alertDialog = setDeBugDialog.show();

        // 移除dialog的decorview背景色
        Objects.requireNonNull(alertDialog.getWindow()).getDecorView().setBackground(null);
        //设置自定义界面的点击事件逻辑
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        but_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbAdapter.deleteOneById(schedule.getId());
                shortCutUtil.remove(schedule.getId());
                showToast("删除成功！");
                alertDialog.dismiss();
                finish();
            }
        });
    }

    private int CalculateDaysBefore(long now_, long start_) {
        long now = getStart(now_);
        long start = getStart(start_);
        int days;
        for(days = 0; start < now; start += 86400, days++){

        }
        return days;
    }

    private int CalculateDaysAfter(long now_, long start_) {
        long now = getStart(now_);
        long start = getStart(start_);
        int days;
        for(days = 0; now < start; now += 86400, days++){

        }
        return days;
    }

    private long getStart(long time){
        return (time + 28800000) / 1000 - (time + 28800000) / 1000 % 86400;
    }


    private void showToast(String msg){
        Toast.makeText(ImpDayDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
