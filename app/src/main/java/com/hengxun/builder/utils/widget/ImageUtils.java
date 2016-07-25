package com.hengxun.builder.utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.MediaType;

import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ZY on 2016/3/23.
 */
public class ImageUtils {


    //将bitmap写入文件
    public static Uri creatFile(Bitmap heaBitmap){
        FileOutputStream fileOutputStream = null;
        String saveDir = Environment.getExternalStorageDirectory() + "/jr_ordor";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddssSSS");
        String fileName = "JR" + (dateFormat.format(new Date())) + ".jpg";
        File file = new File(saveDir, fileName);
        try {
            fileOutputStream = new FileOutputStream(file);
            heaBitmap.compress(Bitmap.CompressFormat.JPEG, 10, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

    public static Bitmap creatFilePhoto(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInsampleSize(options, 480, 800);
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        return bitmap;
    }


    public static int calculateInsampleSize(BitmapFactory.Options options, int reWidth, int reHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSiampleSize = 1;
        if (height > reHeight || width > reWidth){
            final int heightRatio = Math.round((float) height / (float) reHeight);
            final int widthRatio = Math.round((float) width / (float) reWidth);
            inSiampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSiampleSize;
    }

    /**
     * 传入相册或拍照的照片的uri转成string类型的地址
     * 经过压缩 返回bitmap  （原比例）
     * **/
    public static Bitmap commpressImageFromFile(Context context, String srcPath){
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//原来的方法调用了这个方法企图进行二次/压缩
        //其实是无效的,大家尽管尝试
//        return bitmap;
    }


    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Convert bitmap to string;
     * 将转换的字符串存入数组中
     * @param bitmap
     * @return
     */
    public static JSONArray bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 计算数组的长度
        JSONArray img = new JSONArray();
        int num = result.length() % 5000;
        int length = result.length() / 5000;
        if (num == 0) {
            for (int i = 0; i < length; i++) {
                img.put(result.substring(5000 * i, 5000 * i + 5000));
            }
        } else {
            for (int i = 0; i < length; i++) {
                img.put(result.substring(5000 * i, 5000 * i + 5000));
            }
            img.put(result.substring(5000 * length, result.length()));
        }

        return img;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
