package com.hengxun.builder.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hengxun.builder.R;
import com.hengxun.builder.model.OrderList;
import com.hengxun.builder.utils.widget.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZY on 2016/3/29.
 * 首页订单列表适配器
 */
public class HomeRvAdapter extends RecyclerView.Adapter<HomeRvAdapter.HomeViewHolder> {
    private Context context;
    private List<OrderList.DataMapEntity.OrdersEntity> list = new ArrayList<>();
    private OnItemClickListener itemClickListener; // 点击进入订单详情

    public HomeRvAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<OrderList.DataMapEntity.OrdersEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addDataLoad(List<OrderList.DataMapEntity.OrdersEntity> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_rv, null);
        HomeViewHolder holder = new HomeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HomeViewHolder holder, int position) {
        if (list != null) {
            holder.orderType_Tv.setText(list.get(position).getService());
            holder.orderAddress_Tv.setText(list.get(position).getAddress());
            holder.orderTime_Tv.setText(DateUtil.getStrTime(String.valueOf(list.get(position).getAppoint_time())));
            holder.orderMoney_Tv.setText("¥ " + list.get(position).getPrice());
        }

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position >= 0 && position < list.size()) {
                        itemClickListener.onItemClick(holder.itemView, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private TextView orderType_Tv; // 订单类别
        private TextView orderTime_Tv; // 订单时间
        private TextView orderMoney_Tv; // 订单金额
        private TextView orderAddress_Tv; // 订单地址

        public HomeViewHolder(View v) {
            super(v);
            orderType_Tv = (TextView) v.findViewById(R.id.orderType_Tv);
            orderTime_Tv = (TextView) v.findViewById(R.id.orderTime_Tv);
            orderMoney_Tv = (TextView) v.findViewById(R.id.orderMoney_Tv);
            orderAddress_Tv = (TextView) v.findViewById(R.id.orderAddress_Tv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
