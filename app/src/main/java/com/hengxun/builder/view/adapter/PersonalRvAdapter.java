package com.hengxun.builder.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hengxun.builder.R;
import com.hengxun.builder.model.PersonalAccount;
import com.hengxun.builder.utils.widget.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZY on 2016/4/1.
 * 账户列表
 */
public class PersonalRvAdapter extends RecyclerView.Adapter<PersonalRvAdapter.PersonalViewHolder> {
    private Context context;
    private List<PersonalAccount.DataMapEntity.IncomesEntity> list = new ArrayList<>(); // 账户列表

    public PersonalRvAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<PersonalAccount.DataMapEntity.IncomesEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addDataLoad(List<PersonalAccount.DataMapEntity.IncomesEntity> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public PersonalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_marks_rv, null);
        PersonalViewHolder holder = new PersonalViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(PersonalViewHolder holder, int position) {
        if (list != null) {
            holder.marksType_Tv.setText(list.get(position).getContent());
            double d = list.get(position).getAmount();
            String st = String.format("%.2f", d);
            if (list.get(position).getAccount_type() == 1) {
                holder.marksPoint_Tv.setText("-" + st);
            } else {
                holder.marksPoint_Tv.setText("+" + st);
            }

            holder.marksTime_Tv.setText(DateUtil.getStringTime(String.valueOf(list.get(position).getUpdate_time())));
        }
    }

    @Override
    public int getItemCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    public class PersonalViewHolder extends RecyclerView.ViewHolder {
        private TextView marksType_Tv;      // 维修类型
        private TextView marksTime_Tv;      // 维修时间
        private TextView marksPoint_Tv;     // 每单金额

        public PersonalViewHolder(View v) {
            super(v);
            marksType_Tv = (TextView) v.findViewById(R.id.marksType_Tv);
            marksTime_Tv = (TextView) v.findViewById(R.id.marksTime_Tv);
            marksPoint_Tv = (TextView) v.findViewById(R.id.marksPoint_Tv);
        }
    }
}
