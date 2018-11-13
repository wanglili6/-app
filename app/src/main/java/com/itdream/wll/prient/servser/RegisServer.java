package com.itdream.wll.prient.servser;

import com.itdream.wll.prient.bean.RegisInfo;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wll on 2018/11/12.
 */

public interface RegisServer {

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("/regis")
    Call<ResponseBody> modelPost(@Body RequestBody user);

    @POST("/add")
    Observable<RegisInfo> regis(
            @Query("regisMsg") String regisMsg
    );
}
