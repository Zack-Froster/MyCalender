package com.haibin.calendarviewproject.custom;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.haibin.calendarviewproject.entity.Schedule;
import com.haibin.calendarviewproject.entity.ScheduleListItem;
import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.adapter.DBAdapter;
import com.haibin.calendarviewproject.adapter.SearchAdapter;
import com.haibin.calendarviewproject.base.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends BaseActivity implements SearchAdapter.OnShowItemClickListener {

    //组件
    private SearchView search_bar;
    private ListView search_result;
    private ImageButton search_back;
    private RelativeLayout lay_up;
    private LinearLayout lay_down;

    private Schedule schedule;
    private SearchAdapter searchAdapter;

    private DBAdapter dbAdapter;
    private List<ScheduleListItem> scheduleList;
    private List<ScheduleListItem> selectedList;

    private static boolean isShow = false; // 是否显示CheckBox标识

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        dbAdapter = new DBAdapter(SearchActivity.this);
        dbAdapter.open();

        scheduleList = new ArrayList<ScheduleListItem>();
        selectedList = new ArrayList<ScheduleListItem>();
        scheduleList = ConvertScheduleToItem(dbAdapter.selectAll());
        //创建适配器
        searchAdapter = new SearchAdapter(SearchActivity.this, scheduleList);

        //组件
        search_bar = findViewById(R.id.search_bar);
        search_result = findViewById(R.id.search_result);
        search_back = findViewById(R.id.search_back);
        lay_up = findViewById(R.id.lay_up);
        lay_down = findViewById(R.id.lay_down);

        //ListView绑定适配器
        search_result.setAdapter(searchAdapter);
        searchAdapter.setOnShowItemClickListener(this);

        search_bar.setIconifiedByDefault(false);
        //搜索框输入和提交事件监听
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //添加下面一句,防止数据两次加载
                search_bar.setIconified(true);
                scheduleList = ConvertScheduleToItem(dbAdapter.selectAllByInfo(s));
                searchAdapter.setScheduleItems(scheduleList);

                searchAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                scheduleList = ConvertScheduleToItem(dbAdapter.selectAllByInfo(s));
                searchAdapter.setScheduleItems(scheduleList);
                searchAdapter.notifyDataSetChanged();
                return true;
            }
        });

        //搜索框的关闭事件
        search_bar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //添加关闭事件
                scheduleList = ConvertScheduleToItem(dbAdapter.selectAll());
                //重新刷新listview组件
                searchAdapter.setScheduleItems(scheduleList);
                searchAdapter.notifyDataSetChanged();
                return true;
            }
        });

        search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(isShow){
                    ScheduleListItem scheduleListItem = scheduleList.get(position);
                    boolean isChecked = scheduleListItem.isChecked();
                    if(isChecked) {
                        scheduleListItem.setChecked(false);
                    } else {
                        scheduleListItem.setChecked(true);
                    }
                    searchAdapter.notifyDataSetChanged();
                }
                else{
                    schedule = scheduleList.get(position).getSchedule();
                    Intent intent = new Intent(SearchActivity.this, ScheduleDetailActivity.class);
                    intent.putExtra("schedule", schedule);
                    startActivity(intent);
                }
            }
        });
        search_result.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(isShow){
                    return false;
                }else{
                    isShow = true;
                    for (ScheduleListItem item : scheduleList){
                        item.setShow(true);
                    }
                    searchAdapter.notifyDataSetChanged();
                    showOperate();
                    search_result.setLongClickable(false);
                }
                return true;
            }
        });

    }
    @Override
    protected void initData() {

    }
    @Override
    protected void onResume() {
        super.onResume();
        scheduleList = ConvertScheduleToItem(dbAdapter.selectAll());

        RefreshView();
    }
    private void RefreshView(){
        searchAdapter.setScheduleItems(scheduleList);
        searchAdapter.notifyDataSetChanged();
    }
    private List<ScheduleListItem> ConvertScheduleToItem(List<Schedule> scheduleList){
        List<ScheduleListItem> scheduleListItems = new ArrayList<>();
        for(int i = 0; i < scheduleList.size(); i++){
            scheduleListItems.add(new ScheduleListItem(scheduleList.get(i), false, false));
        }
        return scheduleListItems;
    }

    private void showOperate(){
        Animation animationUpIn = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.operate_up_in);
        Animation animationDownIn = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.operate_down_in);
        lay_up.setVisibility(View.VISIBLE);
        lay_up.setAnimation(animationUpIn);
        lay_down.setVisibility(View.VISIBLE);
        lay_down.setAnimation(animationDownIn);

        TextView select_all = findViewById(R.id.select_all);
        TextView select_none = findViewById(R.id.select_none);
        RelativeLayout delete = findViewById(R.id.delete);

        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isCheckAll()) {
                    for (ScheduleListItem item : scheduleList) {
                        if (!item.isChecked()) {
                            item.setChecked(true);
                            if (!selectedList.contains(item)) {
                                selectedList.add(item);
                            }
                        }else if(item.isChecked() && !selectedList.contains(item)){
                            selectedList.add(item);
                        }
                    }
                    searchAdapter.notifyDataSetChanged();
                }else {
                    selectedList.clear();
                    for (ScheduleListItem item : scheduleList) {
                        item.setChecked(false);
                    }
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });
        select_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShow){
                    selectedList.clear();
                    for(ScheduleListItem item : scheduleList){
                        item.setChecked(false);
                        item.setShow(false);
                    }
                    searchAdapter.notifyDataSetChanged();
                    isShow = false;
                    search_result.setLongClickable(true);
                    disMissOperate();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if(selectedList != null && selectedList.size() > 0){
                    showSetDeBugDialog();
                    searchAdapter.setScheduleItems(scheduleList);
                    searchAdapter.notifyDataSetChanged();
                }else{
                    showToast("未选择条目");
                }
            }
        });

    }

    private void disMissOperate(){
        Animation animationUpOut = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.operate_up_out);
        Animation animationDownOut = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.operate_down_out);

        lay_up.setVisibility(View.GONE);
        lay_up.setAnimation(animationUpOut);
        lay_down.setVisibility(View.GONE);
        lay_down.setAnimation(animationDownOut);
    }
    @Override
    public void onShowItemClick(ScheduleListItem item) {
        if(item.isChecked() && !selectedList.contains(item)){
            selectedList.add(item);
        }else if(!item.isChecked() && selectedList.contains(item)){
            selectedList.remove(item);
        }
    }
    @Override
    public void onBackPressed() {
        if (isShow) {
            selectedList.clear();
            for (ScheduleListItem item : scheduleList) {
                item.setChecked(false);
                item.setShow(false);
            }
            searchAdapter.notifyDataSetChanged();
            isShow = false;
            search_result.setLongClickable(true);
            disMissOperate();
        } else {
            super.onBackPressed();
        }
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
                scheduleList.removeAll(selectedList);
                for(int i = 0; i < selectedList.size(); i++) {
                    dbAdapter.deleteOneById(selectedList.get(i).getSchedule().getId());
                }
                showToast("删除成功！");
                alertDialog.dismiss();
                if(isShow){
                    selectedList.clear();
                    for(ScheduleListItem item : scheduleList){
                        item.setChecked(false);
                        item.setShow(false);
                    }
                    searchAdapter.notifyDataSetChanged();
                    isShow = false;
                    search_result.setLongClickable(true);
                    disMissOperate();
                }
            }
        });
    }
    private boolean isCheckAll(){
        return  this.selectedList.size() == this.scheduleList.size() ? true : false;
    }
    private void showToast(String msg){
        Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
