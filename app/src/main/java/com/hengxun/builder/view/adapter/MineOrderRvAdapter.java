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
 * Created by ZY on 2016/3/17.
 */
public class MineOrderRvAdapter extends RecyclerView.Adapter<MineOrderRvAdapter.MineOrderHolder> {
    private Context context;
    private List<OrderList.DataMapEntity.OrdersEntity> list = new ArrayList<>(); // 匠人的订单列表
    private OnItemClickListener itemClickListener;

    public MineOrderRvAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<OrderList.DataMapEntity.OrdersEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addDataList(List<OrderList.DataMapEntity.OrdersEntity> list) {
//        this.list = list;
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public MineOrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_mine_order_rv, null);
        MineOrderHolder holder = new MineOrderHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MineOrderHolder holder, int position) {
        if (list != null) {
            holder.orderDate_Tv.setText(DateUtil.getStringTime(String.valueOf(list.get(position).getAppoint_time())));
            holder.orderAddress_Tv.setText(list.get(position).getAddress());
//            holder.orderDetails_Tv.setText(list.get(position).getComment());
            holder.orderType_Tv.setText(list.get(position).getService());

            // 订单状态
            // ZY 2016年5月19日13:17:45
            // 订单状态颜色注掉了  个人觉得不合理
            switch (list.get(position).getOrder_status()) {
                case "0": // 已取消
                    holder.orderState_Tv.setText(R.string.order_cancel);
//                    holder.orderState_Tv.setTextColor(context.getResources().getColor(R.color.toolbar_title));
                    break;

                case "1": // 可抢单
                    break;

                case "2": // 已预约
                    holder.orderState_Tv.setText(R.string.order_appointment);
//                    holder.orderState_Tv.setTextColor(context.getResources().getColor(R.color.my_orders_tv));
                    break;

                case "3": // 预约中
                    holder.orderState_Tv.setText(R.string.order_appointing);
//                    holder.orderState_Tv.setTextColor(context.getResources().getColor(R.color.my_orders_tv));
                    break;

                case "4": // 已开工
                    holder.orderState_Tv.setText(R.string.order_ongoing);
//                    holder.orderState_Tv.setTextColor(context.getResources().getColor(R.color.primary_color));
                    break;

                case "5": // 已完工
                    holder.orderState_Tv.setText(R.string.order_not_pay);
//                    holder.orderState_Tv.setTextColor(context.getResources().getColor(R.color.toolbar_title));
                    break;

                case "6": // 待付款
                    holder.orderState_Tv.setText(R.string.order_wait_for_payment);
//                    holder.orderState_Tv.setTextColor(context.getResources().getColor(R.color.person_btn));
                    break;

                case "7": // 已付款
                    holder.orderState_Tv.setText(R.string.order_paid);
                    break;
                case "8": // 未知 ?

                case "99": // 未知 ?
                default:
                    holder.orderState_Tv.setText(R.string.order_complete);
//                    holder.orderState_Tv.setTextColor(context.getResources().getColor(R.color.toolbar_title));
                    break;

            }
        }

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    itemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    public class MineOrderHolder extends RecyclerView.ViewHolder {
        private TextView orderDate_Tv;      // 订单日期
        private TextView orderType_Tv;      // 订单分类
//        private TextView orderDetails_Tv;   // 订单详情
        private TextView orderAddress_Tv;   // 订单地址
        private TextView orderState_Tv;     // 订单状态

        public MineOrderHolder(View v) {
            super(v);
            orderDate_Tv = (TextView) v.findViewById(R.id.orderDate_Tv);
            orderType_Tv = (TextView) v.findViewById(R.id.orderType_Tv);
//            orderDetails_Tv = (TextView) v.findViewById(R.id.orderDetails_Tv);
            orderAddress_Tv = (TextView) v.findViewById(R.id.orderAddress_Tv);
            orderState_Tv = (TextView) v.findViewById(R.id.orderState_Tv);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
