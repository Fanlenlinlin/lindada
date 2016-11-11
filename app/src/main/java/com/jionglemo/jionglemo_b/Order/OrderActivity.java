package com.jionglemo.jionglemo_b.Order;

import android.content.Intent;
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

public class OrderActivity extends AppCompatActivity {

    private int order_status;
    private List<Order> orderList;
    public static int p;//当前页数
    public static int total_page;//总页数
    private RecyclerView orderRV;
    private OrderRVadapter orderRVadapter;
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private LinearLayoutManager linearLayoutManager;
    private int lastPosition;
    private LinearLayout noneOrderLL;
    private TextView noneOrderTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        order_status = getIntent().getIntExtra("order_status",0);//订单状态：1待付款；2待发货；3待收货；4待评价；5订单完成；6取消订单
        TextView stateTV= (TextView) findViewById(R.id.stateTV);
        noneOrderLL = (LinearLayout) findViewById(R.id.noneOrderLL);
        noneOrderTV = (TextView) findViewById(R.id.noneOrderTV);
        switch (order_status){
            case 1:
                stateTV.setText("未付款");
                noneOrderTV.setText("暂无未付款订单");
                break;
            case 2:
                stateTV.setText("待发货");
                noneOrderTV.setText("暂无待发货订单");
                break;
            case 3:
                stateTV.setText("已发货");
                noneOrderTV.setText("暂无已发货订单");
                break;
            case 5:
                stateTV.setText("已完成");
                noneOrderTV.setText("暂无已完成订单");
                break;
            case 8:
                stateTV.setText("全部订单");
                noneOrderTV.setText("暂无订单");
                break;
            case 6:
                stateTV.setText("已关闭");
                noneOrderTV.setText("暂无已关闭订单");
                break;
        }
        orderRV = (RecyclerView) findViewById(R.id.orderRV);
        linearLayoutManager = new LinearLayoutManager(this);
        orderRV.setLayoutManager(linearLayoutManager);

        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    orderList = JSON.parseArray(response.getJSONArray("data_list").toString(),Order.class);
                    if(orderList.size()==0){
                        orderRV.setVisibility(View.GONE);
                        noneOrderLL.setVisibility(View.VISIBLE);
                    }else {
                        JSONObject pageJSON=response.getJSONObject("page");
                        p = pageJSON.getInt("p");
                        total_page = pageJSON.getInt("total_page");
                        orderRVadapter = new OrderRVadapter(OrderActivity.this, orderList,"订单"){
                            @Override
                            public void loadAgain() {
                                super.loadAgain();
                                getMoreOrderData();
                            }
                        };
                        orderRV.setAdapter(orderRVadapter);
                        orderRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                //滑到最后一条再向下滑动时重新获取数据，加载完毕后就不进行加载
                                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == orderList.size() && p<total_page) {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        if(order_status !=8)
            params.put("order_status", order_status);
        getNetData.getData(params, CommonValue.orderList);
    }

    //获取更多订单的数据
    private void getMoreOrderData(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<Order> list= JSON.parseArray(response.getJSONArray("data_list").toString(),Order.class);
                    orderList.addAll(list);
                    orderRVadapter.notifyDataSetChanged();

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
                if(orderRVadapter!=null){
                    orderRVadapter.loadError = true;
                    orderRVadapter.notifyItemChanged(orderRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    lock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("p",p+1);
        if(order_status!=8)
            params.put("order_status",order_status);
        getNetData.getData(params, CommonValue.orderList);
    }

    //返回
    public void back(View view){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data!=null){
            int position=data.getIntExtra("position",0);
            orderRVadapter.notifyItemRemoved(position);//刷新列表，使用该方法可避免闪烁
            orderList.remove(position);//从绑定的列表中删除
            orderRVadapter.notifyItemRangeChanged(position, orderRVadapter.getItemCount());//通知position之后的数据刷新
        }
    }
}
