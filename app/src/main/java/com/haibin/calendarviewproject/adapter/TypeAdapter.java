package com.haibin.calendarviewproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haibin.calendarviewproject.R;

import java.util.List;

public class TypeAdapter extends BaseAdapter {
    private Context context;
    private List<String> statementList;

    public TypeAdapter(Context context, List<String> statementList) {
        this.context = context;
        this.statementList = statementList;
    }

    @Override
    public int getCount() {
        return statementList.size();
    }

    @Override
    public Object getItem(int i) {
        return statementList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = null;
        TypeHolder typeHolder = null;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.type_item, null);
            typeHolder = new TypeHolder();
            typeHolder.one_item_type = view.findViewById(R.id.one_item_type);
            view.setTag(typeHolder);
        } else {
            view = convertView;
            typeHolder = (TypeHolder) view.getTag();
        }
        typeHolder.one_item_type.setText(statementList.get(position));
        return view;
    }

    static class TypeHolder {
        TextView one_item_type;
    }
}
