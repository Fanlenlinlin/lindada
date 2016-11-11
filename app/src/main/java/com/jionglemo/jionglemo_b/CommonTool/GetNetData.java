package com.jionglemo.jionglemo_b.CommonTool;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Mike on 2016/7/1.
 *  对AsyncHttpClient进行二次封装，可用于重新获取auth_key
 */
public class GetNetData {

    private Context context;
    private boolean lock=true;
    private static Toast mToast=null;//使用一个静态的Toast，防止多次弹出

    public GetNetData(Context context){
        this.context=context;
    }

    public void getData(final RequestParams params, final String url){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("auth_key", CommonValue.getAuthKey(context));
        client.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("guodingyuan","JSONObject呵呵--onSuccess"+response.toString());
                try {
                    if(response.getInt("status")==900008||response.getInt("status")==900029){//未授权或授权过期
                        if(lock){
                            getAuthKey(params,url);
                            lock=false;
                        }
                    }else {
                        getDataSuccess(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //如果有异常，说明不含有status字段，判定获取成功
                    getDataSuccess(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                getDataFailure();
                showToast();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("guodingyuan","JSONArray呵呵"+response.toString());
                getDataSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                showToast();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Log.i("guodingyuan","Stringy呵呵"+responseString);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("guodingyuan","String呵呵呵呵--onFailure"+responseString);
                getDataFailure(responseString);
            }
        });
    }

    //封装一个方法用于重新获取auth_key
    private void getAuthKey(RequestParams yuanParms, String url){
         getAuthKeyByPlatformUser(yuanParms,url);//平台用户获取auth_key
    }

    //平台用户获取auth_key
    private void getAuthKeyByPlatformUser(final RequestParams yuanParms, final String url){
        RequestParams params = new RequestParams();
        params.put("accounts", CommonValue.getLoadPhone(context));
        params.put("pwd", AES.decrypt("getResources()",CommonValue.getLoadPassword(context)));
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(CommonValue.login, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("guodingyuan","哈哈"+response.toString());
                try {
                    int status=response.getInt("status");
                    if(status==1){
                        JSONObject jsonObj =  response.getJSONObject("message");
                        //本地重新更新auth_key
                        SharedPreferences.Editor editor = context.getSharedPreferences(CommonValue.UserMessage,0).edit();
                        editor.putString("authKey", jsonObj.getString("auth_key"));
                        editor.commit();
                        getData(yuanParms,url);//获取到新的auth_key后重新发起请求
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                showToast();
            }
        });
    }



    //定义一个公有的方法供外部调用
    public void getDataSuccess(JSONObject response){

    }

    //定义一个公有的方法供外部调用
    public void getDataFailure(){

    }

    //定义一个公有的方法供外部调用
    public void getDataSuccess(JSONArray response){

    }

    //定义一个公有的方法供外部调用
    public void getDataFailure(String responseString){

    }

    /** 不会一直重复重复重复重复的提醒了 */
    protected void showToast() {
        if (mToast == null) {
            mToast = Toast.makeText(context, "请求失败，请检查您的网络状态", Toast.LENGTH_SHORT);
        } else {
            mToast.setText("请求失败，请检查您的网络状态");
        }
        mToast.show();
    }
}
