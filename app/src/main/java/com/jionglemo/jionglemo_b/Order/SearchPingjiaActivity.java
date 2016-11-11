package com.jionglemo.jionglemo_b.Order;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SearchPingjiaActivity extends AppCompatActivity {

    private List<Pingjia> pingjiaList=new ArrayList<>();
    private PingjiaRVadapter pingjiaRVadapter;
    private LinearLayout kong_searchResultLL;
    private RecyclerView searchResultRV;
    public static int p;//当前页数
    public static int total_page;//总页数
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private LinearLayoutManager linearLayoutManager;
    private int lastPosition;
    private String queryPingjia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pingjia);
        searchResultRV = (RecyclerView) findViewById(R.id.searchResultRV);
        linearLayoutManager=new LinearLayoutManager(this);
        searchResultRV.setLayoutManager(linearLayoutManager);
        kong_searchResultLL = (LinearLayout) findViewById(R.id.kong_searchResultLL);

        SearchView searchView= (SearchView) findViewById(R.id.searchView);

        //获取到TextView的ID
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        //获取到TextView的控件
        TextView textView = (TextView) searchView.findViewById(id);
        //设置字体大小为14sp
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//14sp
        //设置字体颜色
        textView.setTextColor(Color.BLACK);
        //设置提示文字颜色
        textView.setHintTextColor(Color.rgb(117,117,117));

        //针对于API小于21，去掉SearchView的下划线
        if (searchView != null) {
            try {
                //--拿到字节码
                Class<?> argClass = searchView.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(searchView);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        //输入监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryPingjia = query;
                GetNetData getNetData=new GetNetData(SearchPingjiaActivity.this){
                    @Override
                    public void getDataSuccess(JSONObject response) {
                        super.getDataSuccess(response);
                        try {
                            List<Pingjia> list = JSON.parseArray(response.getJSONArray("data_list").toString(),Pingjia.class);
                            if(list.size()==0){
                                kong_searchResultLL.setVisibility(View.VISIBLE);//展示搜素不到评价
                                searchResultRV.setVisibility(View.GONE);
                            }else {
                                if(kong_searchResultLL.getVisibility()==View.VISIBLE)
                                    kong_searchResultLL.setVisibility(View.GONE);
                                if(searchResultRV.getVisibility()==View.GONE)
                                    searchResultRV.setVisibility(View.VISIBLE);

                                if(pingjiaList.size()!=0)
                                    pingjiaList.clear();
                                pingjiaList.addAll(list);

                                JSONObject pageJSON=response.getJSONObject("page");
                                p = pageJSON.getInt("p");
                                total_page = pageJSON.getInt("total_page");
                                pingjiaRVadapter = new PingjiaRVadapter(SearchPingjiaActivity.this, pingjiaList,"搜索评价"){
                                    @Override
                                    public void loadAgain() {
                                        super.loadAgain();
                                        getMorePingjiaData();
                                    }
                                };
                                searchResultRV.setAdapter(pingjiaRVadapter);
                                searchResultRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        //滑到最后一条再向下滑动时重新获取数据，加载完毕后就不进行加载
                                        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == pingjiaList.size() && p < total_page) {
                                            if(lock){
                                                lock=false;//锁住
                                                getMorePingjiaData();
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
                params.put("search",queryPingjia);
                getNetData.getData(params, CommonValue.getEvaluateList);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //获取更多评价的数据
    private void getMorePingjiaData(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<Pingjia> list= JSON.parseArray(response.getJSONArray("data_list").toString(),Pingjia.class);
                    pingjiaList.addAll(list);
                    pingjiaRVadapter.notifyDataSetChanged();

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
                if(pingjiaRVadapter!=null){
                    pingjiaRVadapter.loadError = true;
                    pingjiaRVadapter.notifyItemChanged(pingjiaRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    lock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("search",queryPingjia);
        params.put("p",p+1);
        getNetData.getData(params, CommonValue.getEvaluateList);
    }

    //返回
    public void back(View view){
        finish();
    }
}
