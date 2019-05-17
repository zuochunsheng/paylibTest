package com.huican.pay.payUtil;

/**
 * anther: created by zuochunsheng on 2019/5/7 11 : 27
 * description :
 */
public class PayResultStatus {

    public static final int PAY_RESULT_SUCCESS = 0;// 成功 0
    public static final int PAY_RESULT_ERROR = 1;//支付错误 （参数错误）；支付失败
    public static final int PAY_RESULT_CANCEL = 2;//用户取消 操作
    public static final int PAY_RESULT_UNINSTALLED = 3;//用户手机 未安装  支付宝 或微信 （应用未安装 ，版本过低）

    public static final int PAY_RESULT_Dealing = 4;// 支付宝正在处理中


}
