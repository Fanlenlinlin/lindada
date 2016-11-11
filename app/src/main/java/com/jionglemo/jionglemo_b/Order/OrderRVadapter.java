package com.jionglemo.jionglemo_b.Order;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.CommonView.DeleteDialogFragment;
import com.jionglemo.jionglemo_b.ProductManager.ProductShowActivity;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mike on 2016/6/22.
 */
public class OrderRVadapter extends RecyclerView.Adapter {

    private Context context;
    private List<Order> orderList;
    private String tag;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public  boolean loadError=false;//是否加载失败

    public OrderRVadapter(Context mContext, List<Order> mOrderList,String mTag){
        context=mContext;
        orderList=mOrderList;
        tag=mTag;
        imageLoader = ImageLoaderArgument.getInstance(context);//全局初始化此配置
        options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
    }

    @Override
    public int getItemViewType(int position) {
        if (position== orderList.size())
            return 1;
        else
            return 0;
    }

    private class OrderViewHolder extends RecyclerView.ViewHolder{

        private TextView shopNameTV;
        private TextView orderStateTV;
        private ImageView goodIV;
        private TextView goodNameTV;
        private TextView moneyTV;
        private TextView sumNumTV;
        private TextView sumMoneyTV;
        private TextView peisongWayTV;
        private TextView stateActionTV;
        private TextView queryDetailTV;
        private LinearLayout goodLL;

        public OrderViewHolder(View itemView) {
            super(itemView);
            shopNameTV= (TextView) itemView.findViewById(R.id.shopNameTV);
            orderStateTV= (TextView) itemView.findViewById(R.id.orderStateTV);
            goodIV= (ImageView) itemView.findViewById(R.id.goodIV);
            goodNameTV= (TextView) itemView.findViewById(R.id.goodNameTV);
            moneyTV= (TextView) itemView.findViewById(R.id.moneyTV);
            sumNumTV= (TextView) itemView.findViewById(R.id.sumNumTV);
            sumMoneyTV= (TextView) itemView.findViewById(R.id.sumMoneyTV);
            peisongWayTV= (TextView) itemView.findViewById(R.id.peisongWayTV);
            stateActionTV= (TextView) itemView.findViewById(R.id.stateActionTV);
            queryDetailTV= (TextView) itemView.findViewById(R.id.queryDetailTV);
            goodLL= (LinearLayout) itemView.findViewById(R.id.goodLL);
        }
    }

    //点击加载更多
    private class LoadMoreViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout loadmoreLL;
        private ProgressBar progressBar;
        private TextView messageTV;
        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            loadmoreLL= (LinearLayout) itemView.findViewById(R.id.loadmoreLL);
            progressBar= (ProgressBar) itemView.findViewById(R.id.progressBar);
            messageTV= (TextView) itemView.findViewById(R.id.messageTV);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.loadmore, parent,false);
            ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            return new LoadMoreViewHolder(view);
        }

        View view = LayoutInflater.from(context).inflate(R.layout.order_shop_good, parent,false);
         ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         view.setLayoutParams(layoutParams);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LoadMoreViewHolder) {
            final LoadMoreViewHolder loadMoreViewHolder= (LoadMoreViewHolder) holder;
            boolean more=false;
            switch (tag){
                case "订单":
                    more=OrderActivity.p<OrderActivity.total_page;
                    break;
                case "搜索订单":
                    more=SearchOrderActivity.p<SearchOrderActivity.total_page;
                    break;
            }
            if(!more){
                loadMoreViewHolder.progressBar.setVisibility(View.GONE);
                loadMoreViewHolder.messageTV.setText("没有更多数据了");
            }else if(loadError){
                loadMoreViewHolder.progressBar.setVisibility(View.GONE);
                loadMoreViewHolder.messageTV.setText("加载失败，点击重新加载");
                loadMoreViewHolder.loadmoreLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadError=false;//复位
                        loadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
                        loadMoreViewHolder.messageTV.setText("请稍后···");
                        loadAgain();
                    }
                });
            }else {
                loadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
                loadMoreViewHolder.messageTV.setText("请稍后···");
            }
            return;
        }

        OrderViewHolder orderViewHolder= (OrderViewHolder) holder;
        orderViewHolder.shopNameTV.setText(orderList.get(position).getStore_name());
        //根据不同状态设置显示和点击逻辑
        setStateAndAction(orderViewHolder,orderList.get(position).getOrder_status(),position);

        imageLoader.displayImage(CommonValue.serverBasePath+orderList.get(position).getThumb(),orderViewHolder.goodIV,options);
        orderViewHolder.goodNameTV.setText(orderList.get(position).getProduct_name()+"("+orderList.get(position).getNorms_name()+")");
        orderViewHolder.moneyTV.setText(orderList.get(position).getUnit_price());
        orderViewHolder.sumNumTV.setText(orderList.get(position).getQuantity()+"");
        orderViewHolder.sumMoneyTV.setText(orderList.get(position).getTotal_price());
        if(orderList.get(position).getPostage_type()==0)
            orderViewHolder.peisongWayTV.setText("邮费：¥ "+orderList.get(position).getPostage());
        else
            orderViewHolder.peisongWayTV.setText("配送方式：包邮");

        orderViewHolder.goodLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ProductShowActivity.class);
                intent.putExtra("product_id",orderList.get(position).getProduct_id());
                context.startActivity(intent);
            }
        });
    }

    //根据不同状态设置显示和点击逻辑
    private void setStateAndAction(OrderViewHolder orderViewHolder, final int state, final int position){
        switch (state){
            case 1:
                orderViewHolder.orderStateTV.setText("等待买家付款");
                orderViewHolder.stateActionTV.setText("修改价格");
                orderViewHolder.queryDetailTV.setText("查看订单详情");
                orderViewHolder.queryDetailTV.setVisibility(View.VISIBLE);
                orderViewHolder.stateActionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog dialog = new AlertDialog.Builder(context).create();
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setContentView(R.layout.change_price);
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        TextView priceOriginalTV= (TextView) window.findViewById(R.id.priceOriginalTV);
                        final EditText priceNowET= (EditText) window.findViewById(R.id.priceNowET);
                        final TextView confirmTV= (TextView) window.findViewById(R.id.confirmTV);

                        //调用系统输入法
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        //显示键盘
                        imm.showSoftInput(priceNowET, 0);
                        imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);

                        priceOriginalTV.setText(orderList.get(position).getTotal_price());
                        confirmTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(priceNowET.getText().toString().trim().length()==0){
                                    Toast.makeText(context,"请输入修改的价格",Toast.LENGTH_SHORT).show();
                                }else {
                                    GetNetData getNetData=new GetNetData(context){
                                        @Override
                                        public void getDataSuccess(JSONObject response) {
                                            super.getDataSuccess(response);
                                            try {
                                                Toast.makeText(context,response.getString("message"),Toast.LENGTH_SHORT).show();
                                                Order order=orderList.get(position);
                                                order.setTotal_price(priceNowET.getText().toString());
                                                orderList.set(position,order);
                                                notifyItemChanged(position);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            dialog.dismiss();
                                        }
                                    };
                                    RequestParams params=new RequestParams();
                                    params.put("order_id",orderList.get(position).getId());
                                    params.put("total_price", priceNowET.getText().toString());
                                    getNetData.getData(params,CommonValue.updateOrderInfo);
                                }
                            }
                        });
                    }
                });
                orderViewHolder.queryDetailTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSkipIntent(position);
                    }
                });
                break;
            case 2:
                orderViewHolder.orderStateTV.setText("等待卖家发货");
                orderViewHolder.stateActionTV.setText("立即发货");
                orderViewHolder.queryDetailTV.setText("查看订单详情");
                orderViewHolder.queryDetailTV.setVisibility(View.VISIBLE);
                orderViewHolder.stateActionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context,SendNowActivity.class);
                        intent.putExtra("order",orderList.get(position));
                        intent.putExtra("position",position);
                        switch (tag){
                            case "订单":
                                OrderActivity orderActivity= (OrderActivity) context;
                                orderActivity.startActivityForResult(intent,1);
                                break;
                            case "搜索订单":
                                SearchOrderActivity searchOrderActivity= (SearchOrderActivity) context;
                                searchOrderActivity.startActivityForResult(intent,1);
                                break;
                        }
                    }
                });
                orderViewHolder.queryDetailTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSkipIntent(position);
                    }
                });
                break;
            case 3:
                orderViewHolder.orderStateTV.setText("已发货");
                orderViewHolder.stateActionTV.setText("查看物流");
                orderViewHolder.queryDetailTV.setVisibility(View.VISIBLE);
                orderViewHolder.queryDetailTV.setText("查看订单详情");
                orderViewHolder.stateActionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context,WuliuActivity.class);
                        intent.putExtra("order",orderList.get(position));
                        context.startActivity(intent);
                    }
                });
                orderViewHolder.queryDetailTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSkipIntent(position);
                    }
                });
                break;
            case 4:
                orderViewHolder.orderStateTV.setText("等待买家评价");
                orderViewHolder.stateActionTV.setText("查看物流");
                orderViewHolder.queryDetailTV.setVisibility(View.GONE);
                orderViewHolder.stateActionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context,WuliuActivity.class);
                        intent.putExtra("order",orderList.get(position));
                        context.startActivity(intent);
                    }
                });
                break;
            case 5:
                orderViewHolder.orderStateTV.setText("交易完成");
                orderViewHolder.stateActionTV.setText("查看物流");
                orderViewHolder.queryDetailTV.setVisibility(View.VISIBLE);
                orderViewHolder.queryDetailTV.setText("查看订单详情");
                orderViewHolder.stateActionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context,WuliuActivity.class);
                        intent.putExtra("order",orderList.get(position));
                        context.startActivity(intent);
                    }
                });
                orderViewHolder.queryDetailTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSkipIntent(position);
                    }
                });
                break;
            case 6:
                orderViewHolder.orderStateTV.setText("交易关闭");
                orderViewHolder.stateActionTV.setText("删除订单");
                orderViewHolder.queryDetailTV.setVisibility(View.VISIBLE);
                orderViewHolder.queryDetailTV.setText("查看订单详情");
                orderViewHolder.stateActionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteDialogFragment deleteDialogFragment=DeleteDialogFragment.newInstance("删除订单","您确定要删除订单吗？","确定");
                        deleteDialogFragment.setOnDeleteListener(new DeleteDialogFragment.DeleteListener() {
                            @Override
                            public void onDelete() {
                                GetNetData getNetData=new GetNetData(context){
                                    @Override
                                    public void getDataSuccess(JSONObject response) {
                                        super.getDataSuccess(response);
                                        notifyItemRemoved(position);//刷新列表，使用该方法可避免闪烁
                                        orderList.remove(position);//从绑定的列表中删除
                                        notifyItemRangeChanged(position, getItemCount());//通知position之后的数据刷新
                                        Toast.makeText(context,"订单已删除",Toast.LENGTH_SHORT).show();
                                    }
                                };
                                RequestParams params=new RequestParams();
                                params.put("order_id",orderList.get(position).getId());
                                getNetData.getData(params,CommonValue.delOrder);
                            }
                        });
                        switch (tag){
                            case "订单":
                                OrderActivity orderActivity= (OrderActivity) context;
                                deleteDialogFragment.show(orderActivity.getSupportFragmentManager(),"deleteDialog");
                                break;
                            case "搜索订单":
                                SearchOrderActivity searchOrderActivity= (SearchOrderActivity) context;
                                deleteDialogFragment.show(searchOrderActivity.getSupportFragmentManager(),"deleteDialog");
                                break;
                        }
                    }
                });
                orderViewHolder.queryDetailTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSkipIntent(position);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size()+1;
    }

    //暴露一个方法供外部调用
    public void loadAgain(){

    }


    private void setSkipIntent(int position){
        Intent intent = new Intent(context,OrderDetaliActivity.class);
        intent.putExtra("order_id",orderList.get(position).getId());
        switch (orderList.get(position).getOrder_status()){
            case 1:
                intent.putExtra("tag","未付款");
                break;
            case 2:
                intent.putExtra("tag","待发货");
                break;
            case 3:
                intent.putExtra("tag","已发货");
                break;
            case 5:
                intent.putExtra("tag","已完成");
                break;
            case 6:
                intent.putExtra("tag","已关闭");
                break;

        }
        intent.putExtra("position",position);
        context.startActivity(intent);
    }
}
