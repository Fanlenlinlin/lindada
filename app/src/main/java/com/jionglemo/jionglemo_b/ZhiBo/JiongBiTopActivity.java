package com.jionglemo.jionglemo_b.ZhiBo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class JiongBiTopActivity extends AppCompatActivity {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiong_bi_top);

        final CircleImageView photoIV= (CircleImageView) findViewById(R.id.photoIV);
        final TextView nameTV= (TextView) findViewById(R.id.nameTV);
        final TextView jiongbiTV= (TextView) findViewById(R.id.jiongbiTV);

        imageLoader = ImageLoaderArgument.getInstance(this);//全局初始化此配置
        options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);

        final RecyclerView jiongbiTopRV= (RecyclerView) findViewById(R.id.jiongbiTopRV);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        jiongbiTopRV.setLayoutManager(linearLayoutManager);
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<JiongBiTop> jiongBiTopList= JSON.parseArray(response.getJSONArray("data_list").toString(),JiongBiTop.class);
                    if(jiongBiTopList.size()>0){
                        imageLoader.displayImage(CommonValue.serverBasePath+jiongBiTopList.get(0).getPortrait(),photoIV,options);
                        nameTV.setText(jiongBiTopList.get(0).getUser_name());
                        jiongbiTV.setText(jiongBiTopList.get(0).getPrice());
                        jiongBiTopList.remove(0);//去掉第一名
                        JiongBiTopRVadapter jiongBiTopRVadapter=new JiongBiTopRVadapter(JiongBiTopActivity.this,jiongBiTopList);
                        jiongbiTopRV.setAdapter(jiongBiTopRVadapter);
                    }else {
                        TextView textTV= (TextView) findViewById(R.id.textTV);
                        textTV.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("sales_id", CommonValue.getSalesId(this));
        getNetData.getData(params,CommonValue.storeGiftRankingList);
    }

    public void back(View view){
        finish();
    }
}
