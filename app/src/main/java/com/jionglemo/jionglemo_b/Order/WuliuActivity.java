package com.jionglemo.jionglemo_b.Order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.ProductManager.ProductShowActivity;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WuliuActivity extends AppCompatActivity {

    private RecyclerView wuliuRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wuliu);

        final Order order=  (Order) getIntent().getSerializableExtra("order");
        ImageLoader imageLoader= ImageLoaderArgument.getInstance(this);//全局初始化此配置
        DisplayImageOptions options= ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
        TextView shopNameTV= (TextView) findViewById(R.id.shopNameTV);
        TextView orderStateTV= (TextView) findViewById(R.id.orderStateTV);
        ImageView goodIV= (ImageView) findViewById(R.id.goodIV);
        TextView goodNameTV= (TextView) findViewById(R.id.goodNameTV);
        TextView moneyTV= (TextView) findViewById(R.id.moneyTV);
        TextView sumNumTV= (TextView) findViewById(R.id.sumNumTV);
        TextView sumMoneyTV= (TextView) findViewById(R.id.sumMoneyTV);
        TextView peisongWayTV= (TextView) findViewById(R.id.peisongWayTV);

        shopNameTV.setText(order.getStore_name());
        switch (order.getOrder_status()){
            case 3:
                orderStateTV.setText("已发货");
                break;
            case 4:
                orderStateTV.setText("等待买家评价");
                break;
            case 5:
                orderStateTV.setText("交易完成");
                break;
        }
        imageLoader.displayImage(CommonValue.serverBasePath+order.getThumb(),goodIV,options);
        goodNameTV.setText(order.getProduct_name()+"("+order.getNorms_name()+")");
        moneyTV.setText(order.getUnit_price());
        sumNumTV.setText(order.getQuantity()+"");
        sumMoneyTV.setText(order.getTotal_price());
        if(order.getPostage_type()==0)
            peisongWayTV.setText("邮费：¥ "+order.getPostage());
        else
            peisongWayTV.setText("配送方式：包邮");

        final TextView wuliuCompanyNameTV= (TextView) findViewById(R.id.wuliuCompanyNameTV);
        TextView logisticsSnTV= (TextView) findViewById(R.id.logisticsSnTV);
        logisticsSnTV.setText("快递编号："+order.getLogistics_sn());

        wuliuRV = (RecyclerView) findViewById(R.id.wuliuRV);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        wuliuRV.setLayoutManager(linearLayoutManager);

        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    wuliuCompanyNameTV.setText("物流公司："+response.getString("com"));
                    List<WuliuMessage> wuliuMessages = JSON.parseArray(response.getString("data"), WuliuMessage.class);
                    if(wuliuMessages!=null && wuliuMessages.size()>0){
                        WuliuRVadapter adapter=new WuliuRVadapter(WuliuActivity.this,wuliuMessages);
                        wuliuRV.setAdapter(adapter);
                    }else {
                        wuliuCompanyNameTV.setText("系统繁忙，请稍候再试！");
                        wuliuCompanyNameTV.setTextColor(Color.rgb(255,0,153));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    wuliuCompanyNameTV.setText("系统繁忙，请稍候再试！");
                    wuliuCompanyNameTV.setTextColor(Color.rgb(255,0,153));
                }
            }

            @Override
            public void getDataFailure(String responseString) {
                super.getDataFailure(responseString);
                wuliuCompanyNameTV.setText("系统繁忙，请稍候再试！");
                wuliuCompanyNameTV.setTextColor(Color.rgb(255,0,153));
            }
        };
        RequestParams params=new RequestParams();
        params.put("order_num",order.getLogistics_sn());
        getNetData.getData(params,CommonValue.queryExpress);

        LinearLayout goodLL= (LinearLayout) findViewById(R.id.goodLL);
        goodLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WuliuActivity.this,ProductShowActivity.class);
                intent.putExtra("product_id",order.getProduct_id());
                startActivity(intent);
            }
        });
    }

    public void back(View view){
        finish();
    }
}
