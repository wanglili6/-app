package com.itdream.wll.prient.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by wll on 2018/11/12.
 */

public class MyApplacation extends Application {
    private Context mContext;

    public Context getmContext() {
        return getApplicationContext();
    }

    private static Gson gson = null;

    //静态工厂方法
    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        getLogging();

    }


    public static HttpLoggingInterceptor getLogging() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.i("RetrofitLog", "retrofitBack = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
//        if (BuildConfig.DEBUG) {
//        } else {
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
//        }
        return loggingInterceptor;
    }


}
