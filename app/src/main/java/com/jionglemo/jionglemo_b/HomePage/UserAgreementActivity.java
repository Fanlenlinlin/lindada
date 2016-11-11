package com.jionglemo.jionglemo_b.HomePage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jionglemo.jionglemo_b.R;

/**
 * Created by JenKin on 2016/10/26.
 */

public class UserAgreementActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useragreement);

        webView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        //webSettings.setBuiltInZoomControls(true);
        //加载需要显示的网页
        webView.loadUrl("file:///android_asset/seller-agreement.html");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void back(View view){
        finish();
    }

}
