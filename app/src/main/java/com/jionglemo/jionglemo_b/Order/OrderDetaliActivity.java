package com.jionglemo.jionglemo_b.Order;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.Date_transform;
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


/**
 * @author by 韩凤林 on 2016/11/7.
 * @类说明：查询订单详情
 */

public class OrderDetaliActivity extends Activity {
    private long secondLimit = 15 * 24 * 3600;//限定15天
    private int order_id;
    private int position;
    private int product_id;
    private String tag;
    private LinearLayout topLL;
    private TextView stateTV,leftTimeTV; //等待卖家发货
    private ImageView stateIv;
    private TextView order_snTV; //订单编号
    private TextView create_timeTV; //创建时间
    private LinearLayout payment_timeLL;
    private TextView payment_timeTV; //付款时间
    private LinearLayout buyer_LL;
    private TextView buyerTV; //买家留言
    private LinearLayout shopLL;
    private TextView shopNameTV; //店名
    private LinearLayout goodLL;
    private ImageView goodIV;
    private TextView already_downTV;
    private TextView goodNameTV;
    private TextView renMingBiTV;
    private TextView moneyTV;
    private TextView sumMoneyTV; //商品总价
    private TextView postageTV; //运费
    private TextView cutMoneyTV;//优惠
    private TextView needMoneyTV; //需实付
    private TextView nameTV,telTV,addressTV;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private LinearLayout shipments_timeLL;//发货时间layout
    private TextView shipments_timeTV;//发货时间
    private LinearLayout wuliuLL;//物流layout
    private TextView wuliuContentTV;//物流内容
    private TextView wuliuTimeTV;//物流时间
    private LinearLayout complete_timeLL;//成交时间layout
    private TextView complete_timeTV;//成交时间显示
    private LinearLayout cancel_timeLL;//关闭订单时间layout
    private TextView cancel_timeTV;//关闭订单时间
    private TextView fenggexianTV;//分割线



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);

        order_id = getIntent().getIntExtra("order_id", 0);
        position=getIntent().getIntExtra("position",0);
        tag = getIntent().getStringExtra("tag");

        initUi();
        //全局初始化此配置
        imageLoader = ImageLoaderArgument.getInstance(this);
        options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
        //订单详情
        setOrderDetailDatas();
        //点击进去商品详情页
        goodLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetaliActivity.this, ProductShowActivity.class);
                intent.putExtra("product_id", product_id);
                Log.e("TAG","product_id2---"+String.valueOf(product_id));
                startActivity(intent);
            }
        });

    }


    public void initUi(){
        topLL = (LinearLayout) findViewById(R.id.topLL);
        stateTV = (TextView) findViewById(R.id.stateTV);
        leftTimeTV = (TextView) findViewById(R.id.leftTimeTV);
        nameTV = (TextView) findViewById(R.id.nameTV);
        telTV = (TextView) findViewById(R.id.telTV);
        addressTV = (TextView) findViewById(R.id.addressTV);
        order_snTV = (TextView) findViewById(R.id.order_snTV);
        create_timeTV = (TextView) findViewById(R.id.create_timeTV);
        payment_timeTV = (TextView) findViewById(R.id.payment_timeTV);
        buyerTV = (TextView) findViewById(R.id.buyerTV);
        shopNameTV = (TextView) findViewById(R.id.shopNameTV);
        goodNameTV = (TextView) findViewById(R.id.goodNameTV);
        already_downTV = (TextView) findViewById(R.id.already_downTV);
        renMingBiTV = (TextView) findViewById(R.id.renMingBiTV);
        moneyTV = (TextView) findViewById(R.id.moneyTV);
        sumMoneyTV = (TextView) findViewById(R.id.sumMoneyTV);
        postageTV = (TextView) findViewById(R.id.postageTV);
        cutMoneyTV = (TextView) findViewById(R.id.cutMoneyTV);
        needMoneyTV = (TextView) findViewById(R.id.needMoneyTV);
        stateIv = (ImageView) findViewById(R.id.stateIv);
        goodIV = (ImageView) findViewById(R.id.goodIV);
        payment_timeLL = (LinearLayout) findViewById(R.id.payment_timeLL);
        buyer_LL = (LinearLayout) findViewById(R.id.buyer_LL);
        shopLL = (LinearLayout) findViewById(R.id.shopLL);
        goodLL = (LinearLayout) findViewById(R.id.goodLL);
        shipments_timeLL = (LinearLayout) findViewById(R.id.shipments_timeLL);
        wuliuLL = (LinearLayout) findViewById(R.id.wuliuLL);
        complete_timeLL = (LinearLayout) findViewById(R.id.complete_timeLL);
        shipments_timeTV = (TextView) findViewById(R.id.shipments_timeTV);
        wuliuContentTV = (TextView) findViewById(R.id.wuliuContentTV);
        wuliuTimeTV = (TextView) findViewById(R.id.wuliuTimeTV);
        complete_timeTV = (TextView) findViewById(R.id.complete_timeTV);
        cancel_timeLL = (LinearLayout) findViewById(R.id.cancel_timeLL);
        cancel_timeTV = (TextView) findViewById(R.id.cancel_timeTV);
        fenggexianTV = (TextView) findViewById(R.id.fenggexianTV);




    }

    //复制订单号
    public void copyOrder_Sn(View view) {
        ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(order_snTV.getText().toString()); // 复制
        Toast.makeText(this, "订单编号已复制", Toast.LENGTH_SHORT).show();
    }

    //返回
    public void back(View view) {
        finish();
    }

    /**订单详情*/
    public void setOrderDetailDatas(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                Log.e("TAG","response"+response.toString());
                switch (tag) {
                    case "未付款":
                        setUnpaidData(response);
                        break;

                    case "待发货":
                        setUnDeliverGoods(response);
                        break;

                    case "已发货":
                        setAlreadyShippedData(response);
                        break;

                    case "已完成":
                        setCompleteDatas(response);
                        break;

                    case "已关闭":
                        setCloseDatas(response);
                        break;
                }
                setOrderDatas(response);
            }
        };
        RequestParams params=new RequestParams();
        params.put("order_id",order_id);
        getNetData.getData(params, CommonValue.orderDetail);
        Log.e("TAG",CommonValue.orderDetail+params.toString());
    }

    /**未付款*/
    private void setUnpaidData(JSONObject response){
        try {
            long create_time = response.getLong("create_time");
            long now_time = response.getLong("now_time");
            long secondLeft = secondLimit - (now_time - create_time);
            long dayLeft = secondLeft / 3600 / 24;
            long hourLeft = secondLeft / 3600 % 24;
            leftTimeTV.setText("还剩" + dayLeft + "天" + hourLeft + "小时自动关闭");
            payment_timeLL.setVisibility(View.GONE);
            leftTimeTV.setVisibility(View.VISIBLE);
            stateIv.setImageResource(R.drawable.qiandai);
            stateTV.setText("等待买家付款");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**待发货*/
    private void setUnDeliverGoods(JSONObject response){
        try {
            payment_timeLL.setVisibility(View.VISIBLE);
            payment_timeTV.setText(Date_transform.getTimesOne(response.getString("payment_time")));
            leftTimeTV.setVisibility(View.GONE);
            stateIv.setImageResource(R.drawable.box);
            stateTV.setText("等待卖家发货");
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**已发货*/
    private void setAlreadyShippedData(JSONObject response){
        try {
            String shipments_time=response.getString("shipments_time");
            if ("0".equals(shipments_time)){
                shipments_timeTV.setText("正在发货中");
            }
            long shipments_time0 = response.getLong("shipments_time");
            long now_time0 = response.getLong("now_time");
            long secondLeft0 = secondLimit - (now_time0 - shipments_time0);
            long dayLeft0 = secondLeft0 / 3600 / 24;
            long hourLeft0 = secondLeft0 / 3600 % 24;
            leftTimeTV.setText("还剩" + dayLeft0 + "天" + hourLeft0 + "小时自动确认");
            shipments_timeLL.setVisibility(View.VISIBLE);
            shipments_timeTV.setText(Date_transform.getTimesOne(response.getString("shipments_time")));
            leftTimeTV.setVisibility(View.VISIBLE);
            stateTV.setText("已发货");
            stateIv.setImageResource(R.drawable.pho_vehicle);
            getWuliuMessage(response.getString("logistics_sn"));
        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    /**交易成功已完成*/
    private void setCompleteDatas(JSONObject response){
        try{
            leftTimeTV.setVisibility(View.GONE);
            stateTV.setText("交易成功");
            stateIv.setImageResource(R.drawable.pho_gift);
            payment_timeLL.setVisibility(View.VISIBLE);
            payment_timeTV.setText(Date_transform.getTimesOne(response.getString("payment_time")));
            shipments_timeLL.setVisibility(View.VISIBLE);
            shipments_timeTV.setText(Date_transform.getTimesOne(response.getString("shipments_time")));
            complete_timeLL.setVisibility(View.VISIBLE);
            complete_timeTV.setText(Date_transform.getTimesOne(response.getString("complete_time")));
            getWuliuMessage(response.getString("logistics_sn"));
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**已关闭*/
    private void setCloseDatas(JSONObject response){
        try {
            leftTimeTV.setVisibility(View.GONE);
            stateTV.setText("交易已关闭");
            stateIv.setImageResource(R.drawable.pho_close);
            cancel_timeLL.setVisibility(View.VISIBLE);
            cancel_timeTV.setText((Date_transform.getTimesOne(response.getString("cancel_time"))));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**订单详情的*/
    private void setOrderDatas(JSONObject response){
        try {
            product_id = response.getInt("product_id");
            Log.e("TAG","product_id1---"+String.valueOf(product_id));
            order_snTV.setText(response.getString("order_sn"));
            create_timeTV.setText(Date_transform.getTimesOne(response.getString("create_time")));
            nameTV.setText(response.getString("name"));
            telTV.setText("联系电话：" + response.getString("tel"));
            addressTV.setText(response.getString("province_name") + response.getString("city_name") + response.getString("district_name") + response.getString("detail_address"));
            shopNameTV.setText(response.getString("store_name"));
            if (response.getString("seller_message").equals("")){
                buyerTV.setText("无");
            }else {
                buyerTV.setText(response.getString("seller_message"));
            }
            goodNameTV.setText(response.getString("product_name")+"("+response.getString("norms_name")+")");
            moneyTV.setText(response.getString("unit_price"));
            imageLoader.displayImage(CommonValue.serverBasePath+response.getString("thumb"),goodIV,options);

            float sumOriginalMoney=Float.valueOf(response.getString("original_price"))*response.getInt("quantity");
            //float sumPresentMoney=Float.valueOf(response.getString("present_price"))*response.getInt("quantity");
            float postage=Float.valueOf(response.getString("postage"));
            float total_price=Float.valueOf(response.getString("total_price"));
            sumMoneyTV.setText(String.valueOf(sumOriginalMoney));
            postageTV.setText(String.valueOf(postage));
            cutMoneyTV.setText(String.valueOf(sumOriginalMoney-total_price));
            needMoneyTV.setText(String.valueOf(total_price+postage));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**获取物流信息*/
    private void getWuliuMessage(String logistics_sn){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                Log.e("TAG","物流接口response--"+response.toString());
                try {
                    List<WuliuMessage> wuliuMessages = JSON.parseArray(response.getString("data"), WuliuMessage.class);
                    if(wuliuMessages!=null && wuliuMessages.size()>0){
                        wuliuLL.setVisibility(View.VISIBLE);
                        fenggexianTV.setVisibility(View.VISIBLE);
                        wuliuContentTV.setText(wuliuMessages.get(0).getContext());
                        wuliuTimeTV.setText(wuliuMessages.get(0).getTime());
                        wuliuLL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Order order = (Order) getIntent().getSerializableExtra("order");
                                checkWuliu(order);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("order_num",logistics_sn);
        getNetData.getData(params, CommonValue.queryExpress);
        Log.e("TAG","物流接口--"+CommonValue.queryExpress+params.toString());
    }
    /**查看物流*/
    private void checkWuliu(Order order){
        Intent intent=new Intent(this,WuliuActivity.class);
        intent.putExtra("order",order);
        intent.putExtra("position",position);
        startActivityForResult(intent,1);
    }
























}
