package com.hengxun.builder.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hengxun.builder.R;
import com.hengxun.builder.utils.okhttp.HttpsController;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by ZY on 2016/4/1.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder> {
    private Context context;
    private List<String> img; // 照片
    private OnItemClickListener itemClickListener; // 照片点击事件
    private Handler handler;

    public OrderDetailsAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<String> img) {
        this.img = img;
        notifyDataSetChanged();
    }

    @Override
    public OrderDetailsViewHolder onCreateViewHolder(ViewGroup position, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.adapter_add_photo, null);
        OrderDetailsViewHolder holder = new OrderDetailsViewHolder(v);
        handler = new Handler();
        return holder;
    }

    @Override
    public void onBindViewHolder(final OrderDetailsViewHolder holder, final int position) {
        if (img != null) {
//            Picasso.with(context).load(img.get(position)).into(holder.showAddPhotoIV);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    OkHttpClient client = new OkHttpClient();
                    try {
                        client = HttpsController.setCertificates(context, context.getAssets().open("zhenjren.cer"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Request request = new Request.Builder().url(img.get(position)).build();
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()){
                            InputStream is = response.body().byteStream();
                            final Bitmap bm = BitmapFactory.decodeStream(is);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.showAddPhotoIV.setImageBitmap(bm);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
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

    /**
     * 下载头像
     * **/
    public void getAvater(String avatarUrl) throws IOException {

    }

    @Override
    public int getItemCount() {
        return img != null && img.size() > 0 ? img.size() : 0;
    }

    public class OrderDetailsViewHolder extends RecyclerView.ViewHolder {
        private ImageView showAddPhotoIV;

        public OrderDetailsViewHolder(View v) {
            super(v);
            showAddPhotoIV = (ImageView) v.findViewById(R.id.showAddPhotoIV);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
