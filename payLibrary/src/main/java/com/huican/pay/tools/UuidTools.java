package com.huican.pay.tools;

import android.content.Context;
import android.provider.Settings;

/**
 * 作者：宇宙级灵魂冲浪手 bukes .
 * 时间：2017/11/14 on 10:02 .
 * 描述：
 *
 * @author
 */

public class UuidTools {
    public static String getUUid(Context context){
        String s = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        String serialNumber = android.os.Build.SERIAL;
        return s + serialNumber;
    }
}
