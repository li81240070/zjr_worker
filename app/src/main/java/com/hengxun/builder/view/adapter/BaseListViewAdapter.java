package com.hengxun.builder.view.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * listview的基类适配器
 * Created by ge on 2016/3/16.
 */
public abstract class BaseListViewAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> list;

    public BaseListViewAdapter(Context context, List<T> list){
        this.context = context;
        this.list = list == null ? new ArrayList<T>() : new ArrayList<T>(list);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= list.size())
            return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 该方法需要子类实现
     * 返回resource Id
     * @ return
     * **/
    public abstract int getItemResource();

    /**
     * 使用本方法 替换原来的getView方法
     * 需要子类实现
     * **/
    public abstract View getItemView(int position, View convertView, BaseViewHolder holder);


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(context, getItemResource(), null);
            holder = new BaseViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (BaseViewHolder) convertView.getTag();
        }
        return getItemView(position, convertView, holder);
    }


    public class BaseViewHolder{
        private SparseArray<View> views = new SparseArray<View>();
        private View convertView;

        public BaseViewHolder(View convertView){
            this.convertView = convertView;
        }

        public <T extends View> T getView(int resId){
            View v = views.get(resId);
            if (v == null){
                v = convertView.findViewById(resId);
                views.put(resId, v);
            }
            return (T) v;
        }
    }

    public void addData(List<T> emm){
        list.addAll(emm);
        notifyDataSetChanged();
    }

    public void remove(int index){
        list.remove(index);
        notifyDataSetChanged();
    }

    public void remove(T index){
        list.remove(index);
        notifyDataSetChanged();
    }

    public void repleaceAll(List<T> emm){
        list.clear();
        list.addAll(emm);
        notifyDataSetChanged();
    }




}
