package com.huican.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * anther: created by zuochunsheng on 2019/5/8 16 : 38
 * description :
 */
public class WXPayCallbackActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PayHelper.getInstance() != null) {
            PayHelper.getInstance().getWXApi().handleIntent(getIntent(), this);
        } else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (PayHelper.getInstance() != null) {
            PayHelper.getInstance().getWXApi().handleIntent(intent, this);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (PayHelper.getInstance() != null) {
                if (baseResp.errStr != null) {
                    Log.e("wxpay", "errstr=" + baseResp.errStr);
                }
                PayHelper.getInstance().onResp(baseResp.errCode);
                finish();
            }
        }
    }
}
