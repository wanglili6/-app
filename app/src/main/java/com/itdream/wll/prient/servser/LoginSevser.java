package com.itdream.wll.prient.servser;


import com.itdream.wll.prient.bean.UserInfo;

import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wll on 2018/11/9.
 */

public interface LoginSevser {

    Observable<UserInfo> login(
//            @Query("name") String username,
            @Query("phone") String username,
            @Query("password") String password
            );
}
