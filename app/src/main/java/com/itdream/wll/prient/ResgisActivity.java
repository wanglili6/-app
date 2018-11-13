package com.itdream.wll.prient;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itdream.wll.prient.base.Constants;
import com.itdream.wll.prient.base.MyApplacation;
import com.itdream.wll.prient.bean.RegisBean;
import com.itdream.wll.prient.bean.RegisInfo;
import com.itdream.wll.prient.servser.RegisServer;
import com.itdream.wll.prient.utils.Base64Util;
import com.itdream.wll.prient.utils.HttpCacheUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.sms.SMSSDK;
import cn.jpush.sms.listener.SmscheckListener;
import cn.jpush.sms.listener.SmscodeListener;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ResgisActivity extends AppCompatActivity {

    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.et_login_name)
    EditText etLoginName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_get_code)
    Button btnGetCode;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private TimerTask timerTask;
    private Timer timer;
    private int timess;
    private ProgressDialog progressDialog;
    private String TAG = Constants.Tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgis);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        SMSSDK.getInstance().initSdk(this);
        SMSSDK.getInstance().setIntervalTime(30 * 1000);
        tvCenter.setText("注册");
        tvLeft.setText("返回");
    }

    @OnClick({R.id.tv_left, R.id.btn_get_code, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.btn_get_code:
                //获取验证码
                String phoneNum = etCode.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(ResgisActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnGetCode.setClickable(false);
                //开始倒计时
                startTimer();
                SMSSDK.getInstance().getSmsCodeAsyn(phoneNum, 1 + "", new SmscodeListener() {
                    @Override
                    public void getCodeSuccess(final String uuid) {
                        Toast.makeText(ResgisActivity.this, uuid, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void getCodeFail(int errCode, final String errmsg) {
                        //失败后停止计时
                        stopTimer();
                        Toast.makeText(ResgisActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_login:
                //注册
                String phone = etLoginName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
//                if (TextUtils.isEmpty(phone)) {
//                    Toast.makeText(ResgisActivity.this, "手机号码不能为空!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(toBase64Pwd)) {
//                    Toast.makeText(ResgisActivity.this, "手机号码不能为空!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                String toBase64Pwd = Base64Util.toBase64("111111");
                final RegisBean regisBean = new RegisBean();
                regisBean.setPassword(toBase64Pwd);
                regisBean.setPhone("18310459359");
                String json = MyApplacation.getGson().toJson(regisBean);
                RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),
                        "{\"regisMsg\":" + json + "}");
                RegisServer regisServer = HttpCacheUtils.getRetrofit(ResgisActivity.this).create(RegisServer.class);
                String encode = Uri.encode(json);
                post(encode, regisServer);
//                regisServer.modelPost(requestBody).enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        Toast.makeText(ResgisActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
//                        Log.i(TAG, "onResponse: " + response.toString());
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                    }
//                });

                break;
        }
    }

    private void post(String json, RegisServer regisServer) {

        regisServer.regis(json).subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<RegisInfo>() {
                    @Override
                    public void call(RegisInfo userInfo) {
//                        saveUserInfo(userInfo);//保存用户信息到本地
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<RegisInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                    }

                    @Override
                    public void onNext(RegisInfo userInfo) {
                        Log.i(TAG, "onNext: " + userInfo);
                    }
                });
    }

    /**
     * 短信注册
     */
    private void msgRegis() {
        String code = etCode.getText().toString();
        String phone = etLoginName.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(ResgisActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(ResgisActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setTitle("正在验证...");
        progressDialog.show();
        SMSSDK.getInstance().checkSmsCodeAsyn(phone, code, new SmscheckListener() {
            @Override
            public void checkCodeSuccess(final String code) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(ResgisActivity.this, code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void checkCodeFail(int errCode, final String errmsg) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(ResgisActivity.this, errmsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTimer() {
        timess = (int) (SMSSDK.getInstance().getIntervalTime() / 1000);
        btnGetCode.setText(timess + "s");
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timess--;
                            if (timess <= 0) {
                                stopTimer();
                                return;
                            }
                            btnGetCode.setText(timess + "s");
                        }
                    });
                }
            };
        }
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(timerTask, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        btnGetCode.setText("重新获取");
        btnGetCode.setClickable(true);
    }
}
