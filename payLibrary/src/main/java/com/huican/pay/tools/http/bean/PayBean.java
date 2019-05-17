package com.huican.pay.tools.http.bean;

/**
 * anther: created by zuochunsheng on 2019/5/9 11 : 37
 * description :
 */
public class PayBean extends ParamentBean {
    /**
     *
     * walletType : 钱包类型
     * payMethod : 支付方式（0 ：支付宝hk 默认, 1 :支付宝cn,  2 微信 ）
     * subject : 商品名称
     * body ： 商品详情
     * totalAmount ： 商品金额（单位 元） -微信需要处理为分
     * mid : 支付宝（商户UID/PID） ；和微信 （商户号 mch_id）
     * seller_id : 支付宝 （签约卖家支付宝账号）
     */



    private int walletType = 1;
    private int payMethod ;
    private String subject;
    private String body;
    private String totalAmount;
    private String mid;
    private String seller_id;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(int payMethod) {
        this.payMethod = payMethod;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

}
