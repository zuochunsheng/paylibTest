package com.huican.pay.tools.http;

import com.huican.pay.tools.http.bean.PayBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * Created by zcs on 2017/6/21.
 */

public interface ApiServer {

    /**
     * 签名数据取  orderStr字段
     * @param
     * @return 异步返回
     */
    @POST("test/getSign")
    Observable<ResponseBody> getSign(@Body PayBean payBean);
}
