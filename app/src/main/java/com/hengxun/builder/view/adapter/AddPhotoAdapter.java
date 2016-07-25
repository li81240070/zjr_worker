package com.hengxun.builder.view.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hengxun.builder.R;
import com.hengxun.builder.model.ImageUriEntity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加照片适配器
 * Created by ge on 2016/3/9.
 */
public class AddPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPEFIRST = 00;
    private static final int TYPELAST = 01;
    private static final int TYPETHREE = 02;
    private List<ImageUriEntity> list = new ArrayList<ImageUriEntity>();
    private int type = 0;
    private FileInputStream inputStream;
    private Context context;

    public AddPhotoAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<ImageUriEntity> list, int type) {
        this.list.clear();
        this.list.addAll(list);
        this.type = type;
        notifyDataSetChanged();
    }

    public void addItem(ImageUriEntity entity) {
        this.list.add(entity);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.list.remove(position);
        notifyDataSetChanged();
    }

    private boolean isLength() {
        return list != null && list.size() > 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View v = null;
        switch (viewType) {
            case TYPEFIRST://显示
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_add_photo, null);
                viewHolder = new ImageViewHolder(v);
                break;
            case TYPELAST://加
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pulse_img, null);
                viewHolder = new PlusViewHolder(v);
                break;
            case TYPETHREE://删除布局
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_clear_photo, null);
                viewHolder = new ImageClearHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Bitmap bitmap = null;
        InputStream stream = null;
        ContentResolver contentResolver = context.getContentResolver();
        switch (getItemViewType(position)) {
            case TYPEFIRST://显示
                if (type == 0) {
                    ImageUriEntity iue = list.get(position);
//                    final String largStr = iue.getLargImgPath();
//                    Uri uri = Uri.parse(largStr);
//                    bitmap = getBitmapFromUri(uri);
//                    ((ImageViewHolder) holder).showAddPhotoIV.setImageBitmap(bitmap);

                    final String largStr = iue.getLargImgPath();
                    Uri uri = Uri.parse(largStr);
                    try {
                        stream = contentResolver.openInputStream(uri);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = 2;
                        bitmap = BitmapFactory.decodeStream(stream, null, options);
                        ((ImageViewHolder) holder).showAddPhotoIV.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    ((ImageViewHolder) holder).showAddPhotoIV.setOnLongClickListener(new View.OnLongClickListener() {//长按照片
                        @Override
                        public boolean onLongClick(View v) {
                            if (listener != null) {
                                listener.onLongType(1);
                            }
                            return true;
                        }
                    });
                    ((ImageViewHolder) holder).showAddPhotoIV.setOnClickListener(new View.OnClickListener() {//点击
                        @Override
                        public void onClick(View v) {
                            if (listener != null) {
                                listener.onClickTouch(largStr);
                            }
                        }
                    });
                    bitmap = null;
                }
                break;
            case TYPELAST://加
                if (position % 4 == 0 && position != 0) {
                    ((PlusViewHolder) holder).addPulseIV.setVisibility(View.GONE);
                } else {
                    ((PlusViewHolder) holder).addPulseIV.setVisibility(View.VISIBLE);
                }
                break;
            case TYPETHREE://删除图片
                ImageUriEntity iueClear = list.get(position);
//                String strClear = iueClear.getLargImgPath();
//                bitmap = getBitmapFromUri(Uri.parse(strClear));
                final String largStr = iueClear.getLargImgPath();
                Uri uri = Uri.parse(largStr);
                try {
                    stream = contentResolver.openInputStream(uri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 4;
                    bitmap = BitmapFactory.decodeStream(stream, null, options);
                    ((ImageClearHolder) holder).showClearPhotoIV.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ((ImageClearHolder) holder).clearBtn.setOnClickListener(new View.OnClickListener() {//点击删除按钮
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.remove(position);
                        }
                    }
                });
                bitmap = null;
                break;
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return isLength() ? list.size() + 1 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (type == 0) {
            if (position == getItemCount() - 1) {
                return TYPELAST;
            }
            return TYPEFIRST;
        } else {
            if (position == getItemCount() - 1) {
                return TYPELAST;
            }
            return TYPETHREE;
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView showAddPhotoIV;

        public ImageViewHolder(View itemView) {
            super(itemView);
            showAddPhotoIV = (ImageView) itemView.findViewById(R.id.showAddPhotoIV);
        }
    }

    class PlusViewHolder extends RecyclerView.ViewHolder {
        ImageView addPulseIV;//加号

        public PlusViewHolder(View itemView) {
            super(itemView);
            addPulseIV = (ImageView) itemView.findViewById(R.id.addPulseIV);
            addPulseIV.setOnClickListener(new View.OnClickListener() {//添加照片
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.plus();
                    }
                }
            });
        }
    }

    class ImageClearHolder extends RecyclerView.ViewHolder {
        ImageView showClearPhotoIV, clearBtn;

        public ImageClearHolder(View itemView) {
            super(itemView);
            showClearPhotoIV = (ImageView) itemView.findViewById(R.id.showClearPhotoIV);
            clearBtn = (ImageView) itemView.findViewById(R.id.clearBtn);
        }
    }


    /**
     * 自定义接口回调
     * plus 添加照片 加号点击
     * remove 移除照片 减号
     * onLongType 长按图片
     * onClickTouch 图片点击
     **/
    public interface ButtonClickListener {
        void plus();

        void remove(int position);

        void onLongType(int type);

        void onClickTouch(String uriStr);
    }

    private ButtonClickListener listener;

    public void setButtonClickListener(ButtonClickListener listener) {
        this.listener = listener;
    }

}
