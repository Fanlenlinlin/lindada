package com.jionglemo.jionglemo_b.Order;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    private View rootView;//作为内部变量，用于与ViewPager结合优化内存
    private TextView wait_for_payTV;
    private TextView wait_for_sendTV;
    private TextView already_sendTV;
    private TextView order_completeTV;
    private TextView all_orderTV;
    private TextView look_pingjiaTV;
    private TextView close_orderTV;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_order, container, false);
            //未付款
            LinearLayout wait_for_payLL= (LinearLayout) rootView.findViewById(R.id.wait_for_payLL);
            wait_for_payLL.setOnClickListener(new MyOnClickListener());
            //待发货
            LinearLayout wait_for_sendLL= (LinearLayout) rootView.findViewById(R.id.wait_for_sendLL);
            wait_for_sendLL.setOnClickListener(new MyOnClickListener());
            //已发货
            LinearLayout already_sendLL= (LinearLayout) rootView.findViewById(R.id.already_sendLL);
            already_sendLL.setOnClickListener(new MyOnClickListener());
            //已完成
            LinearLayout order_completeLL= (LinearLayout) rootView.findViewById(R.id.order_completeLL);
            order_completeLL.setOnClickListener(new MyOnClickListener());
            //全部订单
            LinearLayout all_orderLL= (LinearLayout) rootView.findViewById(R.id.all_orderLL);
            all_orderLL.setOnClickListener(new MyOnClickListener());
            //查看评价
            LinearLayout look_pingjiaLL= (LinearLayout) rootView.findViewById(R.id.look_pingjiaLL);
            look_pingjiaLL.setOnClickListener(new MyOnClickListener());
            //已关闭
            LinearLayout close_orderLL= (LinearLayout) rootView.findViewById(R.id.close_orderLL);
            close_orderLL.setOnClickListener(new MyOnClickListener());

            wait_for_payTV = (TextView) rootView.findViewById(R.id.wait_for_payTV);
            wait_for_sendTV = (TextView) rootView.findViewById(R.id.wait_for_sendTV);
            already_sendTV = (TextView) rootView.findViewById(R.id.already_sendTV);
            order_completeTV = (TextView) rootView.findViewById(R.id.order_completeTV);
            all_orderTV = (TextView) rootView.findViewById(R.id.all_orderTV);
            look_pingjiaTV = (TextView) rootView.findViewById(R.id.look_pingjiaTV);
            close_orderTV = (TextView) rootView.findViewById(R.id.close_orderTV);

            TextView searchTV= (TextView) rootView.findViewById(R.id.searchTV);
            searchTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),SearchOrderActivity.class);
                    startActivity(intent);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        GetNetData getNetData=new GetNetData(getActivity()){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    wait_for_payTV.setText("未付款("+response.getInt("arrearage")+")");
                    wait_for_sendTV.setText("待发货("+response.getInt("not_shipped")+")");
                    already_sendTV.setText("已发货("+response.getInt("already_shipped")+")");
                    order_completeTV.setText("已完成("+response.getInt("done_order")+")");
                    all_orderTV.setText("全部订单("+response.getInt("all_order")+")");
                    look_pingjiaTV.setText("查看评价("+response.getInt("evaluate_order")+")");
                    close_orderTV.setText("已关闭("+response.getInt("cancel_order")+")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        if(CommonValue.getStoreId(getActivity())!=0)
            params.put("store_id",CommonValue.getStoreId(getActivity()));
        getNetData.getData(params, CommonValue.orderCount);
    }

    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.wait_for_payLL: //未付款
                    startOrderActivity(1);
                    break;
                case R.id.wait_for_sendLL: //待发货
                    startOrderActivity(2);
                    break;
                case R.id.already_sendLL: //已发货
                    startOrderActivity(3);
                    break;
                case R.id.order_completeLL: //已完成
                    startOrderActivity(5);
                    break;
                case R.id.all_orderLL: //全部订单
                    startOrderActivity(8);
                    break;
                case R.id.look_pingjiaLL: //查看评价
                    Intent intent=new Intent(getActivity(),CheckPingjiaActivity.class);
                    startActivity(intent);
                    break;
                case R.id.close_orderLL: //已关闭
                    startOrderActivity(6);
                    break;
            }
        }
    }

    //启动订单
    private void startOrderActivity(int order_status){
        Intent intent=new Intent(getActivity(), OrderActivity.class);
        intent.putExtra("order_status",order_status);
        startActivity(intent);
    }
}
