package com.huican.pay;

import android.Manifest;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.huican.pay.payUtil.PayResult;
import com.huican.pay.payUtil.PayResultCallback;
import com.huican.pay.payUtil.PayResultStatus;
import com.huican.pay.tools.Config;
import com.huican.pay.tools.LogUtil;
import com.huican.pay.tools.UuidTools;
import com.huican.pay.tools.http.bean.PayBean;
import com.huican.pay.tools.http.getsign.GetSignPresenter;
import com.huican.pay.tools.http.getsign.IGetSignView;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


/**
 * anther: created by zuochunsheng on 2019/5/8 15 : 14
 * description :
 */
public class PayHelper {


    private static PayResultCallback payResultCallback;
    private Activity context;
    private static PayHelper instance;

    private IWXAPI mWxApi;

    private int payMethod;//必选
    private String subject;
    private String body;
    private String totalAmount;
    private String mid;
    private String seller_id;


    private static final int SDK_PAY_FLAG = 1;
    private PayTask mPayTask;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    //String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {               //支付成功
                        payResultCallback.onSuccess(payMethod);
                    } else if (TextUtils.equals(resultStatus, "8000")) {        //等待支付结果确认
                        //payResultCallback.onDealing();
                        payResultCallback.onError(payMethod, PayResultStatus.PAY_RESULT_Dealing, "正在处理中");
                    } else if (TextUtils.equals(resultStatus, "6001")) {        //支付取消
                        payResultCallback.onCancel(payMethod);
                    } else if (TextUtils.equals(resultStatus, "6002")) {        //网络连接出错
                        payResultCallback.onError(payMethod, PayResultStatus.PAY_RESULT_ERROR, "网络连接出错");
                    } else if (TextUtils.equals(resultStatus, "4000")) {        //支付错误
                        payResultCallback.onError(payMethod, PayResultStatus.PAY_RESULT_ERROR, "支付错误");
                    }
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });


    // 单例模式中获取唯一的PayHelper实例  synchronized
    public static PayHelper getInstance() {
        if (instance == null) {
            instance = new PayHelper();
        }
        return instance;
    }

    private PayHelper() {

    }

    protected IWXAPI getWXApi() {
        return mWxApi;
    }


    /**
     * @time : 2019/5/10 11:01
     * @author : zcs
     * @description :  6.0检查权限， 并启动支付
     */
    public void checkPermissions(final Activity activity, final PayResultCallback payResultCallback) {
        AndPermission.with(context)
                .runtime()
                .permission(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                        doPay(activity, payResultCallback);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                        checkPermissions(activity, payResultCallback);
                    }
                })
                .start();

    }


    /**
     * @time : 2019/5/10 10:57
     * @author : zcs
     * @description :  启动支付
     */
    public void doPay(final Activity activity, final PayResultCallback payResultCallback) {

        this.context = activity;
        if (TextUtils.isEmpty(subject)) {
            Toast.makeText(context, "商品名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(totalAmount)) {
            Toast.makeText(context, "商品金额不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mid)) {
            Toast.makeText(context, "商户Id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (payMethod == 0 || payMethod == 1) {
            if (TextUtils.isEmpty(seller_id)) {
                Toast.makeText(context, "签约卖家支付宝账号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        PayBean payBean = new PayBean();
        payBean.setPayMethod(payMethod);
        payBean.setSubject(subject);
        payBean.setBody(body);
        payBean.setTotalAmount(totalAmount);
        payBean.setMid(mid);
        payBean.setSeller_id(seller_id);

        payBean.setDeviceId(UuidTools.getUUid(context));
        LogUtil.e("payBean", payBean);


        GetSignPresenter getSignPresenter = new GetSignPresenter(new IGetSignView() {
            @Override
            public void getSignView(String result) {
                LogUtil.e("pay", result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if (TextUtils.equals(code, "000000")) {

                        String orderStr = jsonObject.getString("orderStr");

                        //String payMethod = jsonObject.getString("payMethod");
//                        if(payMethod == 0 || payMethod ==1){
//
//                        }else {
//
//                        }
                        doPay(payMethod, orderStr, payResultCallback);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        getSignPresenter.getSign(payBean);

    }


    //发起支付
    private void doPay(int payMethod, final String payParam, PayResultCallback callback) {
        payResultCallback = callback;
        if (payMethod == 2) {

            String WX_APPID = Config.WX_APP_ID;//定值
            mWxApi = WXAPIFactory.createWXAPI(context, WX_APPID, true);
            boolean b1 = mWxApi.registerApp(WX_APPID);
            if (mWxApi != null) {

                if (!mWxApi.isWXAppInstalled()) {
                    if (payResultCallback != null) {
                        payResultCallback.onError(payMethod, PayResultStatus.PAY_RESULT_UNINSTALLED, "未安装微信");
                    }
                } else if (mWxApi.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
                    if (payResultCallback != null) {
                        payResultCallback.onError(payMethod, PayResultStatus.PAY_RESULT_UNINSTALLED, "微信版本过低");
                    }
                } else {
                    //PayReq req = genPayReq(payParam);

                    PayReq req = new PayReq();
                    req.appId = "wxff989c97f26783b1";
                    req.partnerId = "1433741002";
                    req.prepayId = "wx20170307112556bd9969dbfd0294057248";
                    req.packageValue = "Sign=WXPay";
                    req.nonceStr = "evvVLW";
                    req.timeStamp = "1488857156";
                    req.sign = "7A5D7756CB59DDADB445F6FFC921EAF8";

                    if (req == null) {
                        return;
                    }
                    boolean b = mWxApi.sendReq(req);
                    Log.e("tag", "发起微信支付申请， 注册到微信" + b1 + "<> 发起支付" + b);

                }
            }
        } else {//  if (payMethod == 0 || payMethod == 1)
            mPayTask = new PayTask(context);
            //LogUtil.e("pay", "PayTask");
            final Runnable payRunnable = new Runnable() {
                @Override
                public void run() {

                    // 调用支付接口，获取支付结果
                    final Map<String, String> result = mPayTask.payV2(payParam, true);
                    LogUtil.e("pay", "result", result);
                    Message msg = Message.obtain();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    if (mHandler != null) {
                        mHandler.sendMessage(msg);
                    }

                }
            };
            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();

        }
    }


    @Nullable
    private PayReq genPayReq(String payParam) {
        JSONObject param;
        try {
            param = new JSONObject(payParam);
        } catch (JSONException e) {
            e.printStackTrace();
            if (payResultCallback != null) {
                payResultCallback.onError(payMethod, PayResultStatus.PAY_RESULT_ERROR, "支付参数错误");
            }
            return null;
        }
        if (TextUtils.isEmpty(param.optString("appid"))
                || TextUtils.isEmpty(param.optString("partnerid"))
                || TextUtils.isEmpty(param.optString("prepayid"))
                || TextUtils.isEmpty(param.optString("package"))
                || TextUtils.isEmpty(param.optString("noncestr"))
                || TextUtils.isEmpty(param.optString("timestamp"))
                || TextUtils.isEmpty(param.optString("sign"))) {
            if (payResultCallback != null) {
                payResultCallback.onError(payMethod, PayResultStatus.PAY_RESULT_ERROR, "支付参数错误");
            }
            return null;
        }
        PayReq req = new PayReq();
        req.appId = param.optString("appid");
        req.partnerId = param.optString("partnerid");
        req.prepayId = param.optString("prepayid");
        req.packageValue = param.optString("package");
        req.nonceStr = param.optString("noncestr");
        req.timeStamp = param.optString("timestamp");
        req.sign = param.optString("sign");
        return req;
    }


    //支付回调响应
    public void onResp(int errorCode) {
        if (payResultCallback == null) {
            return;
        }
        if (errorCode == 0) {   //成功
            payResultCallback.onSuccess(payMethod);
        } else if (errorCode == -1) {   //错误
            payResultCallback.onError(payMethod, PayResultStatus.PAY_RESULT_ERROR, "支付失败");
        } else if (errorCode == -2) {   //取消
            payResultCallback.onCancel(payMethod);
        }
        payResultCallback = null;
    }





    private PayHelper(Builder builder) {
        payMethod = builder.payMethod;
        subject = builder.subject;
        body = builder.body;
        totalAmount = builder.totalAmount;
        mid = builder.mid;
        seller_id = builder.seller_id;

    }

    public int getPayMethod() {
        return payMethod;
    }

    @Keep
    public static class Builder {
        /**
         * walletType : 钱包类型 固定值 1
         * payMethod : 支付方式（0 ：支付宝hk 默认, 1 :支付宝cn,  2 微信 ）
         * subject : 商品名称
         * body ： 商品详情
         * totalAmount ： 商品金额（单位 元） -微信需要处理为分
         * mid : 支付宝（商户UID/PID） ；和微信 （商户号 mch_id）
         * seller_id : 支付宝 （签约卖家支付宝账号）
         */

        private int walletType = 1;
        private int payMethod;
        private String subject;
        private String body;
        private String totalAmount;
        private String mid;
        private String seller_id;


        public Builder() {

        }

        //        public int getPayMethod() {
//            return payMethod;
//        }
        public Builder setPayMethod(int payMethod) {
            this.payMethod = payMethod;
            return this;
        }


        //        public String getSubject() {
//            return subject;
//        }
        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }


        //        public String getBody() {
//            return body;
//        }
        public Builder setBody(String body) {
            this.body = body;
            return this;
        }


        //        public String getTotalAmount() {
//            return totalAmount;
//        }
        public Builder setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }


        //        public String getMid() {
//            return mid;
//        }
        public Builder setMid(String mid) {
            this.mid = mid;
            return this;
        }


        //        public String getSeller_id() {
//            return seller_id;
//        }
        public Builder setSeller_id(String seller_id) {
            this.seller_id = seller_id;
            return this;
        }


        public PayHelper build() {
            instance = new PayHelper(this);
            return instance;
        }
    }


}
