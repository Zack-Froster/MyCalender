package com.haibin.calendarviewproject.custom;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.haibin.calendarviewproject.R;
import com.haibin.calendarviewproject.adapter.TypeAdapter;
import com.haibin.calendarviewproject.base.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class TypeActivity extends BaseActivity {
    private ListView type_list;
    private ImageButton type_back;
    private String[] states;
    private TypeAdapter typeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.type_activity;
    }

    @Override
    protected void initView() {
        type_list = findViewById(R.id.type_list);
        type_back = findViewById(R.id.type_back);
        type_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        states = intent.getStringArrayExtra("states");
        typeAdapter = new TypeAdapter(TypeActivity.this, ConvertArrayToList(states));
        type_list.setAdapter(typeAdapter);

    }
    private List<String> ConvertArrayToList(String[] states){
        List<String> stateList = new ArrayList<>();
        for(int i = 0; i< states.length; i++){
            stateList.add(states[i]);
        }
        return stateList;
    }
}
