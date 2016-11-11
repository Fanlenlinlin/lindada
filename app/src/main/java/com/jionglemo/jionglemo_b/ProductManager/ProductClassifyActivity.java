package com.jionglemo.jionglemo_b.ProductManager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductClassifyActivity extends AppCompatActivity {

    public static int p;//当前页数
    public static int total_page;//总页数

    private int classify_id;
    private List<ProductManager> productManagerList;
    private ProductManagerRVadapter productManagerRVadapter;
    private RecyclerView productClassifyRV;
    private LinearLayoutManager linearLayoutManager;
    private int lastPosition;
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private LinearLayout noneProductLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_classify);

        classify_id = getIntent().getIntExtra("classify_id",0);

        TextView classifyNameTV = (TextView) findViewById(R.id.classifyNameTV);
        classifyNameTV.setText(getIntent().getStringExtra("name"));
        productClassifyRV = (RecyclerView) findViewById(R.id.productClassifyRV);
        linearLayoutManager = new LinearLayoutManager(this);
        productClassifyRV.setLayoutManager(linearLayoutManager);
        noneProductLL = (LinearLayout)findViewById(R.id.noneProductLL);

        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    productManagerList = JSON.parseArray(response.getJSONArray("data_list").toString(),ProductManager.class);
                    if(productManagerList.size()==0){
                        productClassifyRV.setVisibility(View.GONE);
                        noneProductLL.setVisibility(View.VISIBLE);
                    }else {
                        productClassifyRV.setVisibility(View.VISIBLE);
                        noneProductLL.setVisibility(View.GONE);

                        JSONObject pageJSON=response.getJSONObject("page");
                        p = pageJSON.getInt("p");
                        total_page = pageJSON.getInt("total_page");
                        productManagerRVadapter = new ProductManagerRVadapter(ProductClassifyActivity.this,productManagerList,-1){
                            @Override
                            public void loadAgain() {
                                super.loadAgain();
                                getMoreProductData();
                            }
                        };
                        productClassifyRV.setAdapter(productManagerRVadapter);
                        productClassifyRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                //滑到最后一条再向下滑动时重新获取数据，加载完毕后就不进行加载
                                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == productManagerList.size() && p<total_page) {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("classify_id", classify_id);
        getNetData.getData(params, CommonValue.classifyProduct);
    }

    //获取更多商品数据
    private void getMoreProductData(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<ProductManager> list= JSON.parseArray(response.getJSONArray("data_list").toString(),ProductManager.class);
                    productManagerList.addAll(list);
                    productManagerRVadapter.notifyDataSetChanged();
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
                if(productManagerRVadapter !=null){
                    productManagerRVadapter.loadError = true;
                    productManagerRVadapter.notifyItemChanged(productManagerRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    lock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("p",p+1);
        params.put("classify_id", classify_id);
        getNetData.getData(params, CommonValue.classifyProduct);
    }

    //返回
    public void back(View view){
        finish();
    }
}
