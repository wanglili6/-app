package com.itdream.wll.prient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itdream.wll.prient.base.Constants;
import com.itdream.wll.prient.base.MyApplacation;
import com.itdream.wll.prient.bean.RegisBean;
import com.itdream.wll.prient.bean.UserInfo;
import com.itdream.wll.prient.servser.LoginSevser;
import com.itdream.wll.prient.servser.RegisServer;
import com.itdream.wll.prient.utils.Base64Util;
import com.itdream.wll.prient.utils.HttpCacheUtils;
import com.itdream.wll.prient.utils.PreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_login_name)
    EditText etLoginName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_wjmm)
    TextView tvWjmm;
    @BindView(R.id.tv_zhuc)
    TextView tvZhuc;
    @BindView(R.id.v_left)
    TextView vLeft;
    @BindView(R.id.v_right)
    TextView vRight;
    private String TAG = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_login, R.id.tv_wjmm, R.id.tv_zhuc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //去登陆
                LoginSevser service = HttpCacheUtils.getRetrofit(this).create(LoginSevser.class);
                service.login("13661148369", "111111")               //获取Observable对象
                        .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                        .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                        .doOnNext(new Action1<UserInfo>() {
                            @Override
                            public void call(UserInfo userInfo) {
//                        saveUserInfo(userInfo);//保存用户信息到本地
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                        .subscribe(new Subscriber<UserInfo>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                //请求失败
                            }

                            @Override
                            public void onNext(UserInfo userInfo) {
                                Log.i(TAG, "onNext: " + userInfo);
                                //请求成功
                                if (userInfo != null) {
                                    if (userInfo.getResult().equals("200")) {
                                        PreferencesUtils.putString(LoginActivity.this,
                                                Constants.UserName, userInfo.getData().getLoginname());
                                        PreferencesUtils.putString(LoginActivity.this,
                                                Constants.UserPsd, Base64Util.toBase64(userInfo.getData().getPassword() + ""));
                                        PreferencesUtils.putInt(LoginActivity.this,
                                                Constants.UserId, userInfo.getData().getId());
                                        PreferencesUtils.putString(LoginActivity.this,
                                                Constants.UserPhone, userInfo.getData().getPhone());
                                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        });
                break;
            case R.id.tv_wjmm:
                //忘记密码
                String toBase64Pwd = Base64Util.toBase64("111111");
                final RegisBean regisBean = new RegisBean();
                regisBean.setPassword(toBase64Pwd);
                regisBean.setPhone("18310459359");
                String json = MyApplacation.getGson().toJson(regisBean);
                RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),
                        "{\"regisMsg\":" + json + "}");
                RegisServer regisServer = HttpCacheUtils.getRetrofit(LoginActivity.this).create(RegisServer.class);
//                String encode = Uri.decode(json);
                regisServer.modelPost(requestBody).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onResponse: " + response.toString());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                break;
            case R.id.tv_zhuc:
                //注册
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ResgisActivity.class);
                startActivity(intent);
                break;
        }
    }
}
