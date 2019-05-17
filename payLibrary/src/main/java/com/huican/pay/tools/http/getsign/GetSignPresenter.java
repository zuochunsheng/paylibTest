package com.huican.pay.tools.http.getsign;

import android.util.Log;
import com.huican.pay.tools.http.HttpManager;
import com.huican.pay.tools.http.bean.PayBean;

import org.json.JSONException;
import java.io.IOException;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 *
 * @author zsc
 * @date 2019/1/13
 */
public class GetSignPresenter {

    private IGetSignView getSignView;
    public GetSignPresenter(IGetSignView getSignView ){
        this.getSignView = getSignView;
    }



    public void getSign(PayBean payBean) {

        HttpManager.getIntanceHttps().getSign(payBean, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {

                try {
                    //Log.e("sign", "签名response ：" + responseBody.string());
                    getSignView.getSignView(responseBody.string()+"");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.e("sign", "提交错误");
            }

            @Override
            public void onComplete() {

            }
        });
    }


}
