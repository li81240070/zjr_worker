package com.hengxun.builder.view.activity.order;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.model.BacklogOrder;
import com.hengxun.builder.model.HelpOrder;
import com.hengxun.builder.utils.AppUtils;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.DateUtil;
import com.hengxun.builder.utils.widget.StringUtils;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.HomeActivity;
import com.hengxun.builder.view.activity.map.MapLocationActivity;
import com.hengxun.builder.view.adapter.OrderDetailsAdapter;
import com.hengxun.builder.view.widget.OrderPhotoDialog;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZY on 2016/3/25.
 * 订单详情页面
 */
public class OfferOrderDetailsActivity extends BaseActivity implements View.OnClickListener {
    private static final int UN_OVEW_REQUEST_CALL_PHONE = 100;
    private static final int OVEW_REQUEST_CALL_PHONE = 101;
    public static OfferOrderDetailsActivity finishActivity; // 注销销毁的activity
//    private boolean ISCOMMIT = false;

    private RecyclerView orderAddPhoto_Rv;      // 添加照片
    private RelativeLayout callMerchants_Rl;    // 联系用户
    private TextView orderLocation_Tv;          // 开始定位
    private Button orderChange_btn;             // 更改价格
    //    private OrderList.DataMapEntity.OrdersEntity entity; // 订单实体类
    private BacklogOrder.DataMapEntity.OfferOrdersEntity orderPush;
    private HelpOrder.DataMapEntity.DetailEntity entity;
    private LatLng orderStart;                  // 工人位置
    private LatLng orderEnd;                    // 订单位置
//    private UserInfo userInfo;                  // 匠人信息

    private WaittingDiaolog progressDialog;
    private AlertDialog dialog;                 // 更改价格对话框
    private EditText changeOrder_Et;            // 新价格输入框
    private String price = "0";                 // 新价格
    private TextView orderMessages_Tv;          // 订单信息
    private TextView orderPrice_Tv;             // 订单价格
    private TextView orderType_Tv;              // 服务类型
    private TextView orderAddress_Tv;           // 订单地址
    private ImageView orderPhotos_Iv;           // 添加图片图标
    private ImageView orderMessages_Iv;         // 订单信息图标
    private Button orderDetails_Btn;            // 完成按钮
    //    private OrderPush orderPush;                // 推送订单
    private TextView toolbar_right_Tv;          // 上传照片
    private TextView appointment_Tv;            // 预约时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        finishActivity = this;
        EventBus.getDefault().register(this);
        showToolBar(getResources().getString(R.string.order_details), true, this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        orderAddPhoto_Rv = (RecyclerView) findViewById(R.id.orderAddPhoto_Rv);
        callMerchants_Rl = (RelativeLayout) findViewById(R.id.callMerchants_Rl);
        orderLocation_Tv = (TextView) findViewById(R.id.orderLocation_Tv);
        orderChange_btn = (Button) findViewById(R.id.orderChange_btn);
        orderMessages_Tv = (TextView) findViewById(R.id.orderMessages_Tv);
        orderPrice_Tv = (TextView) findViewById(R.id.orderPrice_Tv);
        orderType_Tv = (TextView) findViewById(R.id.orderType_Tv);
        orderAddress_Tv = (TextView) findViewById(R.id.orderAddress_Tv);
        orderPhotos_Iv = (ImageView) findViewById(R.id.orderPhotos_Iv);
        orderMessages_Iv = (ImageView) findViewById(R.id.orderMessages_Iv);
        orderDetails_Btn = (Button) findViewById(R.id.orderDetails_Btn);
        appointment_Tv = (TextView) findViewById(R.id.appointment_Tv);
    }

    @Override
    protected void initData() {
        super.initData();
        callMerchants_Rl.setOnClickListener(this);
        orderLocation_Tv.setOnClickListener(this);
        orderChange_btn.setOnClickListener(this);
        // 获取抢险抢修实体
        entity = (HelpOrder.DataMapEntity.DetailEntity) getIntent().getSerializableExtra("order");
        if (entity == null) {
            orderPush = (BacklogOrder.DataMapEntity.OfferOrdersEntity) getIntent().getSerializableExtra("orderPush");
        }

        if (entity != null) {
            if (!entity.getOrder_status().equals("4")) {
                toolbar_right_Tv.setVisibility(View.GONE);
            }
            appointment_Tv.setText(DateUtil.getStringTime(String.valueOf(entity.getAppoint_time())));

            if (!entity.getAdjustable().equals("1") || entity.getOrder_status().equals("4")) {
                orderChange_btn.setTextColor(getResources().getColor(R.color.forget_tv_lines));
                orderChange_btn.setBackgroundResource(R.drawable.shape_order_unchange_btn);
            }
        }
        if (orderPush != null) {
            if (!orderPush.getOrder_status().equals("4")) {
                toolbar_right_Tv.setVisibility(View.GONE);
            }
            appointment_Tv.setText(DateUtil.getStringTime(String.valueOf(orderPush.getAppoint_time())));
            if (!orderPush.getAdjustable().equals("1") || orderPush.getOrder_status().equals("4")) {
                orderChange_btn.setTextColor(getResources().getColor(R.color.forget_tv_lines));
                orderChange_btn.setBackgroundResource(R.drawable.shape_order_unchange_btn);
            }
        }
        orderStart = getIntent().getParcelableExtra("start");
        orderEnd = getIntent().getParcelableExtra("end");

        if (orderEnd == null && orderPush != null) {
            orderEnd = new LatLng(Double.parseDouble(orderPush.getX()),
                    Double.parseDouble(orderPush.getY()));
        }
//        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        // 根据订单状态不同  UI和逻辑不同
        if (entity != null && entity.getOrder_status() != null) {
            switch (entity.getOrder_status()) {
                case "3": // 议价 设置完成按钮可见并可点击

                    break;
                case "4": // 已开工
                    orderDetails_Btn.setVisibility(View.VISIBLE);
                    orderDetails_Btn.setOnClickListener(this);
                    break;
            }
        } else {
            switch (orderPush.getOrder_status()) {
                case "3": // 议价 设置完成按钮可见并可点击

                    break;
                case "4": // 已开工
                    orderDetails_Btn.setVisibility(View.VISIBLE);
                    orderDetails_Btn.setOnClickListener(this);
                    break;
            }
        }
        OrderDetailsAdapter adapter = new OrderDetailsAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        orderAddPhoto_Rv.setLayoutManager(gridLayoutManager);

        final List<String> img = new LinkedList<>();
//        // 如果图片不为空则给adapter设置图片
//        if (entity != null && entity.getImages() != null) {
//            for (int i = 0; i < entity.getImages().; i++) {
//                String imgurl = AppApi.ORDER_IMG + entity.getImages().get(i) + "_200.jpg";
//                img.add(imgurl);
//            }
//
//            orderMessages_Tv.setText(entity.getComment()); // 订单描述
//            orderPrice_Tv.setText(entity.getPrice());      // 订单价格
//            orderType_Tv.setText(entity.getService());
//            orderAddress_Tv.setText(entity.getAddress());
//        } else {
        if (entity != null && entity.getAddress() != null) {
            orderAddress_Tv.setText(entity.getAddress()); // 抢险抢修地址
            orderType_Tv.setText("抢险抢修"); // 抢险抢修描述
        } else if (orderPush != null) {
            if (orderPush.getImages() != null) {
                for (int i = 0; i < orderPush.getImages().size(); i++) {
                    String imgurl = AppApi.ORDER_IMG + orderPush.getImages().get(i) + "_200.jpg";
                    img.add(imgurl);
                }
                orderAddPhoto_Rv.setAdapter(adapter); // 添加图片数据
                adapter.addData(img);
            }

            orderMessages_Tv.setText(orderPush.getComment()); // 订单描述
            orderPrice_Tv.setText(StringUtils.changeToMoney(Double.valueOf(orderPush.getPrice())));      // 订单价格
            orderType_Tv.setText(orderPush.getService());
            orderAddress_Tv.setText(orderPush.getAddress());


            // 弹开照片
            adapter.setOnItemClickListener(new OrderDetailsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String imgurl;
                    //                if (entity != null && entity.getImages() != null) {
                    //                    imgurl = AppApi.ORDER_IMG + entity.getImages().get(position) + "_640.jpg";
                    //                } else {
                    imgurl = AppApi.ORDER_IMG + orderPush.getImages().get(position) + "_640.jpg";
                    //                }
                    WindowManager manager = OfferOrderDetailsActivity.this.getWindowManager();
                    int wid = manager.getDefaultDisplay().getWidth();
                    int hei = manager.getDefaultDisplay().getHeight();
                    final OrderPhotoDialog dialog = new OrderPhotoDialog(OfferOrderDetailsActivity.this, imgurl, wid, hei);
                    dialog.show();
                    dialog.setOnClickBigPhotoListener(new OrderPhotoDialog.OnClickBigPhotoListener() {
                        @Override
                        public void onClickBigPhotol(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            orderAddPhoto_Rv.setAdapter(adapter); // 添加图片数据
            adapter.addData(img);

            //        if (entity != null && entity.getMobile() != null) {
            //            if (!(entity.getImages() != null) || !(entity.getImages().size() > 0)) { // 如果图片为空则不显示recyclerview
            //                orderAddPhoto_Rv.setVisibility(View.GONE);
            //                orderPhotos_Iv.setVisibility(View.GONE);
            //            }
            //        } else {
            if ((orderPush.getImages() == null) ||
                    (orderPush.getImages().size() == 0)) { // 如果图片为空则不显示recyclerview
                orderAddPhoto_Rv.setVisibility(View.GONE);
                orderPhotos_Iv.setVisibility(View.VISIBLE);
            } else {
                orderAddPhoto_Rv.setVisibility(View.GONE);
                orderPhotos_Iv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callMerchants_Rl:
                // 如果不是完工状态
                if (entity != null) {
                    if (!entity.getOrder_status().equals("5") && entity.getMobile() != null && !entity.getMobile().equals("")) {

                        Toast.makeText(this, "将给" + entity.getCustomerName() + "拨打电话，手机号" + entity.getMobile(), Toast.LENGTH_SHORT).show();

                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                                    UN_OVEW_REQUEST_CALL_PHONE);
                        } else {
                            callPhone(entity.getMobile());
                        }
                    }
                } else {
                    if (!orderPush.getOrder_status().equals("5") &&
                            orderPush.getMobile() != null && !orderPush.getMobile().equals("")) {

                        Toast.makeText(this, "将给" + orderPush.getCustomerName() + "拨打电话，手机号" +
                                orderPush.getMobile(), Toast.LENGTH_SHORT).show();

                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                                    OVEW_REQUEST_CALL_PHONE);
                        } else {
                            callPhone(orderPush.getMobile());
                        }
                    }
                }
                break;

            case R.id.orderLocation_Tv:
//                if (entity != null && entity.getModel() == 2) {
//                    Toast.makeText(this, "该订单不可定位", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent toMap = new Intent(OfferOrderDetailsActivity.this, MapLocationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("start", orderStart); // 订单起始位置
                bundle.putParcelable("end", orderEnd);   // 订单结束位置
                toMap.putExtras(bundle);
                startActivity(toMap);
                break;

            case R.id.orderChange_btn:
                // 判断订单价格是否可以修改
                if (entity != null && entity.getMobile() != null) {
                    if (entity.getAdjustable().equals("1") && entity.getModel() != 2) {
                        if (entity.getOrder_status().equals("3") || entity.getOrder_status().equals("2")) {
                            showDialog(); // 更改价格
                        }
                    } else {
//                        orderChange_btn.setTextColor(getResources().getColor(R.color.primary_color));
//                        orderChange_btn.setBackgroundResource(R.drawable.shape_order_unchange_btn);
                        Toast.makeText(this, "该订单不可改价", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (orderPush.getAdjustable().equals("1")) {
                        if (orderPush.getOrder_status().equals("3") ||
                                orderPush.getOrder_status().equals("2")) {
                            showDialog(); // 更改价格
                        }
                    } else {
//                        orderChange_btn.setTextColor(getResources().getColor(R.color.primary_color));
//                        orderChange_btn.setBackgroundResource(R.drawable.shape_order_unchange_btn);
                        Toast.makeText(this, "该订单不可改价", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.cancel_Tv: // 取消更改
                dialog.dismiss();
                break;

            case R.id.sure_Tv: // 确定更改
                price = changeOrder_Et.getText().toString().trim();
                if (TextUtils.isEmpty(price)) {
                    Toast.makeText(this, "请输入价格", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isPrice = AppUtils.formatPrice(price);
                if (!isPrice) {
                    Toast.makeText(this, "价钱输入格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 如果要修改的价格  小于原来的价格 不可更改
                if (entity != null) {
                    if (Float.parseFloat(price) <= Float.parseFloat(entity.getPrice())) {
                        Toast.makeText(this, "修改价格不能低于原始价格", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (Float.parseFloat(price) <= Float.parseFloat(String.valueOf(orderPush.getPrice()))) {
                        Toast.makeText(this, "修改价格不能低于原始价格", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (Double.parseDouble(price) >= 1000000.000000) {
                    Toast.makeText(this, "价钱输入格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new WaittingDiaolog(this);
                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setMessage("正在加载中...");
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show();
                }
                new Thread(changePriceTask).start();
                break;

            case R.id.toolbar_right_Tv: // 上传照片
                if (entity != null && entity.getMobile() != null) {
                    if (!"4".equals(entity.getOrder_status())) {
                        Toast.makeText(OfferOrderDetailsActivity.this, "无法上传照片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (orderPush.getOrder_status().equals("4")) {
                        Toast.makeText(OfferOrderDetailsActivity.this, "无法上传照片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent commit = new Intent(OfferOrderDetailsActivity.this, CommitPictureActivity.class);
                Bundle data = new Bundle();
                if (entity != null) {
                    data.putSerializable("order_id", entity.getOrder_id());
                } else {
                    data.putSerializable("order_id", String.valueOf(orderPush.getOrder_id()));
                }
                commit.putExtras(data);
                startActivity(commit);
                break;

            case R.id.orderDetails_Btn: // 匠人上传照片后, 点击完工
//                if (ISCOMMIT) {
                progressDialog = new WaittingDiaolog(this);
                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setMessage("正在加载中...");
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show();
                }
                new Thread(completePriceTask).start();
//                } else {
//                    Toast.makeText(this, "需要先上传图片", Toast.LENGTH_SHORT).show();
//                }

                break;
        }
    }

    /**
     * 上传照片
     */
    @Override
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        super.showToolBar(titleName, isShow, activity);
        toolbar_right_Tv = (TextView) activity.findViewById(R.id.toolbar_right_Tv);
        toolbar_right_Tv.setText(getResources().getString(R.string.commit));
        toolbar_right_Tv.setTextColor(getResources().getColor(R.color.order_commit_picture));
        toolbar_right_Tv.setOnClickListener(this);
    }

    /**
     * 订单可以更改的情况下 点击更改按钮  弹出对话框
     */
    private void showDialog() {
        // 使用builder创建对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_price, null);
        builder.setView(view);
        dialog = builder.create();
//        Display display = getWindowManager().getDefaultDisplay();
//        int width = display.getWidth();
//        int height = display.getHeight();
//        dialog.getWindow().setLayout((int) (width * 0.7), (int) (height * 0.4));

        // 设置对话框的view
        changeOrder_Et = (EditText) view.findViewById(R.id.changeOrder_Et); // 输入价格et
        TextView sure_Tv = (TextView) view.findViewById(R.id.sure_Tv); // 确定
        sure_Tv.setOnClickListener(this);
        TextView cancel_Tv = (TextView) view.findViewById(R.id.cancel_Tv); // 取消
        cancel_Tv.setOnClickListener(this);
        dialog.show();
    }

    /**
     * 更改结束  对话框消失
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case 1:  // 修改价格
                    orderPrice_Tv.setText(StringUtils.changeToMoney(Double.valueOf(price)));
                    EventBus.getDefault().post("123");
                    dialog.dismiss();
                    break;

                case 2: // 完工状态
                    Intent receiver = new Intent(); // 发送广播启动发送地理位置的服务
                    receiver.setAction("com.hengxun.builder.service");
                    sendBroadcast(receiver);

                    Intent intent = new Intent(OfferOrderDetailsActivity.this, HomeActivity.class);
                    EventBus.getDefault().post("finish");
                    startActivity(intent);
                    finish();
                    break;
            }

        }
    };

    /**
     * 改变订单状态--修改价格
     */
    Runnable changePriceTask = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> map = new HashMap<>();
            if (entity != null) {
                map.put(AppConstants.ORDER_ID, entity.getOrder_id());
            } else {
                map.put(AppConstants.ORDER_ID, String.valueOf(orderPush.getOrder_id()));
            }
            map.put(AppConstants.ORDER_STATUS, "3");
            map.put(AppConstants.PARAM, price);
            String url = UrlUtils.addParams(AppApi.ORDERSTATUS, map);
            Log.d("qwer", url);
            Response status;
            Message message = Message.obtain();
            try {
                status = OkHttpClientManager.getAsyn(url, OfferOrderDetailsActivity.this);
                if (status.code() != 401) {
                    message.what = 1; // 修改价格
//                    Log.d("qwer1", status.body().string());
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 改变订单状态--完工
     */
    Runnable completePriceTask = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> map = new HashMap<>();
            if (entity != null) {
                map.put(AppConstants.ORDER_ID, entity.getOrder_id());
            } else {
                map.put(AppConstants.ORDER_ID, String.valueOf(orderPush.getOrder_id()));
            }
            map.put(AppConstants.ORDER_STATUS, "5");
            map.put(AppConstants.PARAM, "");
            String url = UrlUtils.addParams(AppApi.ORDERSTATUS, map);
            Log.d("qwer", url);
            Response status;
            Message message = Message.obtain();
            try {
                status = OkHttpClientManager.getAsyn(url, OfferOrderDetailsActivity.this);
                if (status.code() != 401) {
                    message.what = 2; // 完工
//                    Log.d("qwer1", status.body().string());
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 上传图片以后
     * 可以点击完成 否则提示需要上传图片
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(String str) {
        if (str.equals("5")) {
//            ISCOMMIT = true;
            Toast.makeText(this, "图片上传成功！", Toast.LENGTH_SHORT).show();
        } else if (str.equals("6")) {
            Toast.makeText(this, "请先提交照片", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        callPhoneCallback(requestCode, grantResults);
    }

    private void callPhoneCallback(int requestCode, int[] grantResults) {
        if (requestCode == UN_OVEW_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone(entity.getMobile());
            } else {
                Toast.makeText(this, "已禁止本应用拨打电话", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == OVEW_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone(orderPush.getMobile());
            } else {
                Toast.makeText(this, "已禁止本应用拨打电话", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callPhone(String phoneNum) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + phoneNum));
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ISCOMMIT = false;
        EventBus.getDefault().post("123");
        EventBus.getDefault().unregister(this);
    }
}
