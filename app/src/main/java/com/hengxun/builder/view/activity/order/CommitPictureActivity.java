package com.hengxun.builder.view.activity.order;

import android.app.Activity;
import android.app.Dialog;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.ImageUriEntity;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.utils.AppUtils;
import com.hengxun.builder.utils.okhttp.HttpsController;
import com.hengxun.builder.utils.widget.ImageUtils;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.adapter.AddPhotoAdapter;
import com.hengxun.builder.view.widget.ShowBigPhotoDialog;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 上传照片页面
 * Created by ge on 2016/3/28.
 */
public class CommitPictureActivity extends BaseActivity implements View.OnClickListener, AddPhotoAdapter.ButtonClickListener {
    private RecyclerView addPhotoRV;//添加照片列表
    private GridLayoutManager manager;//列表事物管理器
    private AddPhotoAdapter photoAdapter;//添加照片列表适配器
    private Button pop_window_head_photeBT, pop_window_head_audioBT, pop_window_head_cancleBT;
    private Uri imgUri;//更改拍照后照片输出路径
    private String imgPath;//拍照后照片输出路径
    private Dialog dialog;
    private List<ImageUriEntity> list = new ArrayList<>();
    private int type;//长按图片切换布局
    private Button cancelPhotoBtn;//取消按钮
    private Toast toast;
//    private UserInfo userInfo; // 用户信息
    //    private OrderList.DataMapEntity.OrdersEntity order; // 订单实体
    private String order_id;    // 订单id
    private WaittingDiaolog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_picture);
        showToolBar(getResources().getString(R.string.commit_picture), true, this);
        initView();
        initData();
        setListener();
    }

    @Override
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        super.showToolBar(titleName, isShow, activity);
        TextView toolbar_right_Tv = (TextView) activity.findViewById(R.id.toolbar_right_Tv);
        toolbar_right_Tv.setText(getResources().getString(R.string.commit));
        toolbar_right_Tv.setTextColor(getResources().getColor(R.color.order_commit_picture));
        toolbar_right_Tv.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        addPhotoRV = (RecyclerView) findViewById(R.id.addPhotoRV);//照片列表
        cancelPhotoBtn = (Button) findViewById(R.id.cancelPhotoBtn);//取消按钮
    }

    @Override
    protected void initData() {
        manager = new GridLayoutManager(this, 4);
        addPhotoRV.setLayoutManager(manager);
        photoAdapter = new AddPhotoAdapter(this);
        addPhotoRV.setAdapter(photoAdapter);
//        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
//        order = (OrderList.DataMapEntity.OrdersEntity) getIntent().getSerializableExtra("order");
        order_id = getIntent().getStringExtra("order_id");
    }

    @Override
    protected void setListener() {
        photoAdapter.setButtonClickListener(this);
        cancelPhotoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_right_Tv:
                progressDialog = new WaittingDiaolog(this);
                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setMessage("正在加载中...");
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show();
                }
                new Thread(commitImgTask).start(); // 上传照片
                break;
            case R.id.cancelPhotoBtn://取消
                photoAdapter.addData(list, 0);
                cancelPhotoBtn.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void plus() {
        dialog = creatDialog(this, R.style.MyDialogStyleBottom);
        dialog.show();
    }

    @Override
    public void remove(int position) {
        photoAdapter.removeItem(position);
        list.remove(position);
    }

    @Override
    public void onLongType(int type) {
        this.type = type;
        photoAdapter.addData(list, type);
        cancelPhotoBtn.setVisibility(View.VISIBLE);//切换布局, 取消按钮出现
    }

    @Override
    public void onClickTouch(String uriStr) {
        WindowManager manager = this.getWindowManager();
        int wid = manager.getDefaultDisplay().getWidth();
        int hei = manager.getDefaultDisplay().getHeight();
        final ShowBigPhotoDialog dialog = new ShowBigPhotoDialog(this, uriStr, wid, hei);
        dialog.show();
        dialog.setOnClickBigPhotoListener(new ShowBigPhotoDialog.OnClickBigPhotoListener() {
            @Override
            public void onClickBigPhotol(View view) {
                dialog.dismiss();
            }
        });
    }

    //创建底部菜单dialog
    private Dialog creatDialog(Context context, int stytle) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.buttom_popupwindow_head, null);
        pop_window_head_photeBT = (Button) dialogView.findViewById(R.id.pop_window_head_photeBT);
        pop_window_head_audioBT = (Button) dialogView.findViewById(R.id.pop_window_head_audioBT);
        pop_window_head_cancleBT = (Button) dialogView.findViewById(R.id.pop_window_head_cancleBT);
        popWindowListener();

        final Dialog customDialog = new Dialog(context, stytle);

        WindowManager.LayoutParams localLayoutParams = customDialog.getWindow().getAttributes();
        localLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        localLayoutParams.x = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.y = WindowManager.LayoutParams.MATCH_PARENT;
        int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        dialogView.setMinimumWidth(screenWidth);

        customDialog.onWindowAttributesChanged(localLayoutParams);
        customDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        customDialog.setCancelable(true);
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.setContentView(dialogView, localLayoutParams);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                customDialog.show();
            }
        }
        return customDialog;
    }

    private void popWindowListener() {
        //拍照
        pop_window_head_photeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    imgPath = Environment.getExternalStorageDirectory().getPath() + "/jr_ordor";
                    File dir = new File(imgPath);//图片路径
                    if (!dir.exists()) {//判断文件夹是否存在
                        dir.mkdir();
                    }
                    //取时间为文件名
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddssSSS");
                    String fileName = "JR" + (dateFormat.format(new Date())) + ".jpg";
                    imgPath = imgPath + "/" + fileName;//文件路径
                    imgUri = Uri.fromFile(new File(imgPath));
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);//照完照片的输出路径更改
                    startActivityForResult(takePhotoIntent, 31);
                    dialog.dismiss();
                } else {
                    Toast.makeText(CommitPictureActivity.this, "找不到SD卡", Toast.LENGTH_LONG).show();
                }
            }
        });
        //相册
        pop_window_head_audioBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 21);
                dialog.dismiss();
            }
        });
        //取消
        pop_window_head_cancleBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri path = null;
        ContentResolver contentResolver = CommitPictureActivity.this.getContentResolver();
        Bitmap bitmap = null;
        InputStream inputStream = null;
        if (resultCode == 0) {
            return;
        }
        switch (requestCode) {
            case 31://拍照
                try {
                    inputStream = contentResolver.openInputStream(imgUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 4;
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    path = AppUtils.creatFile(bitmap);
                    bitmap.recycle();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                addUriToAdapter(path);
                break;
            case 21://相册
                Uri uri = data.getData();
                try {
                    inputStream = contentResolver.openInputStream(uri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 4;
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    path = AppUtils.creatFile(bitmap);
                    bitmap.recycle();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                addUriToAdapter(path);
                break;
        }
    }

    //将返回的照片uri添加到集合中，和适配器中
    public void addUriToAdapter(Uri largUri) {
        if (largUri != null) {
            ImageUriEntity entity = new ImageUriEntity();
            entity.setLargImgPath(largUri.toString());//大图地址
            photoAdapter.addItem(entity);
            list.add(entity);
        }
    }

    /**
     * 上传照片后的操作
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (msg.what == 200) {
                showToast("请选择照片");
            }
        }
    };

    /**
     * 上传照片
     */
    Runnable commitImgTask = new Runnable() {
        @Override
        public void run() {
            String url = AppApi.UPLOADIMAGE;
            Message message = Message.obtain();

            String account = PreferenceUtil.readString(CommitPictureActivity.this,
                    SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
            if (list != null && list.size() > 0) {
                JSONObject object = new JSONObject();
                try {
                    object.put(AppConstants.ORDERID, order_id);
                    object.put(AppConstants.WORK_NO, account);
                    JSONArray array = new JSONArray();
                    JSONArray jsonArray = null;
                    for (int i = 0; i < list.size(); i++) {
                        Bitmap bitmap = getBitmapFromUri(Uri.parse(list.get(i).getLargImgPath()));
                        jsonArray = ImageUtils.bitmapToBase64(bitmap);
                        array.put(jsonArray);
                    }
                    object.put("imgData", array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(ImageUtils.JSON, object.toString());
                OkHttpClient client = null;
                try {
                    client = HttpsController.setCertificates(CommitPictureActivity.this, CommitPictureActivity.this.getAssets().open("zhenjren.cer"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String token = PreferenceUtil.readString(CommitPictureActivity.this, AppConstants.TOKEN, AppConstants.TOKEN);
                Request request = new Request.Builder().addHeader("token", token).post(body).url(url).build();
                Response response;

                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        if (response.code() != 401){
                            //成功
                            EventBus.getDefault().post("5");
                            finish();
                        }
                    } else {
                        //失败
                        setResult(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                message.what = 200;
            }
            handler.sendMessage(message);

        }
    };


    public void showToast(String str) {
        if (toast == null) {
            toast = Toast.makeText(CommitPictureActivity.this, str, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(str);
            toast.show();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

}
