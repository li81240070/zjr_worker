package com.hengxun.builder.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hengxun.builder.R;
import com.hengxun.builder.model.UserInfo;

import java.util.List;

/**
 * Created by ZY on 2016/3/26.
 */
public class MyGiftRvAdapter extends RecyclerView.Adapter<MyGiftRvAdapter.MyGiftViewHolder> {
    private UserInfo.DataMapEntity entity; // 用户信息
    private Context context;
    private List<String> giftNum; // 礼物的数量

    public MyGiftRvAdapter(Context context) {
        this.context = context;
    }

    public void addData(UserInfo.DataMapEntity datas) {
        this.entity = datas;
        giftNum = entity.getGift_val();
        notifyDataSetChanged();
    }

    @Override
    public MyGiftViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_gift_rv, null);
        MyGiftViewHolder holder = new MyGiftViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyGiftViewHolder holder, int position) {
        if (entity != null) {
            holder.gift_Tv.setText(giftNum.get(position));
            // 如果用户积分比这档积分高
            int marks = entity.getPoints();
            if (marks >= Integer.parseInt(giftNum.get(position))) {
                holder.gift_Iv.setImageResource(R.mipmap.gift_full);
            }
        }
    }

    @Override
    public int getItemCount() {
        return giftNum != null && giftNum.size() > 0 ? giftNum.size() : 0;
    }

    public class MyGiftViewHolder extends RecyclerView.ViewHolder {
        private TextView gift_Tv;   // 礼物条每一级的数目
        private ImageView gift_Iv;  // 礼物图标

        public MyGiftViewHolder(View v) {
            super(v);
            gift_Tv = (TextView) v.findViewById(R.id.gift_Tv);
            gift_Iv = (ImageView) v.findViewById(R.id.gift_Iv);
        }
    }
}
