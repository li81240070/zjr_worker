package com.hengxun.builder.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hengxun.builder.R;
import com.hengxun.builder.utils.okhttp.HttpsController;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;


/**
 * 展示大图片dialog
 * Created by ge on 2016/3/11.
 */
public class OrderPhotoDialog extends Dialog {

    private Context context;
    private int wid, hei;           //dialog宽高
    private String uriStr;          //uri
    private ImageView bigPhotoIV;   //大图片
    private Handler handler;

    public OrderPhotoDialog(Context context, String uriStr, int wid, int hei) {
        super(context, R.style.OrderInforDialog);
        this.context = context;
        this.uriStr = uriStr;
        this.wid = wid;
        this.hei = hei;
        handler = new Handler();
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_big_photo);
        bigPhotoIV = (ImageView) findViewById(R.id.bigPhotoIV);
//        Bitmap bitmap = getBitmapFromUri(Uri.parse(uriStr));
//        Picasso.with(context).load(uriStr).into(bigPhotoIV);
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
                    Request request = new Request.Builder().url(uriStr).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        InputStream is = response.body().byteStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bigPhotoIV.setImageBitmap(bm);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
//        bigPhotoIV.setImageBitmap(bitmap);
        bigPhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBigPhotoListener != null) {
                    onClickBigPhotoListener.onClickBigPhotol(v);
                }
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
//        JrUtils.getDensity(context);//透明度
        params.width = wid;
        params.height = hei;
        window.setAttributes(params);
    }

//    private Bitmap getBitmapFromUri(Uri uri) {
//        try {
//            // 读取uri所在的图片
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
//            return bitmap;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private OnClickBigPhotoListener onClickBigPhotoListener;

    public void setOnClickBigPhotoListener(OnClickBigPhotoListener onClickBigPhotoListener) {
        this.onClickBigPhotoListener = onClickBigPhotoListener;
    }

    public interface OnClickBigPhotoListener {
        void onClickBigPhotol(View view);
    }


}
