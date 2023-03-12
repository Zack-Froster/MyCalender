package com.haibin.calendarviewproject.custom;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.haibin.calendarviewproject.entity.Schedule;
import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.adapter.DBAdapter;
import com.haibin.calendarviewproject.base.activity.BaseActivity;

import java.text.SimpleDateFormat;
;
import java.util.Date;

import java.util.Objects;

public class ScheduleDetailActivity extends BaseActivity {
    private ImageButton schedule_detail_back;
    private TextView schedule_edit;
    private TextView things;
    private TextView item_start_time;
    private TextView item_end_time;
    private RelativeLayout types;
    private RelativeLayout delete_button;
    private TextView show_type;

    private DBAdapter dbAdapter;
    private Schedule schedule;


    @Override
    protected int getLayoutId() {
        return R.layout.schedule_detail;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        schedule = intent.getParcelableExtra("schedule");

        schedule_detail_back = findViewById(R.id.schedule_detail_back);
        schedule_edit = findViewById(R.id.schedule_edit);
        things = findViewById(R.id.things);
        item_start_time = findViewById(R.id.item_start_time);
        item_end_time = findViewById(R.id.item_end_time);
        types = findViewById(R.id.types);
        show_type = findViewById(R.id.show_type);
        delete_button = findViewById(R.id.delete);


        things.setText(schedule.getInfo());
        item_start_time.setText(ConvertLongToString(schedule.getStartDateTime()));
        item_end_time.setText(ConvertLongToString(schedule.getEndDateTime()));
        show_type.setText(ConvertIntToString(schedule.getStatement())[0]);



        schedule_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        schedule_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleDetailActivity.this, EditScheduleActivity.class);
                intent.putExtra("schedule", schedule);
                startActivity(intent);
            }
        });
        types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleDetailActivity.this, TypeActivity.class);
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
        dbAdapter = new DBAdapter(ScheduleDetailActivity.this);
        dbAdapter.open();

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
                showToast("删除成功！");
                alertDialog.dismiss();
                finish();
            }
        });
    }
    private void showToast(String msg){
        Toast.makeText(ScheduleDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
