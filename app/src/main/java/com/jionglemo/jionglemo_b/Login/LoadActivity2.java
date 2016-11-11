package com.jionglemo.jionglemo_b.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.BasePacket.MainActivity;
import com.jionglemo.jionglemo_b.CommonTool.AES;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.PictureTool;
import com.jionglemo.jionglemo_b.CommonTool.StatusBarUtil;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.DB.UserHistoryDB;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class LoadActivity2 extends AppCompatActivity {

    public static LoadActivity2 loadActivity2;//使用一个公用的静态的activity实例，用于后续可关闭该activity

    private EditText phoneET;
    private EditText passwordET;
    private LinearLayout loadLL;
    private ImageView logo_titleIV;
    private float scale;
    private PopupWindow popupWindow;
    private RecyclerView userHistoryRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(CommonValue.getLoadState(this)){ //如果用户处于登录状态，直接进入主界面
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);//启动主界面
            finish();
        }

        loadActivity2=this;

        //设置状态栏黑色字体
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_load2);

        phoneET = (EditText) findViewById(R.id.phoneET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        logo_titleIV = (ImageView) findViewById(R.id.logo_titleIV);
        logo_titleIV.setImageBitmap(PictureTool.readBitMap(this,R.drawable.logo_title));

        ImageView loadIV= (ImageView) findViewById(R.id.loadIV);
        loadIV.setImageBitmap(PictureTool.readBitMap(this,R.drawable.load));

        loadLL= (LinearLayout) findViewById(R.id.loadLL);
        loadLL.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = loadLL.getRootView().getHeight() - loadLL.getHeight();
                if (heightDiff > 100) {
                    // 说明键盘是弹出状态
                    Log.v("guodingyuan", "键盘弹出状态");
                    logo_titleIV.setVisibility(View.GONE);

                } else {
                    Log.v("guodingyuan", "键盘收起状态");
                    logo_titleIV.setVisibility(View.VISIBLE);
                }
            }
        });

        scale= getResources().getDisplayMetrics().density;
    }

    //下拉选择
    public void dropdown(View view){

        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View userHistoryView = layoutInflater.inflate(R.layout.userhistory_popupwindow, null);
            userHistoryRV = (RecyclerView) userHistoryView.findViewById(R.id.userHistoryRV);
            // 创建一个PopuWidow对象
            popupWindow = new PopupWindow(userHistoryView, phoneET.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            userHistoryRV.setLayoutManager(linearLayoutManager);

            UserHistoryDB userHistoryDB= UserHistoryDB.getInstance(LoadActivity2.this);
            List<String> list= userHistoryDB.loadUserHistory();
            if(list.size()==0)
                list.add("暂无任何登录记录");
            UserHistoryRVadapter userHistoryRVadapter =new UserHistoryRVadapter(this,list,userHistoryDB){
                @Override
                public void checkUser(String phone) {
                    super.checkUser(phone);
                    phoneET.setText(phone);
                    popupWindow.dismiss();
                }
            };
            userHistoryRV.setAdapter(userHistoryRVadapter);
        }
        // 使其聚集
        // popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.city_name_popwindow_anim_style);// 设置popWindow的显示和消失动
        popupWindow.showAsDropDown(phoneET,(int)(20*scale),0);
    }

    //登录
    public void load(View view){
        RequestParams params = new RequestParams();
        params.put("pwd", passwordET.getText().toString());
        params.put("accounts", phoneET.getText().toString());
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e("TAG",CommonValue.login+params.toString());
        client.post(CommonValue.login, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Log.i("guodingyuan",responseString.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("guodingyuan",responseString.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int status=response.getInt("status");
                    if(status==1){
                        JSONObject jsonObj =  response.getJSONObject("message");
                        Toast.makeText(LoadActivity2.this,jsonObj.getString("msg"),Toast.LENGTH_SHORT).show();

                        //进行本地化存储
                        SharedPreferences.Editor editor = getSharedPreferences(CommonValue.UserMessage,0).edit();
                        editor.putBoolean("loadState",true);
                        editor.putString("loadPhone",phoneET.getText().toString());
                        editor.putString("loadPassword", AES.encrypt("getResources()",passwordET.getText().toString()));
                        editor.putString("authKey", jsonObj.getString("auth_key"));
                        editor.putInt("stype", jsonObj.getInt("stype"));
                        editor.commit();

                        //进行用户登录记录的本地化存储
                        UserHistoryDB userHistoryDB=UserHistoryDB.getInstance(LoadActivity2.this);
                        userHistoryDB.saveUserHistory(phoneET.getText().toString());

                        if(jsonObj.getInt("stype")==1) {//店铺登录
                            //获取店铺的信息
                            GetNetData getSalesInfo=new GetNetData(LoadActivity2.this){
                                @Override
                                public void getDataSuccess(JSONObject response) {
                                    super.getDataSuccess(response);
                                    //进行本地化存储
                                    try {
                                        SharedPreferences.Editor editor = getSharedPreferences(CommonValue.UserMessage,0).edit();
                                        editor.putInt("jid",response.getInt("jid"));
                                        editor.putString("rong_token",response.getString("im_key"));
                                        editor.putInt("sales_id",response.getInt("id"));
                                        editor.putInt("store_id",response.getInt("store_id"));
                                        editor.putString("userName",response.getString("store_name"));
                                        editor.putString("portrait",response.getString("logo"));
                                        editor.commit();

                                        //连接融云
                                        RongIM.connect(CommonValue.getRongToken(LoadActivity2.this), new RongIMClient.ConnectCallback() {
                                            @Override
                                            public void onTokenIncorrect() {

                                            }

                                            @Override
                                            public void onSuccess(String s) {
                                                //登录成功后刷新一下当前用户的数据，因为商家版的导购头像获取不了
                                                RongIM.getInstance().refreshUserInfoCache(new UserInfo(String.valueOf(CommonValue.getJid(LoadActivity2.this)),CommonValue.getUserName(LoadActivity2.this), Uri.parse(CommonValue.serverBasePath+CommonValue.getPortraitPath(LoadActivity2.this))));
                                                //当连接融云成功后，再让其登录，否则会使得消息列表获取后又没连接上
                                                Intent intent=new Intent(LoadActivity2.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
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
                            getSalesInfo.getData(null,CommonValue.getStoreInfo);

                          /*  SharedPreferences.Editor editor0 = getSharedPreferences(CommonValue.UserMessage,0).edit();
                            editor0.putInt("user_id",71);
                            editor0.putInt("sales_id",1);
                            editor0.putInt("store_id",1);
                            editor0.putString("userName","情话腻耳");
                            editor0.putString("portrait","/picture/rcVTohVFQplp.jpg");
                            editor0.putString("rong_token","JDMEeAUwM0lOiado+FzzXgVDU5RuU59bbbUeIG9u9WGFyUgTZd49I6C2hIEX4ojChqY6y1d4yXvrDdPfl0C7Hg==");
                            editor0.commit();

                            RongIM.connect(CommonValue.getRongToken(LoadActivity2.this), new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {

                                }

                                @Override
                                public void onSuccess(String s) {
                                    //登录成功后刷新一下当前用户的数据，因为商家版的导购头像获取不了
                                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(String.valueOf(CommonValue.getJid(LoadActivity2.this)),CommonValue.getUserName(LoadActivity2.this), Uri.parse(CommonValue.serverBasePath+CommonValue.getPortraitPath(LoadActivity2.this))));
                                    //当连接融云成功后，再让其登录，否则会使得消息列表获取后又没连接上
                                    Intent intent=new Intent(LoadActivity2.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
*/
                        }else {
                            //获取导购的信息
                            GetNetData getSalesInfo=new GetNetData(LoadActivity2.this){
                                @Override
                                public void getDataSuccess(JSONObject response) {
                                    super.getDataSuccess(response);
                                    //进行本地化存储
                                    try {
                                        SharedPreferences.Editor editor = getSharedPreferences(CommonValue.UserMessage,0).edit();
                                        editor.putInt("jid",response.getInt("jid"));
                                        editor.putString("rong_token",response.getString("im_key"));
                                        editor.putInt("sales_id",response.getInt("id"));
                                        editor.putInt("store_id",response.getInt("store_id"));
                                        editor.putString("userName",response.getString("sales_name"));
                                        editor.putString("portrait",response.getString("portrait"));
                                        editor.commit();

                                        //连接融云
                                        RongIM.connect(CommonValue.getRongToken(LoadActivity2.this), new RongIMClient.ConnectCallback() {
                                            @Override
                                            public void onTokenIncorrect() {

                                            }

                                            @Override
                                            public void onSuccess(String s) {
                                                //登录成功后刷新一下当前用户的数据，因为商家版的导购头像获取不了
                                                RongIM.getInstance().refreshUserInfoCache(new UserInfo(String.valueOf(CommonValue.getJid(LoadActivity2.this)),CommonValue.getUserName(LoadActivity2.this), Uri.parse(CommonValue.serverBasePath+CommonValue.getPortraitPath(LoadActivity2.this))));
                                                //当连接融云成功后，再让其登录，否则会使得消息列表获取后又没连接上
                                                Intent intent=new Intent(LoadActivity2.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
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
                            getSalesInfo.getData(null,CommonValue.getStoreSalesInfo);
                        }
                    }else {
                        String message=response.getString("message");
                        Toast.makeText(LoadActivity2.this,message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(LoadActivity2.this,"登录失败，请检查您的网络状态",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //忘记密码
    public void forgetPassword(View view){
        Intent intent=new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }
}
