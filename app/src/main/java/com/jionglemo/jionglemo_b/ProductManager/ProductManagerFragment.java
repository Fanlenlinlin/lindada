package com.jionglemo.jionglemo_b.ProductManager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.CommonView.OrderButton;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductManagerFragment extends Fragment implements View.OnClickListener{

    private View rootView;//作为内部变量，用于与ViewPager结合优化内存
    private OrderButton addDateOB;
    private OrderButton sellNumOB;
    private OrderButton kuCunOB;

    public static int p;//当前页数
    public static int total_page;//总页数

    private int status=0;//产品状态：0上架；1下架；
    private int rank=4;//排序方式：0：销量升序；1：销量降序；2：库存升序；3：库存降序；4：添加时间升序；5：添加时间降序
    private String product_name;//搜索商品名称

    private List<ProductManager> productManagerList=new ArrayList<>();
    private ProductManagerRVadapter productManagerRVadapter;
    private RecyclerView productManagerRV;
    private LinearLayoutManager linearLayoutManager;
    private int lastPosition;
    private boolean lock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住
    private TextView noneProductTV;
    private LinearLayout noneProductLL;
    private ImageView allManagerIV;
    private RadioGroup optionsRG;

    public ProductManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_product_manager, container, false);
            addDateOB = (OrderButton) rootView.findViewById(R.id.addDateOB);
            sellNumOB = (OrderButton) rootView.findViewById(R.id.sellNumOB);
            kuCunOB = (OrderButton) rootView.findViewById(R.id.kuCunOB);
            addDateOB.setOnClickListener(this);
            sellNumOB.setOnClickListener(this);
            kuCunOB.setOnClickListener(this);
            addDateOB.chooseUp();

            productManagerRV = (RecyclerView) rootView.findViewById(R.id.productManagerRV);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            productManagerRV.setLayoutManager(linearLayoutManager);

            noneProductTV = (TextView) rootView.findViewById(R.id.noneProductTV);
            noneProductLL = (LinearLayout) rootView.findViewById(R.id.noneProductLL);
            allManagerIV = (ImageView) rootView.findViewById(R.id.allManagerIV);

            final LinearLayout orderLL= (LinearLayout) rootView.findViewById(R.id.orderLL);
            optionsRG = (RadioGroup) rootView.findViewById(R.id.optionsRG);
            optionsRG.check(R.id.upRB);
            optionsRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                     switch (checkedId){
                         case R.id.upRB:
                             if(orderLL.getVisibility()==View.GONE)
                                 orderLL.setVisibility(View.VISIBLE);
                             if(allManagerIV.getVisibility()==View.GONE)
                                 allManagerIV.setVisibility(View.VISIBLE);
                             status=0;
                             getProductList();
                             break;
                         case R.id.downRB:
                             if(orderLL.getVisibility()==View.GONE)
                                 orderLL.setVisibility(View.VISIBLE);
                             if(allManagerIV.getVisibility()==View.GONE)
                                 allManagerIV.setVisibility(View.VISIBLE);
                             status=1;
                             getProductList();
                             break;
                         case R.id.classifyRB:
                             if(orderLL.getVisibility()==View.VISIBLE)
                                 orderLL.setVisibility(View.GONE);
                             if(allManagerIV.getVisibility()==View.VISIBLE)
                                 allManagerIV.setVisibility(View.GONE);
                             getProductClassify();
                             break;
                     }
                }
            });

            allManagerIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),AllManagerActivity.class);
                    intent.putExtra("status",status);
                    intent.putExtra("rank",rank);
                    intent.putExtra("product_name",product_name);
                    startActivity(intent);
                }
            });

            final EditText searchET= (EditText) rootView.findViewById(R.id.searchET);
            TextView searchTV= (TextView) rootView.findViewById(R.id.searchTV);
            searchTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    product_name=searchET.getText().toString().trim();
                    getProductList();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //隐藏键盘
                    imm.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(optionsRG.getCheckedRadioButtonId()!=R.id.classifyRB)
            getProductList();//放于此处，便于刷新
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addDateOB:
                order(addDateOB,sellNumOB,kuCunOB);
                break;
            case R.id.sellNumOB:
                order(sellNumOB,addDateOB,kuCunOB);
                break;
            case R.id.kuCunOB:
                order(kuCunOB,sellNumOB,addDateOB);
                break;
        }
    }

    //排序的逻辑
    private void order(OrderButton orderButton1,OrderButton orderButton2,OrderButton orderButton3){
        int orderButtonID=orderButton1.getId();
        if(orderButton1.getState()!=2){
            orderButton1.chooseDown();
            switch (orderButtonID){
                case R.id.addDateOB:
                    rank=5;//添加时间降序
                    getProductList();
                    break;
                case R.id.sellNumOB:
                    rank=1;//销量降序
                    getProductList();
                    break;
                case R.id.kuCunOB:
                    rank=3;//库存降序
                    getProductList();
                    break;
            }
        } else{
            orderButton1.chooseUp();
            switch (orderButtonID){
                case R.id.addDateOB:
                    rank=4;//添加时间升序
                    getProductList();
                    break;
                case R.id.sellNumOB:
                    rank=0;//销量升序
                    getProductList();
                    break;
                case R.id.kuCunOB:
                    rank=2;//库存升序
                    getProductList();
                    break;
            }
        }

        if(orderButton2.getState()!=0)
            orderButton2.clearOrder();
        if(orderButton3.getState()!=0)
            orderButton3.clearOrder();
    }

    //获取商品列表
    private void getProductList(){
        GetNetData getNetData=new GetNetData(getActivity()){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<ProductManager> list = JSON.parseArray(response.getJSONArray("data_list").toString(),ProductManager.class);
                    if(list.size()==0){
                        productManagerRV.setVisibility(View.GONE);
                        noneProductLL.setVisibility(View.VISIBLE);
                        if(status==0)
                           noneProductTV.setText("暂无，请添加商品");
                        else if(status==1)
                            noneProductTV.setText("暂无已下架的商品");
                    }else {
                        productManagerRV.setVisibility(View.VISIBLE);
                        noneProductLL.setVisibility(View.GONE);

                        if(productManagerList.size()!=0)
                            productManagerList.clear();
                        productManagerList.addAll(list);
                        JSONObject pageJSON=response.getJSONObject("page");
                        p = pageJSON.getInt("p");
                        total_page = pageJSON.getInt("total_page");
                        productManagerRVadapter = new ProductManagerRVadapter(getActivity(),productManagerList,status){
                            @Override
                            public void loadAgain() {
                                super.loadAgain();
                                getMoreProductData();
                            }
                            @Override
                            public void showNone() {
                                super.showNone();
                                productManagerRV.setVisibility(View.GONE);
                                noneProductLL.setVisibility(View.VISIBLE);
                                if(status==0)
                                    noneProductTV.setText("暂无，请添加商品");
                                else if(status==1)
                                    noneProductTV.setText("暂无已下架的商品");
                            }
                        };
                        productManagerRV.setAdapter(productManagerRVadapter);
                        productManagerRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("status",status);
        params.put("rank",rank);
        params.put("product_name",product_name);
        getNetData.getData(params, CommonValue.productList);
    }

    //获取更多商品数据
    private void getMoreProductData(){
        GetNetData getNetData=new GetNetData(getActivity()){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<ProductManager> list= JSON.parseArray(response.getJSONArray("data_list").toString(),ProductManager.class);
                    productManagerList.addAll(list);
                    productManagerRVadapter.notifyDataSetChanged();

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
                if(productManagerRVadapter !=null){
                    productManagerRVadapter.loadError = true;
                    productManagerRVadapter.notifyItemChanged(productManagerRVadapter.getItemCount() - 1);//更新最后一条数据的状态
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

    //获取分类
    private void getProductClassify(){
        GetNetData getNetData=new GetNetData(getActivity()){
            @Override
            public void getDataSuccess(JSONArray response) {
                super.getDataSuccess(response);
                productManagerRV.setVisibility(View.VISIBLE);
                noneProductLL.setVisibility(View.GONE);
                List<ProductClassify> list=JSON.parseArray(response.toString(),ProductClassify.class);
                ProductClassifyRVadapter productClassifyRVadapter=new ProductClassifyRVadapter(getActivity(),list);
                productManagerRV.setAdapter(productClassifyRVadapter);
            }
        };
        getNetData.getData(null,CommonValue.getProductClassify);
    }
}
