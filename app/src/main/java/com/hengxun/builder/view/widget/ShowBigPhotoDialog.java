package com.hengxun.builder.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hengxun.builder.R;


/**
 * 展示大图片dialog
 * Created by ge on 2016/3/11.
 */
public class ShowBigPhotoDialog extends Dialog {

    private Context context;
    private int wid, hei;//dialog宽高
    private String uriStr;//uri
    private ImageView bigPhotoIV;//大图片

    public ShowBigPhotoDialog(Context context, String uriStr, int wid, int hei) {
        super(context, R.style.OrderInforDialog);
        this.context = context;
        this.uriStr = uriStr;
        this.wid = wid;
        this.hei = hei;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_big_photo);
        bigPhotoIV = (ImageView) findViewById(R.id.bigPhotoIV);
//        bigPhotoIV.setImageBitmap(JrUtils.commpressImageFromFile(context, uriStr));
        bigPhotoIV.setImageURI(Uri.parse(uriStr));
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(uriStr);
//            Bitmap bitmap = BitmapFactory.decodeStream(fis);
//            bigPhotoIV.setImageBitmap(bitmap);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        bigPhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBigPhotoListener != null){
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



    private OnClickBigPhotoListener onClickBigPhotoListener;

    public void setOnClickBigPhotoListener(OnClickBigPhotoListener onClickBigPhotoListener){
        this.onClickBigPhotoListener = onClickBigPhotoListener;
    }

    public interface OnClickBigPhotoListener{
        void onClickBigPhotol(View view);
    }

}
