package com.itdream.wll.prient.utils;

import android.content.Context;

import com.itdream.wll.prient.base.Constants;
import com.itdream.wll.prient.base.MyApplacation;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wll on 2018/11/12.
 */

public class HttpCacheUtils {


    /**
     * 配置网络请求的连接器
     *
     * @return
     */
    public static Retrofit getRetrofit(final Context context) {
        HttpLoggingInterceptor httpLoggingInterceptor = MyApplacation.getLogging();
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(getCache(context))
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(getCacaheInterceptor(context))
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(150000, TimeUnit.SECONDS)//连接时间
                .readTimeout(150000, TimeUnit.SECONDS)//读取时间
                .writeTimeout(150000, TimeUnit.SECONDS)//写时间
                .retryOnConnectionFailure(true)//错误重连
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//新的配置
                .baseUrl(Constants.Base_Url)
                .build();
        return retrofit;
    }

    private static Cache getCache(final Context context) {
        //生成缓存，50M
        File cacheFile = new File(context.getExternalCacheDir(), "wllcacheFiles");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);

        return cache;
    }

    private static Interceptor getCacaheInterceptor(final Context context) {

        //缓存拦截器
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                //网络不可用
                if (!NetworkUtils.isNetworkConnected(context)) {
                    //在请求头中加入：强制使用缓存，不访问网络
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                //网络可用
                if (NetworkUtils.isNetworkConnected(context)) {
                    int maxAge = 0;
                    // 有网络时 在响应头中加入：设置缓存超时时间0个小时
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();
                } else {
                    // 无网络时，在响应头中加入：设置超时为4周
                    int maxStale = 60 * 60 * 24 * 28;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
                return response;
            }
        };
        return cacheInterceptor;
    }


}
