package com.hengxun.builder.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hengxun.builder.R;
import com.hengxun.builder.model.Marks;
import com.hengxun.builder.utils.widget.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZY on 2016/3/17.
 * 总积分列表
 */
public class MyMarksRvAdapter extends RecyclerView.Adapter<MyMarksRvAdapter.MarksViewHolder> {
    private List<Marks.DataMapEntity.PointsEntity> list = new ArrayList<>(); // 积分列表
    private Context context;

    public MyMarksRvAdapter(Context context) {
        this.context = context;
    }

    //第一次加数据和刷新
    public void addData(List<Marks.DataMapEntity.PointsEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    //加载
    public void addDataList(List<Marks.DataMapEntity.PointsEntity> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public MarksViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marks_rv, null);
        MarksViewHolder holder = new MarksViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MarksViewHolder holder, int position) {
        if (list != null) {
            holder.marksPoint_Tv.setText(list.get(position).getPoint() + "");
            if (list.get(position).getFinishTime() != 0) {
                holder.marksTime_Tv.setText(DateUtil.getStringTime(String.valueOf(list.get(position).getFinishTime())));
            }
            holder.marksType_Tv.setText(list.get(position).getService() + "");
        }
    }

    @Override
    public int getItemCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }


    public class MarksViewHolder extends RecyclerView.ViewHolder {
        private TextView marksPoint_Tv; // 金额
        private TextView marksTime_Tv;  // 时间
        private TextView marksType_Tv;  // 类型

        public MarksViewHolder(View v) {
            super(v);
            marksPoint_Tv = (TextView) v.findViewById(R.id.marksPoint_Tv);
            marksTime_Tv = (TextView) v.findViewById(R.id.marksTime_Tv);
            marksType_Tv = (TextView) v.findViewById(R.id.marksType_Tv);
        }
    }
}
