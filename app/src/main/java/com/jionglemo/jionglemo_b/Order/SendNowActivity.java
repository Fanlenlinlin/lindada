package com.jionglemo.jionglemo_b.Order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.ProductManager.ProductShowActivity;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendNowActivity extends AppCompatActivity {

    private TextView sendNowTV;
    private EditText wuliuET;
    private ScrollView sendSV;
    private int position;
    private Pattern pattern = Pattern.compile("^[A-Za-z0-9-]+$");//正则表达式，匹配大小写字母、数字、符号“-”

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_now);
        position = getIntent().getIntExtra("position",0);
        final Order order=  (Order) getIntent().getSerializableExtra("order");
        ImageLoader imageLoader= ImageLoaderArgument.getInstance(this);//全局初始化此配置
        DisplayImageOptions options= ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
        TextView shopNameTV= (TextView) findViewById(R.id.shopNameTV);
        ImageView goodIV= (ImageView) findViewById(R.id.goodIV);
        TextView goodNameTV= (TextView) findViewById(R.id.goodNameTV);
        TextView moneyTV= (TextView) findViewById(R.id.moneyTV);
        TextView sumNumTV= (TextView) findViewById(R.id.sumNumTV);
        TextView sumMoneyTV= (TextView) findViewById(R.id.sumMoneyTV);
        TextView peisongWayTV= (TextView) findViewById(R.id.peisongWayTV);
        TextView receiveNameTV= (TextView) findViewById(R.id.receiveNameTV);
        TextView phoneTV= (TextView) findViewById(R.id.phoneTV);
        TextView addressTV= (TextView) findViewById(R.id.addressTV);
        wuliuET = (EditText) findViewById(R.id.wuliuET);
        sendNowTV = (TextView) findViewById(R.id.sendNowTV);
        sendSV = (ScrollView) findViewById(R.id.sendSV);

        shopNameTV.setText(order.getStore_name());
        imageLoader.displayImage(CommonValue.serverBasePath+order.getThumb(),goodIV,options);
        goodNameTV.setText(order.getProduct_name()+"("+order.getNorms_name()+")");
        moneyTV.setText(order.getUnit_price());
        sumNumTV.setText(order.getQuantity()+"");
        sumMoneyTV.setText(order.getTotal_price());
        if(order.getPostage_type()==0)
            peisongWayTV.setText("邮费：¥ "+order.getPostage());
        else
            peisongWayTV.setText("配送方式：包邮");
        receiveNameTV.setText("收货人："+order.getName());
        phoneTV.setText("联系电话："+order.getTel());
        addressTV.setText("收货地址："+order.getProvince_name()+order.getCity_name()+order.getDistrict_name()+order.getDetail_address());


        //当点击EditText时，键盘会弹出，此时需将ScrollView滚动到底，整个布局向上顶
        wuliuET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSV.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendSV.fullScroll(ScrollView.FOCUS_DOWN); //将ScrollView滚动到底
                    }
                },100);//此处必须使用延时，否则没效果
            }
        });

        wuliuET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String logistics_sn=s.toString().trim();

                Matcher m = pattern.matcher(logistics_sn);
                if(logistics_sn.length()<8||!m.matches()){//小于8位或含其它字符的
                    sendNowTV.setBackgroundResource(R.drawable.juxing_grey);
                }else {
                    sendNowTV.setBackgroundResource(R.drawable.juxing_ff0099);
                }
            }
        });

        sendNowTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logistics_sn=wuliuET.getText().toString().trim();
                Matcher m = pattern.matcher(logistics_sn);
                if(logistics_sn.length()<8||!m.matches()){//小于8位或含其它字符的
                    Toast.makeText(SendNowActivity.this,"物流单号不规范，请确认",Toast.LENGTH_SHORT).show();
                }else {
                    GetNetData getNetData=new GetNetData(SendNowActivity.this){
                        @Override
                        public void getDataSuccess(JSONObject response) {
                            super.getDataSuccess(response);
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            //隐藏键盘
                            imm.hideSoftInputFromWindow(wuliuET.getWindowToken(), 0);
                            Toast.makeText(SendNowActivity.this,"发货成功",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            intent.putExtra("position",position);
                            setResult(1,intent);
                            finish();
                        }
                    };
                    RequestParams params=new RequestParams();
                    params.put("order_id",order.getId());
                    params.put("logistics_sn",logistics_sn);
                    getNetData.getData(params,CommonValue.deliverGoods);
                }
            }
        });

        LinearLayout goodLL= (LinearLayout) findViewById(R.id.goodLL);
        goodLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SendNowActivity.this,ProductShowActivity.class);
                intent.putExtra("product_id",order.getProduct_id());
                startActivity(intent);
            }
        });
    }

    //返回
    public void back(View view){
        finish();
    }
}
