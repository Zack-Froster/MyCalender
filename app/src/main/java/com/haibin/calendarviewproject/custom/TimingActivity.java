package com.haibin.calendarviewproject.custom;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.haibin.calendarviewproject.R;

import java.util.Calendar;

public class TimingActivity extends Activity {
    TextView onetextview;
    TextView twotextview;
    TextView threetextview;
    ImageView msgButtion;
    Button onebutton;
    Button twobutton;
    Dialog dialog = null;
    //新建日历对象，用来设置闹钟时间
    Calendar calendar = Calendar.getInstance();
    private SharedPreferences sharedPreferences;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        calendar.setTimeInMillis(System.currentTimeMillis());
        LinearLayout relativeLayout =(LinearLayout) findViewById(R.id.LinearLayout);
        relativeLayout.setBackgroundResource(R.drawable.bejing);

        msgButtion = findViewById(R.id.msg_image);

        onebutton=(Button) findViewById(R.id.onebutton);
        onebutton.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        twobutton=(Button) findViewById(R.id.twobutton);
        twobutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        onetextview=(TextView) findViewById(R.id.onetextview);
        twotextview=(TextView) findViewById(R.id.twotextview);
        threetextview=(TextView) findViewById(R.id.threetextview);
        sharedPreferences=getSharedPreferences("alarm_record",Activity.MODE_PRIVATE);
        AlarmManager aManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this,AlarmReceiver.class);
        intent.setAction("AlarmReceiver");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0);
//      aManager.set(AlarmManager.RTC,pendingIntent);
        aManager.setRepeating(AlarmManager.RTC,pendingIntent);
    }
    public void dialog(){
        View view=getLayoutInflater().inflate(R.layout.shijian,null);//
        final TimePicker timePicker=(TimePicker)view.findViewById(R.id.timepicker);
        final EditText oneeditext=(EditText)view.findViewById(R.id.oneeditext);
        final EditText twoeditext=(EditText)view.findViewById(R.id.twoeditext);
        timePicker.setIs24HourView(true);
        new AlertDialog.Builder(this)
                .setTitle("设置")
                .setView(view)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        String timeStr=String.valueOf(timePicker.getCurrentHour())+":"+String.valueOf(timePicker.getCurrentMinute());
                /*calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE,55);*/
                        onetextview.setText("您设置的时间为: "+timeStr);
                        twotextview.setText("您设置的号码为: "+oneeditext.getText().toString());
                        threetextview.setText("您设置的内容为: "+twoeditext.getText().toString());
                        sharedPreferences.edit().putString(timeStr,timeStr).commit();
                        sharedPreferences.edit().putString("haoma",oneeditext.getText().toString()).commit();
                        sharedPreferences.edit().putString("neirong",twoeditext.getText().toString()).commit();
                    }
                }).setNegativeButton("取消",null).show();
    }
}
