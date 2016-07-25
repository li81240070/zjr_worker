package com.hengxun.builder.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.hengxun.builder.R;


/**
 * 等待时候展示得dialog
 * Created by ge on 2016/3/21.
 */
public class WaittingDiaolog extends Dialog {

    private Context context;
    private static int default_width = 250;
    private static int default_heigh = 180;

    public WaittingDiaolog(Context context) {
        super(context, R.style.RushToDealialog);
        this.context = context;
        init();
        setCanceledOnTouchOutside(false);
    }

    private void init() {
        setContentView(R.layout.dialog_waiting);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) dip2px(context, default_width);
        params.height = (int) dip2px(context, default_heigh);
        window.setAttributes(params);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }


}
