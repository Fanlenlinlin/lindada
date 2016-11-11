package com.jionglemo.jionglemo_b.CommonTool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 根据网络响应码区分不同的错误
 *
 * Created by JenKin on 2016/10/31.
 */

public class WebviewErrorAndLoad {

    private static Runnable runnable;

    public static void loadNewUrl(Context context, final WebView webView, final String url , final LinearLayout topBarLL, final TextView textView) {

        runnable=new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn=(HttpURLConnection)new URL(url).openConnection();
                    final int code=conn.getResponseCode();
                    Log.e("aaa", "响应码run: "+code );
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            if(code==200){
                                webView.loadUrl(url);
                                topBarLL.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.GONE);
                            }else if (code == 404){
                                webView.loadUrl("file:///android_asset/error.html");
                                topBarLL.setVisibility(View.GONE);
                                textView.setVisibility(View.GONE);
                            }else if (code == 500){
                                webView.loadUrl("file:///android_asset/error1.html");
                                topBarLL.setVisibility(View.GONE);
                                textView.setVisibility(View.GONE);
                            }else if (code == 601){
                                webView.loadUrl("file:///android_asset/error2.html");
                                topBarLL.setVisibility(View.GONE);
                                textView.setVisibility(View.GONE);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        network(context, webView);
    }

    public static void loadNewUrl2(Context context, final WebView webView, final String url, final RelativeLayout topBarLL) {

        runnable=new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn=(HttpURLConnection)new URL(url).openConnection();
                    final int code=conn.getResponseCode();
                    Log.e("aaa", "响应码run: "+code );
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            if(code==200){
                                webView.loadUrl(url);
                                topBarLL.setVisibility(View.VISIBLE);
                            }else if (code == 404){
                                webView.loadUrl("file:///android_asset/error.html");
                                topBarLL.setVisibility(View.GONE);
                            }else if (code == 500){
                                webView.loadUrl("file:///android_asset/error1.html");
                                topBarLL.setVisibility(View.GONE);
                            }else if (code == 601){
                                webView.loadUrl("file:///android_asset/error2.html");
                                topBarLL.setVisibility(View.GONE);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        network(context, webView);
    }

    private static void network(Context context, WebView webView) {
        //先判断是否已经联网
        ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if ( netinfo !=null && netinfo.isConnected() ) {
            new Thread(runnable).start();//在联网的条件下，进行URL地址的正确性检验
        }else{
            webView.loadUrl("file:///android_asset/error2.html");
        }
    }
}
