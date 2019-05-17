package com.huican.pay;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.huican.pay.PayHelper;
import com.huican.pay.payUtil.PayResultCallback;

public class PayActivity extends AppCompatActivity  implements View.OnClickListener{

    private TextView tv_subject;
    private TextView tv_body;
    private TextView tv_total_fee;
    private RadioGroup radioGroup;
    private RadioButton radio_alipycn;
    private RadioButton radio_alipyhk;
    private RadioButton radio_weichart;
    private Button btn_payHK;

    private int payWay = 0;// 支付方式（0 ：支付宝hk 默认, 1 :支付宝cn,  2 微信 ）

    /**
     * 获取权限使用的 RequestCode
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        requestPermission();
    }



    private void initView() {
        tv_subject = (TextView) findViewById(R.id.tv_subject);
        tv_body = (TextView) findViewById(R.id.tv_body);
        tv_total_fee = (TextView) findViewById(R.id.tv_total_fee);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radio_alipycn = (RadioButton) findViewById(R.id.radio_alipycn);
        radio_alipyhk = (RadioButton) findViewById(R.id.radio_alipyhk);
        radio_weichart = (RadioButton) findViewById(R.id.radio_weichart);

        btn_payHK = (Button) findViewById(R.id.btn_payHK);

        btn_payHK.setOnClickListener(this);
        // 支付方式（0 ：支付宝hk 默认, 1 :支付宝cn,  2 微信 ）
        if (payWay == 0) {
            radio_alipyhk.setChecked(true);
        } else if (payWay == 1) {
            radio_alipycn.setChecked(true);
        }else if (payWay == 2) {
            radio_weichart.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (radio_alipycn.getId() == checkedId) {
                    payWay = 1;
                } else if (radio_alipyhk.getId() == checkedId) {
                    payWay = 0;
                } else if (radio_weichart.getId() == checkedId) {
                    payWay = 2;
                }

            }
        });


    }

    /**
     * 检查支付宝 SDK 所需的权限，并在必要的时候动态获取。
     * 在 targetSDK = 23 以上，READ_PHONE_STATE 和 WRITE_EXTERNAL_STORAGE 权限需要应用在运行时获取。
     * 如果接入支付宝 SDK 的应用 targetSdk 在 23 以下，可以省略这个步骤。
     */
    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSIONS_REQUEST_CODE);

        } else {
            //showToast(this, getString(R.string.permission_already_granted));
        }
    }



    /**
     * 权限获取回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {

                // 用户取消了权限弹窗
                if (grantResults.length == 0) {
                    showToast(this, getString(R.string.permission_rejected));
                    return;
                }

                // 用户拒绝了某些权限
                for (int x : grantResults) {
                    if (x == PackageManager.PERMISSION_DENIED) {
                        showToast(this, getString(R.string.permission_rejected));
                        return;
                    }
                }

                // 所需的权限均正常获取
                //showToast(this, getString(R.string.permission_granted));
            }
        }
    }


    private static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_payHK:
                doPay();
                break;
        }
    }






    /**
     * @time : 2019/5/9 14:46
     * @author : zcs
     * @description : 支付
     */
    private void doPay() {

//        {
//                "mid":"Test Merchant No1",
//                "walletType":1,
//                "totalAmount":9999.9999,
//                "payMethod":1,
//                "subject":"alipay testing",
//                "body":"test",
//                "seller_id":"hksandbox_3832@alitest.com"
//        }

        String subject = tv_subject.getText().toString();
        if (TextUtils.isEmpty(subject)) {
            return;
        }
        String total_fee = tv_total_fee.getText().toString();
        if (TextUtils.isEmpty(total_fee)) {
            return;
        }
        String body = "test";
        String sellerId = "hksandbox_3832@alitest.com";
        String mid = "Test Merchant No1";

        PayHelper payHelper = new PayHelper.Builder()
                .setPayMethod(payWay)
                .setSubject(subject)
                .setBody(body)
                .setTotalAmount(total_fee)
                .setMid(mid)
                .setSeller_id(sellerId)
                .build();

        //payHelper.checkPermissions();
        payHelper.doPay(this,new PayResultCallback() {
            @Override
            public void onSuccess(int payMethod) {
                Log.e("pay","onSuccess payMethod:" + payMethod);

            }

            @Override
            public void onError(int payMethod,int errorCode, String errorMsg) {
                Log.e("pay","errorCode="+ errorCode+",errorMsg="+errorMsg);
            }

            @Override
            public void onCancel(int payMethod) {
                Log.e("pay","onCancel");
            }
        });

    }
}
