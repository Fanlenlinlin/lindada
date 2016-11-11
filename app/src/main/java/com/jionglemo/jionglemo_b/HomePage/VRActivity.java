package com.jionglemo.jionglemo_b.HomePage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.WebviewErrorAndLoad;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.CommonView.SharePopupWindow;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VRActivity extends AppCompatActivity {
    private WebView webView;
    private boolean blockLoadingNetworkImage=false;
    private String link;
    private ProgressDialog loadingProgressDialog = null;
    private LinearLayout topBarLL;
    private TextView fullscreenTV;
    private ImageView exit_fullscreenIV;
    private PopupWindow popupWindow;
    private RecyclerView productListRV;

    public static int p;//当前页数
    public static int total_page;//总页数
    private VRstoreProductRVadapter vRstoreProductRVadapter;
    private int lastPosition;
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private List<Product> productList=new ArrayList<>();
    private String shopName;
    private WebSettings setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //必须在setContentView之前调用
        //去除toolbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar，即状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_vr);

        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    link=response.getString("message");
                    //webView.loadUrl(link);
                    WebviewErrorAndLoad.loadNewUrl(VRActivity.this,webView,link,topBarLL,fullscreenTV);
                    showDialog();
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
                fullscreenTV.setVisibility(View.GONE);
            }
        };
        RequestParams params=new RequestParams();
        params.put("store_id",CommonValue.getStoreId(this));
        getNetData.getData(params,CommonValue.storeUrl);

        TextView shopNameTV= (TextView) findViewById(R.id.shopNameTV);
        shopName = getIntent().getStringExtra("store_name");
        shopNameTV.setText(shopName);

        webView = (WebView) findViewById(R.id.tencentWebView);
        setting = webView.getSettings();
        //setting.setBuiltInZoomControls(true);
        setting.setJavaScriptEnabled(true);
        setting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        setting.setBlockNetworkImage(true);
        blockLoadingNetworkImage=true;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(loadingProgressDialog!=null&&loadingProgressDialog.isShowing())
                    loadingProgressDialog.setProgress(progress);
                if (progress >= 100) {
                    if(blockLoadingNetworkImage) {
                        setting.setBlockNetworkImage(false);
                        blockLoadingNetworkImage=false;
                    }
                    if (loadingProgressDialog!=null&&loadingProgressDialog.isShowing())
                        loadingProgressDialog.dismiss();
                }
            }
        });
        webView.addJavascriptInterface(new JSBridge(), "VirtualStore");//注入接口给WebView调用
        webView.addJavascriptInterface(new JSBridge(),"Quit");//退出
        webView.addJavascriptInterface(new JSBridge(),"GotoSet");//去设置


        topBarLL = (LinearLayout) findViewById(R.id.topBarLL);
        fullscreenTV = (TextView) findViewById(R.id.fullscreenTV);
        exit_fullscreenIV = (ImageView) findViewById(R.id.exit_fullscreenIV);
        exit_fullscreenIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topBarLL.setVisibility(View.VISIBLE);
                fullscreenTV.setVisibility(View.VISIBLE);
                exit_fullscreenIV.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏设置
            }
        });
    }

    /**
     * 定义一个类和方法供前端代码调用
     */
    public class JSBridge {
        @JavascriptInterface
        public void goodsShelfId(String message) {
            Log.i("guodingyuan",message);
            GetNetData getNetData=new GetNetData(VRActivity.this){
                @Override
                public void getDataSuccess(JSONObject response) {
                    super.getDataSuccess(response);
                    try {
                        final List<Product> mProductList = JSON.parseArray(response.getJSONArray("data_list").toString(),Product.class);
                        JSONObject pageJSON=response.getJSONObject("page");
                        p = pageJSON.getInt("p");
                        total_page = pageJSON.getInt("total_page");
                        //注意：这里需要用UI线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            showProductPopupWindow(mProductList);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestParams params=new RequestParams();
            params.put("store_id",CommonValue.getStoreId(VRActivity.this));
            getNetData.getData(params,CommonValue.shelfProductList);
        }

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

    //展示虚拟店铺里的产品
    private void showProductPopupWindow(List<Product> mProductList){
        if(productList.size()!=0)
            productList.clear();
        productList.addAll(mProductList);
        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vrstoreProductView = layoutInflater.inflate(R.layout.vrstore_product_popupwindow, null);
            TextView outTV= (TextView) vrstoreProductView.findViewById(R.id.outTV);
            productListRV = (RecyclerView) vrstoreProductView.findViewById(R.id.productListRV);
            final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
            productListRV.setLayoutManager(linearLayoutManager);
            productListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //滑到最后一条再向下滑动时重新获取数据，加载完毕后就不进行加载
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == productList.size() && p<total_page) {
                        if(lock){
                            lock=false;//锁住
                            getMoreProductData();
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                }
            });

            // 创建一个PopuWidow对象
            popupWindow = new PopupWindow(vrstoreProductView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
            //点击外部关闭
            outTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
        //绑定数据
        vRstoreProductRVadapter = new VRstoreProductRVadapter(this,productList){
            @Override
            public void loadAgain() {
                super.loadAgain();
                getMoreProductData();
            }
        };
        productListRV.setAdapter(vRstoreProductRVadapter);

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置popWindow的显示和消失动画
        popupWindow.showAtLocation(webView, Gravity.BOTTOM,0,0);
    }

    //获取更多产品的数据
    private void getMoreProductData(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<Product> list= JSON.parseArray(response.getJSONArray("data_list").toString(),Product.class);
                    productList.addAll(list);
                    vRstoreProductRVadapter.notifyDataSetChanged();

                    JSONObject pageJSON=response.getJSONObject("page");
                    p = pageJSON.getInt("p");
                    total_page = pageJSON.getInt("total_page");

                    lock=true;//解锁
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getDataFailure() {
                super.getDataFailure();
                if(vRstoreProductRVadapter!=null){
                    vRstoreProductRVadapter.loadError = true;
                    vRstoreProductRVadapter.notifyItemChanged(vRstoreProductRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    lock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("p",p+1);
        params.put("store_id",CommonValue.getStoreId(this));
        getNetData.getData(params,CommonValue.shelfProductList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView.getProgress() < 100)
            showDialog();
    }

    @Override
    protected void onDestroy() {
        webView.stopLoading();
        webView.destroy();
        super.onDestroy();
    }

    //展示对话框
    private void showDialog(){
        loadingProgressDialog = new ProgressDialog(this);
        loadingProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loadingProgressDialog.setMessage("正在拼命加载中，请稍后···");
        loadingProgressDialog.setMax(100);
        loadingProgressDialog.show();
    }

    public void back(View view){
        finish();
    }

    //全屏显示
    public void fullscreen(View view){
        topBarLL.setVisibility(View.GONE);
        fullscreenTV.setVisibility(View.GONE);
        exit_fullscreenIV.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏设置
    }


    //分享
    public void share(View view){
        SharePopupWindow sharePopupWindow=new SharePopupWindow(this);
        sharePopupWindow.showSharePopupWindow(view);
    }
}
