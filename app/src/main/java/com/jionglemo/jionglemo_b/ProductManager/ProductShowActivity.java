package com.jionglemo.jionglemo_b.ProductManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.WebviewErrorAndLoad;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductShowActivity extends AppCompatActivity {

    private WebView webView;
    private TextView progressTV;
    private RelativeLayout topBarLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_show);
        int product_id = getIntent().getIntExtra("product_id",1);

        topBarLL = (RelativeLayout) findViewById(R.id.topBarLL);
        webView = (WebView) findViewById(R.id.tencentWebView);
        progressTV = (TextView) findViewById(R.id.progressTV);
        WebSettings setting = webView.getSettings();
        //setting.setBuiltInZoomControls(true);
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if(i>=100)
                    progressTV.setVisibility(View.GONE);
            }
        });

        //获取网页版产品详情
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    //webView.loadUrl(response.getString("message"));
                    WebviewErrorAndLoad.loadNewUrl2(ProductShowActivity.this,webView,response.getString("message"),topBarLL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getDataFailure() {
                super.getDataFailure();
                webView.loadUrl("file:///android_asset/error2.html");
                webView.addJavascriptInterface(new JSBridge(),"Quit");//退出
                webView.addJavascriptInterface(new JSBridge(),"GotoSet");//去设置
                topBarLL.setVisibility(View.GONE);
            }
        };
        RequestParams params=new RequestParams();
        params.put("product_id",product_id);
        getNetData.getData(params,CommonValue.getProductDetails);

        webView.addJavascriptInterface(new JSBridge(),"Quit");//退出
        webView.addJavascriptInterface(new JSBridge(),"GotoSet");//去设置
    }

    /**
     * 定义一个类和方法供前端代码调用
     */
    public class JSBridge {
        @JavascriptInterface
        public void out(){
            Log.i("guodingyuan","退出");
            finish();
        }

        @JavascriptInterface
        public void gotoSetting(){
            Log.i("guodingyuan","去设置");
            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        webView.stopLoading();
        webView.destroy();
        super.onDestroy();
    }

    //返回
    public void back(View view){
        finish();
    }
}
