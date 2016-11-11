package com.jionglemo.jionglemo_b.ZhiBo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JiongBiRecordActivity extends AppCompatActivity {

    private RecyclerView jiongbiRecordRV;
    public static int p;//当前页数
    public static int total_page;//总页数
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private LinearLayoutManager linearLayoutManager;
    private int lastPosition;
    private List<JiongBiRecord> jiongBiRecordList;
    private JiongBiRecordRVadapter jiongBiRecordRVadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiong_bi_record);
        jiongbiRecordRV = (RecyclerView) findViewById(R.id.jiongbiRecordRV);
        linearLayoutManager=new LinearLayoutManager(this);
        jiongbiRecordRV.setLayoutManager(linearLayoutManager);
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    jiongBiRecordList = JSON.parseArray(response.getJSONArray("data_list").toString(),JiongBiRecord.class);
                    jiongBiRecordRVadapter = new JiongBiRecordRVadapter(JiongBiRecordActivity.this, jiongBiRecordList){
                        @Override
                        public void loadAgain() {
                            super.loadAgain();
                            getMoreOrderData();
                        }
                    };
                    jiongbiRecordRV.setAdapter(jiongBiRecordRVadapter);
                    jiongbiRecordRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            //滑到最后一条再向下滑动时重新获取数据，加载完毕后就不进行加载
                            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == jiongBiRecordList.size() && p<total_page) {
                                if(lock){
                                    lock=false;//锁住
                                    getMoreOrderData();
                                }
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("sales_id", CommonValue.getSalesId(this));
        getNetData.getData(params,CommonValue.storeGiftList);
    }


    //获取更多囧币记录的数据
    private void getMoreOrderData(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<JiongBiRecord> list= JSON.parseArray(response.getJSONArray("data_list").toString(),JiongBiRecord.class);
                    jiongBiRecordList.addAll(list);
                    jiongBiRecordRVadapter.notifyDataSetChanged();

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
                if(jiongBiRecordRVadapter!=null){
                    jiongBiRecordRVadapter.loadError = true;
                    jiongBiRecordRVadapter.notifyItemChanged(jiongBiRecordRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    lock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("p",p+1);
        params.put("sales_id", CommonValue.getSalesId(this));
        getNetData.getData(params, CommonValue.storeGiftList);
    }


    //进入囧币贡献排行榜
    public void gotoJiongBiTop(View view){
        Intent intent=new Intent(this,JiongBiTopActivity.class);
        startActivity(intent);
    }

    public void back(View view){
        finish();
    }
}
