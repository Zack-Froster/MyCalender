package com.haibin.calendarviewproject.receiver;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.haibin.calendarviewproject.adapter.DBAdapter;
import com.haibin.calendarviewproject.entity.Schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageBroadcastReceiver extends BroadcastReceiver {
    private DBAdapter dbAdapter;
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("MyMessage"+ "matcher.group(0)");
        dbAdapter = new DBAdapter(context);
        dbAdapter.open();
        String regular = "^\\d{8,8}查询";

        Pattern pattern = Pattern.compile(regular);

        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        Object[] pdus = (Object[]) extras.get("pdus");
        SmsMessage message = null;
        for (Object pdu : pdus) {
            message = SmsMessage.createFromPdu((byte[]) pdu);
        }
        assert message != null;
        String address = message.getOriginatingAddress();   //来信电话号码
        if (address.contains("+86")) {
            address = address.substring(3);
        }
        String body = message.getMessageBody(); //短信内容
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()){
            int year = Integer.parseInt(body.substring(0, 4));
            int month = Integer.parseInt(body.substring(4, 6));
            int day = Integer.parseInt(body.substring(6, 8));
            String date = year + "年" + month + "月" + day + "日的日程：\n";
            String info = "";
            java.util.Calendar myCalendar= java.util.Calendar.getInstance();
            myCalendar.set(year, month-1, day);

            List<Schedule> infos = dbAdapter.selectAllByDate(myCalendar.getTimeInMillis());
            if(infos != null && infos.size() > 1) {
                int i;
                for (i = 0; i < infos.size() - 1; i++) {
                    info += infos.get(i).getInfo() + "\n";
                    info += "时间：" + ConvertLongToString(infos.get(i).getStartDateTime()) + "  -  " + ConvertLongToString(infos.get(i).getEndDateTime()) + "\n\n";
                }
                info += infos.get(i - 1).getInfo();
                info += "时间：" + ConvertLongToString(infos.get(i - 1).getStartDateTime()) + "  -  " + ConvertLongToString(infos.get(i - 1).getEndDateTime()) + "\n\n";
            } else if(infos != null && infos.size() == 1){
                info += infos.get(0).getInfo() + "\n";
                info += "时间：" + ConvertLongToString(infos.get(0).getStartDateTime()) + "  -  " + ConvertLongToString(infos.get(0).getEndDateTime());
            }
            String back = date + info;
            SmsManager backMessage = SmsManager.getDefault();
            backMessage.sendTextMessage(address, null, back, null, null);
            Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
        }
    }
    private String ConvertLongToString(long dateLong){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分");
        Date date = new Date(dateLong);
        String res = simpleDateFormat.format(date);
        return res;
    }
}
