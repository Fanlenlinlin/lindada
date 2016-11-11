package com.jionglemo.jionglemo_b.ProductManager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.PictureTool;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.CommonView.DeleteDialogFragment;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllManagerActivity extends AppCompatActivity {

    public static int p;//当前页数
    public static int total_page;//总页数

    private List<ProductManager> productManagerList;
    private AllManagerRVadapter allManagerRVadapter;
    private RecyclerView allManagerRV;
    private LinearLayoutManager linearLayoutManager;
    private int lastPosition;
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private TextView noneProductTV;
    private LinearLayout noneProductLL;
    private List<Integer> positionCheckList;//用于记录复选框的状态，0代表未选中，1代表选中

    private int status;
    private int rank;
    private String product_name;//搜索商品名称
    private LinearLayout statusLL;
    private TextView allCheckTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_manager);

        status = getIntent().getIntExtra("status",0); //默认上架
        rank = getIntent().getIntExtra("rank",4);//默认添加时间升序
        product_name=getIntent().getStringExtra("product_name");

        allManagerRV = (RecyclerView)findViewById(R.id.allManagerRV);
        linearLayoutManager = new LinearLayoutManager(this);
        allManagerRV.setLayoutManager(linearLayoutManager);
        noneProductTV = (TextView) findViewById(R.id.noneProductTV);
        noneProductLL = (LinearLayout)findViewById(R.id.noneProductLL);

        statusLL = (LinearLayout) findViewById(R.id.statusLL);
        ImageView statusIV= (ImageView) findViewById(R.id.statusIV);
        TextView statusTV= (TextView) findViewById(R.id.statusTV);
        allCheckTV = (TextView) findViewById(R.id.allCheckTV);
        if(status==1){//对已下架的进行处理
            statusIV.setImageBitmap(PictureTool.readBitMap(this,R.drawable.product_manager_up));
            statusTV.setText("上架");
        }

        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    productManagerList = JSON.parseArray(response.getJSONArray("data_list").toString(),ProductManager.class);
                    if(productManagerList.size()==0){
                        allManagerRV.setVisibility(View.GONE);
                        noneProductLL.setVisibility(View.VISIBLE);
                        if(status ==0)
                            noneProductTV.setText("暂无，请添加商品");
                        else if(status ==1)
                            noneProductTV.setText("暂无已下架的商品");
                    }else {
                        allManagerRV.setVisibility(View.VISIBLE);
                        noneProductLL.setVisibility(View.GONE);

                        positionCheckList=new ArrayList<>();
                        for(int i = 0; i< productManagerList.size(); i++){
                            positionCheckList.add(0);//初始化为0，代表未选中
                        }

                        JSONObject pageJSON=response.getJSONObject("page");
                        p = pageJSON.getInt("p");
                        total_page = pageJSON.getInt("total_page");
                        allManagerRVadapter = new AllManagerRVadapter(AllManagerActivity.this,productManagerList,positionCheckList,status){
                            @Override
                            public void loadAgain() {
                                super.loadAgain();
                                getMoreProductData();
                            }
                        };
                        allManagerRV.setAdapter(allManagerRVadapter);
                        allManagerRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                        //上下架处理
                        statusLL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(status==0){
                                    showUpAndDown("商品下架","您确定要将商品下架吗？","商品已下架",1);
                                }else {
                                    showUpAndDown("商品上架","您确定要将商品上架吗？","商品已上架",0);
                                }
                            }
                        });

                        //全选逻辑
                        allCheckTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if("全选".equals(allCheckTV.getText().toString())){
                                    allCheckTV.setText("取消全选");
                                    for(int i=0;i<positionCheckList.size();i++)
                                        positionCheckList.set(i,1);
                                }else {
                                    allCheckTV.setText("全选");
                                    for(int i=0;i<positionCheckList.size();i++)
                                        positionCheckList.set(i,0);
                                }
                                allManagerRVadapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("status", status);
        params.put("rank", rank);
        params.put("product_name",product_name);
        getNetData.getData(params, CommonValue.productList);
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
                    for(int i = 0; i< list.size(); i++){
                        positionCheckList.add(0);//初始化为0，代表未选中
                    }
                    allManagerRVadapter.notifyDataSetChanged();

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
                if(allManagerRVadapter !=null){
                    allManagerRVadapter.loadError = true;
                    allManagerRVadapter.notifyItemChanged(allManagerRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    lock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("p",p+1);
        params.put("status",status);
        params.put("rank",rank);
        params.put("product_name",product_name);
        getNetData.getData(params, CommonValue.productList);
    }

    //上下架处理
    private void showUpAndDown(String dialogTitle, String dialogMessage, final String toastMessage, final int toStatus){
        DeleteDialogFragment deleteDialogFragment=DeleteDialogFragment.newInstance(dialogTitle,dialogMessage,"确定");
        deleteDialogFragment.setOnDeleteListener(new DeleteDialogFragment.DeleteListener() {
            @Override
            public void onDelete() {
                //获取选中的商品id
                String product_id="";
                for(int i=0;i<positionCheckList.size();i++){
                    if(positionCheckList.get(i)==1)
                        product_id=product_id+productManagerList.get(i).getProduct_id()+",";
                }
                if("".equals(product_id))
                    Toast.makeText(AllManagerActivity.this,"请选择商品",Toast.LENGTH_SHORT).show();
                else {
                    product_id=product_id.substring(0,product_id.length()-1);//去掉最后一个“,”
                    GetNetData getNetData=new GetNetData(AllManagerActivity.this){
                        @Override
                        public void getDataSuccess(JSONObject response) {
                            super.getDataSuccess(response);
                            //为了不使列表错位，倒着删
                            for(int i=positionCheckList.size()-1;i>=0;i--){
                                if(positionCheckList.get(i)==1){
                                    positionCheckList.remove(i);
                                    productManagerList.remove(i);
                                }
                            }
                            allManagerRVadapter.notifyDataSetChanged();
                            Toast.makeText(AllManagerActivity.this,toastMessage,Toast.LENGTH_SHORT).show();
                            allCheckTV.setText("全选");
                            if(productManagerList.size()==0) {
                                if(p<total_page)//说明还有数据，必须继续加载
                                    getMoreProductData();
                                else { //如果没有数据了，展示空页面
                                    allManagerRV.setVisibility(View.GONE);
                                    noneProductLL.setVisibility(View.VISIBLE);
                                    if(status ==0)
                                        noneProductTV.setText("暂无，请添加商品");
                                    else if(status ==1)
                                        noneProductTV.setText("暂无已下架的商品");
                                }
                            }
                        }
                    };
                    RequestParams params=new RequestParams();
                    params.put("status",toStatus);
                    params.put("product_id",product_id);
                    getNetData.getData(params,CommonValue.updateProduct);
                }
            }
        });
        deleteDialogFragment.show(getSupportFragmentManager(),"deleteDialog");
    }


    //返回
    public void back(View view){
        finish();
    }
}
