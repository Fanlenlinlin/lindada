package com.jionglemo.jionglemo_b.HomePage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;

public class BusinessServicesActivity extends AppCompatActivity {

    private TextView phoneNumTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_services);
        phoneNumTV = (TextView) findViewById(R.id.phoneNumTV);
    }

    //客户服务电话
    public void serverPhone(View view){
        Intent intent8 = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNumTV.getText().toString());
        intent8.setData(data);
        startActivity(intent8);
    }

    //客服
    public void service(View view){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    RongIM.getInstance().startPrivateChat(BusinessServicesActivity.this, response.getString("jid"),"囧了么客服");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        getNetData.getData(null, CommonValue.serviceJid);
    }

    //投诉建议
    public void suggest(View view){
        Intent intent=new Intent(this,SuggestActivity.class);
        startActivity(intent);
    }

    //返回
    public void back(View view){
        finish();
    }
}
