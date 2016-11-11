package com.jionglemo.jionglemo_b.HomePage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FensiActivity extends AppCompatActivity {

    private RecyclerView fensiRV;
    public static int p;//当前页数
    public static int total_page;//总页数
    private FensiRVadapter fensiRVadapter;
    private int lastPosition;
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private List<Fensi> fensiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fensi);
        final LinearLayout kong_fensiLL= (LinearLayout) findViewById(R.id.kong_fensiLL);
        fensiRV = (RecyclerView) findViewById(R.id.fensiRV);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        fensiRV.setLayoutManager(linearLayoutManager);

        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    fensiList = JSON.parseArray(response.getString("data_list"),Fensi.class);
                    if(fensiList.size()==0){
                        kong_fensiLL.setVisibility(View.VISIBLE);
                        fensiRV.setVisibility(View.GONE);
                    }else {
                        JSONObject pageJSON=response.getJSONObject("page");
                        p = pageJSON.getInt("p");
                        total_page = pageJSON.getInt("total_page");
                        fensiRVadapter=new FensiRVadapter(FensiActivity.this,fensiList){
                            @Override
                            public void loadAgain() {
                                super.loadAgain();
                                getMoreFensiData();
                            }
                        };
                        fensiRV.setAdapter(fensiRVadapter);
                        fensiRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                //滑到最后一条再向下滑动时重新获取数据，加载完毕后就不进行加载
                                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == fensiList.size() && p<total_page) {
                                    if(lock){
                                        lock=false;//锁住
                                        getMoreFensiData();
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
        getNetData.getData(null, CommonValue.fensiList);
    }

    //获取更多粉丝的数据
    private void getMoreFensiData(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<Fensi> list= JSON.parseArray(response.getString("data_list"),Fensi.class);
                    fensiList.addAll(list);
                    fensiRVadapter.notifyDataSetChanged();

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
                if(fensiRVadapter!=null){
                    fensiRVadapter.loadError = true;
                    fensiRVadapter.notifyItemChanged(fensiRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    lock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("p",p+1);
        getNetData.getData(params,CommonValue.fensiList);
    }


    //返回
    public void back(View view){
        finish();
    }
}
