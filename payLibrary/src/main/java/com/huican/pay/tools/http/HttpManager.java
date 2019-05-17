package com.huican.pay.tools.http;

import android.support.annotation.NonNull;

import com.huican.pay.tools.http.bean.PayBean;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络管理类
 * Created by zsc on 2017/6/21.
 */

public class HttpManager {

    public static String HTTP_URL = "https://e1.hcit6705.win/oss-transaction/";

    private static HttpManager intance;

    /* 请求超时时间 */
    private static final int TIME_OUT_PERIOD = 20000 * 10;

    private Retrofit retrofit;

    private ApiServer apiServer;

    private static TrustManager[] trustManagers;


    public static void setHTTP_URL(String url){
        HTTP_URL = url;
    }

    static {
        trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
    }

    private HttpManager(boolean isHttpsSSL){
        OkHttpClient.Builder builder = getBuilder(isHttpsSSL);


        /* 对 retrfit 进行建造 */
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(HTTP_URL)
                .addConverterFactory(GsonConverterFactory.create()) // 设置 gson 支持
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 设置 RxJava adapter
                .build();

        apiServer = retrofit.create(ApiServer.class);
    }

    @NonNull
    private OkHttpClient.Builder getBuilder(boolean isHttpsSSL) {
    /* 对 okhttpClient 进行初始化 */
        OkHttpClient.Builder builder = new OkHttpClient.Builder().retryOnConnectionFailure(false);
        builder.connectTimeout(TIME_OUT_PERIOD, TimeUnit.MILLISECONDS); // 设置连接超时时间 60 秒
        builder.readTimeout(TIME_OUT_PERIOD,TimeUnit.MILLISECONDS); // 设置读取超时时间 60 秒
        builder.writeTimeout(TIME_OUT_PERIOD,TimeUnit.MILLISECONDS); // 设置写入超时时间 60 秒


        // https 忽略证书验证
        if(isHttpsSSL){
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                sslContext.init(null,trustManagers,new SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(DO_NOT_VERIFY);
        }
        return builder;
    }

    /* 单例化 HttpManager 类，但是并不使用同步锁 */
    public static HttpManager getIntance(){
        if(intance == null){
            intance = new HttpManager(false);
        }
        return intance;
    }

    public static HttpManager getIntanceHttps(){
        if(intance == null){
            intance = new HttpManager(true);
        }
        return intance;
    }




    public void getSign(PayBean payBean, Observer<ResponseBody> observer){
        apiServer.getSign(payBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
