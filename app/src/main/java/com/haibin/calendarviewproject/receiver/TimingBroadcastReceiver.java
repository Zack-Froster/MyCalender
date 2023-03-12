package com.haibin.calendarviewproject.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.Calendar;

public class TimingBroadcastReceiver extends BroadcastReceiver {
            /**
            * 通过广播进行扫描，是否到达时间后再响起闹铃
            * */
                @Override
        public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(
                                            "alarm_record", Activity.MODE_PRIVATE);
                String hour = String.valueOf(Calendar.getInstance().get(
                                                    Calendar.HOUR_OF_DAY));
                String minute = String.valueOf(Calendar.getInstance().get(
                                                 Calendar.MINUTE));
                String time = sharedPreferences.getString(hour + ":" + minute,null);// 小时与分，
                String haoma = sharedPreferences.getString("haoma",null);
//                String neirong = sharedPreferences.getString("neirong",null);
                    String neirong = "mm";
                if (time != null) {// 判断是否为空，然后通过创建，
//               MediaPlayer mediaPlayer = MediaPlayer.create(context,R.raw.a);
                    Toast.makeText(context,"短信已经发送成功",Toast.LENGTH_LONG).show();
//               mediaPlayer.start();// 开始 ;
                        sendMsg(haoma,neirong);
                }
        }
        private void sendMsg(String number,String message) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number,null,message,null, null);
        }
}
