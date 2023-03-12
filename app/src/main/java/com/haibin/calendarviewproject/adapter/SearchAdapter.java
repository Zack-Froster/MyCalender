package com.haibin.calendarviewproject.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.haibin.calendarviewproject.entity.ScheduleListItem;
import com.haibin.calendarviewproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private List<ScheduleListItem> scheduleItems;

    private LayoutInflater inflater;
    private OnShowItemClickListener onShowItemClickListener;

    public List<ScheduleListItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(List<ScheduleListItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    public SearchAdapter(Context context, List<ScheduleListItem> scheduleList){
        this.context = context;
        this.scheduleItems = scheduleList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return scheduleItems.size();
    }

    @Override
    public Object getItem(int i) {
        return scheduleItems.get(i).getSchedule();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    //填充每个item的内容
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        SearchViewHolder viewHolder = null;
        if(convertView == null){
            //创建ViewHolder对象
            viewHolder = new SearchViewHolder();
            //加载布局文件，将布局文件转换成View对象
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item, null);
            //实例化ViewHolder
            viewHolder.things = convertView.findViewById(R.id.things);
            viewHolder.item_time = convertView.findViewById(R.id.item_time);
            viewHolder.checkbox = convertView.findViewById(R.id.checkbox);
            //将viewHolder的对象存储到View中
            convertView.setTag(viewHolder);
        }else {
            //取出ViewHolder
            viewHolder = (SearchViewHolder)convertView.getTag();
        }
        final ScheduleListItem schedule = scheduleItems.get(position);

        if(schedule.isShow()){
            Animation animationIn = AnimationUtils.loadAnimation(this.context, R.anim.checkbox_in);
            viewHolder.checkbox.setVisibility(View.VISIBLE);
            viewHolder.checkbox.setAnimation(animationIn);

        }else {
            Animation animationOut = AnimationUtils.loadAnimation(this.context, R.anim.checkbox_out);
            viewHolder.checkbox.setVisibility(View.GONE);
            viewHolder.checkbox.setAnimation(animationOut);
        }
        viewHolder.things.setText(scheduleItems.get(position).getSchedule().getInfo());
        viewHolder.item_time.setText(ConvertLongToString(scheduleItems.get(position).getSchedule().getStartDateTime()) + " - " + ConvertLongToString(scheduleItems.get(position).getSchedule().getEndDateTime()));
        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    schedule.setChecked(true);
                }else{
                    schedule.setChecked(false);
                }
                //回调方法，将Item加入已选
                onShowItemClickListener.onShowItemClick(schedule);
            }
        });
        //必须放在监听后面
        viewHolder.checkbox.setChecked(schedule.isChecked());
        return convertView;
    }
    private String ConvertLongToString(long dateLong){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date = new Date(dateLong);
        String res = simpleDateFormat.format(date);
        return res;
    }

    static class SearchViewHolder{
        TextView things;
        TextView item_time;
        CheckBox checkbox;
    }
    public interface OnShowItemClickListener{
        public void onShowItemClick(ScheduleListItem item);
    }
    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener){
        this.onShowItemClickListener = onShowItemClickListener;
    }

}
