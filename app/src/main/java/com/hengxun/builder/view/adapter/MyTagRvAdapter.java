package com.hengxun.builder.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hengxun.builder.R;

import java.util.List;

/**
 * Created by ZY on 2016/3/26.
 */
public class MyTagRvAdapter extends RecyclerView.Adapter<MyTagRvAdapter.MyTagViewHolder> {
    private List<String> tags; // 匠人标签
    private Context context;

    public MyTagRvAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<String> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }

    @Override
    public MyTagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_tags_rv, null);
        MyTagViewHolder holder = new MyTagViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyTagViewHolder holder, int position) {
        if (tags != null) {
            holder.myTags_Tv.setText(tags.get(position).toString());
        }
    }

    @Override
    public int getItemCount() {
//        if (tags != null && tags.size() > 0) {
//            int num = tags.size() % 4;
//            // 若能被4整除
//            if (num == 0) {
//                return tags.size() / 4;
//            } else {
//                return (tags.size() / 4) + 1;
//            }
//        } else {
//            return 0;
//        }
        return tags != null && tags.size() > 0 ? tags.size() : 0;
    }

    public class MyTagViewHolder extends RecyclerView.ViewHolder {
        private TextView myTags_Tv;

        public MyTagViewHolder(View v) {
            super(v);
            myTags_Tv = (TextView) v.findViewById(R.id.myTags_Tv); // 标签
        }
    }
}
