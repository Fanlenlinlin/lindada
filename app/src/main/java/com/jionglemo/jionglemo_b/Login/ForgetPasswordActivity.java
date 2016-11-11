package com.jionglemo.jionglemo_b.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.BasePacket.MainActivity;
import com.jionglemo.jionglemo_b.CommonTool.AES;
import com.jionglemo.jionglemo_b.CommonTool.StatusBarUtil;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.DB.UserHistoryDB;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cz.msebera.android.httpclient.Header;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText phoneET;
    private EditText checkCodeET;
    private TextView getCheckCodeTV;
    private int second;//用于倒计时记录秒数
    private Timer timer;//计时器，用于验证码倒计时
    private EditText passwordET;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //状态栏完全透明
        StatusBarUtil.transparencyBar(this);
        //设置状态栏黑色字体
        StatusBarUtil.StatusBarLightMode(this);

        setContentView(R.layout.activity_forget_password);

        TextView stateBarTV = (TextView)findViewById(R.id.stateBarTV);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){//v19（4.4）以后开始支持android:windowTranslucentStatus属性，透明状态栏，而v21（5.0）以后出现变色状态栏，可以自由设置状态栏颜色。
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) stateBarTV.getLayoutParams(); //取控件textView当前的布局参数
            linearParams.height = StatusBarUtil.getStatusBarHeight(this);// 系统状态栏的高度
            stateBarTV.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        }else {
            stateBarTV.setVisibility(View.GONE);
        }

        phoneET = (EditText) findViewById(R.id.phoneET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        checkCodeET = (EditText) findViewById(R.id.checkCodeET);
        getCheckCodeTV = (TextView) findViewById(R.id.getCheckCodeTV);

        //初始化短信
        SMSSDK.initSDK(this, "16f7dde9c0dc0", "1c9c85ccf64d688c40077dd32649ce4a");
        SMSSDK.registerEventHandler(eh); //注册短信回调
        handler = new Handler();
    }

    //短信回调
    private EventHandler eh=new EventHandler(){

        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //往后台写入数据
                            RequestParams params = new RequestParams();
                            params.put("pwd", passwordET.getText().toString());
                            params.put("accounts", phoneET.getText().toString());

                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post(CommonValue.findPassword, params, new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    try {
                                        int status=response.getInt("status");
                                        if(status==1){
                                            JSONObject jsonObj =  response.getJSONObject("message");
                                            Toast.makeText(ForgetPasswordActivity.this,jsonObj.getString("msg"),Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(ForgetPasswordActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            LoadActivity2.loadActivity2.finish();
                                            //进行本地化存储
                                            SharedPreferences.Editor editor = getSharedPreferences(CommonValue.UserMessage,0).edit();
                                            editor.putBoolean("loadState",true);
                                            editor.putString("loadPhone",phoneET.getText().toString());
                                            editor.putString("loadPassword", AES.encrypt("getResources()",passwordET.getText().toString()));
                                            editor.putString("authKey", jsonObj.getString("auth_key"));
                                            editor.commit();

                                            //获取用户个人信息
                                        /*    GetNetData getNetData=new GetNetData(ForgetPasswordActivity.this){
                                                @Override
                                                public void getDataSuccess(JSONObject response) {
                                                    super.getDataSuccess(response);
                                                    try {
                                                        SharedPreferences.Editor editor = getSharedPreferences(CommonValue.UserMessage,0).edit();
                                                        editor.putInt("user_id",response.getInt("user_id"));
                                                        editor.putString("userName",response.getString("user_name"));
                                                        editor.putString("portrait",response.getString("portrait"));
                                                        editor.putInt("money",response.getInt("money"));
                                                        editor.putInt("integral",response.getInt("integral"));
                                                        editor.putString("year",response.getString("year"));
                                                        editor.putString("month",response.getString("month"));
                                                        editor.putString("day",response.getString("day"));
                                                        editor.putString("sex",response.getString("sex"));
                                                        editor.putString("sexOrientation",response.getString("sex_orientation"));
                                                        editor.putString("city",response.getString("city"));
                                                        editor.commit();

                                                        //获取融云token值进行本地存储并进行连接
                                                        GetNetData getToken=new GetNetData(ForgetPasswordActivity.this){
                                                            @Override
                                                            public void getDataSuccess(JSONObject response) {
                                                                super.getDataSuccess(response);
                                                                try {
                                                                    String token=response.getString("token");
                                                                    SharedPreferences.Editor editor = getSharedPreferences(CommonValue.UserMessage,0).edit();
                                                                    editor.putString("rong_token",token);//对token值进行存储
                                                                    editor.commit();
                                                                    RongIM.connect(token, new RongIMClient.ConnectCallback() {
                                                                        @Override
                                                                        public void onTokenIncorrect() {

                                                                        }

                                                                        @Override
                                                                        public void onSuccess(String s) {

                                                                        }

                                                                        @Override
                                                                        public void onError(RongIMClient.ErrorCode errorCode) {

                                                                        }
                                                                    });
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        };
                                                        RequestParams params=new RequestParams();
                                                        params.put("user_id", CommonValue.getUser_id(ForgetPasswordActivity.this));
                                                        params.put("user_name",CommonValue.getUserName(ForgetPasswordActivity.this));
                                                        params.put("portrait_uri",CommonValue.serverBasePath+CommonValue.getPortraitPath(ForgetPasswordActivity.this));
                                                        getToken.getData(params,CommonValue.getChatToken);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            getNetData.getData(null,CommonValue.userMessage);*/


                                            //进行用户登录记录的本地化存储
                                            UserHistoryDB userHistoryDB=UserHistoryDB.getInstance(ForgetPasswordActivity.this);
                                            userHistoryDB.saveUserHistory(phoneET.getText().toString());

                                        }else {
                                            String message=response.getString("message");
                                            Toast.makeText(ForgetPasswordActivity.this,message,Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(ForgetPasswordActivity.this,"更新密码失败，请检查您的网络状态",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    Log.i("guodingyuan","获取验证码成功");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getCheckCodeTV.setClickable(false); //设置按钮无法点击
                        }
                    });
                    second = 60;//初始化为60s
                    timer = new Timer();
                    TimeTask timeTask=new TimeTask();
                    timer.schedule(timeTask, 0, 1000);// 0秒后启动任务,以后每隔1秒执行一次线程
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                    Log.i("guodingyuan","返回支持发送验证码的国家列表");
                }
            }else{
                final Throwable throwable= (Throwable) data;
                try {
                    JSONObject jsonObj = new JSONObject(throwable.getMessage());
                    final String detail=jsonObj.getString("detail");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ForgetPasswordActivity.this, detail,Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //使用新密码登录
    public void load(View view){
        String phone=phoneET.getText().toString();
        String code=checkCodeET.getText().toString();
        String password=passwordET.getText().toString().trim();
        if(password.length()<6)
            Toast.makeText(this,"密码不能少于六位",Toast.LENGTH_SHORT).show();
        else
            //提交短信验证码，在监听中返回
            SMSSDK.submitVerificationCode("86",phone,code);
    }

    //定义一个计时器用于倒计时数秒
    private class TimeTask extends TimerTask {
        @Override
        public void run() {
            if(second>=0){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getCheckCodeTV.setText("剩余"+(second--)+"s");
                    }
                });
            } else{
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getCheckCodeTV.setClickable(true);//重新设置按钮可点击
                        getCheckCodeTV.setText("获取验证码");
                        timer.cancel();//终止timer
                        timer=null;
                    }
                });
            }
        }
    }

    //获取验证码
    public void getCheckCode(View view){
        String phone=phoneET.getText().toString();
        //获取短信目前支持的国家列表，在监听中返回
        //SMSSDK.getSupportedCountries();
        //请求获取短信验证码，在监听中返回
        SMSSDK.getVerificationCode("86",phone);
    }

    public void back(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh); //注销回调接口
    }
}
