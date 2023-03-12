package com.haibin.calendarviewproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haibin.calendarviewproject.entity.Schedule;
import com.haibin.calendarviewproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ScheduleAdapter extends BaseAdapter {
    private Context context;
    private List<Schedule> scheduleList;

    public ScheduleAdapter(Context context, List<Schedule> scheduleList){
        this.context = context;
        this.scheduleList = scheduleList;

    }



    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int i) {
        return scheduleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    //填充每个item的内容
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view = null;
        ViewHolder viewHolder = null;
        if(convertView == null){
            //加载布局文件，将布局文件转换成View对象
            view = LayoutInflater.from(context).inflate(R.layout.schedule_item, null);
            //创建ViewHolder对象
            viewHolder = new ViewHolder();
            //实例化ViewHolder
            viewHolder.things = view.findViewById(R.id.things);
            viewHolder.item_time = view.findViewById(R.id.item_time);
            //将viewHolder的对象存储到View中
            view.setTag(viewHolder);
        }else {
            view = convertView;
            //取出ViewHolder
            viewHolder = (ViewHolder)view.getTag();
        }

        if(getItem(position) != null){
//            viewHolder.cb_select.setChecked(isSelected.get(position));
            viewHolder.things.setText(scheduleList.get(position).getInfo());
            viewHolder.item_time.setText(ConvertLongToString(scheduleList.get(position).getStartDateTime()) + " - " + ConvertLongToString(scheduleList.get(position).getEndDateTime()));
        }


        return view;
    }
    private String ConvertLongToString(long dateLong){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date = new Date(dateLong);
        String res = simpleDateFormat.format(date);
        return res;
    }
   static class ViewHolder{
        TextView things;
        TextView item_time;

    }
}
