package com.jionglemo.jionglemo_b.ProductManager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Mike on 2016/6/22.
 */
public class AllManagerRVadapter extends RecyclerView.Adapter {

    private Context context;
    private List<ProductManager> productManagerList;
    private List<Integer> positionCheckList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public  boolean loadError=false;//是否加载失败
    private int status;//产品状态：0上架；1下架；

    public AllManagerRVadapter(Context mContext, List<ProductManager> mProductManagerList, List<Integer> mPositionCheckList, int mStatus){
        context=mContext;
        productManagerList=mProductManagerList;
        positionCheckList=mPositionCheckList;
        status=mStatus;
        imageLoader = ImageLoaderArgument.getInstance(context);//全局初始化此配置
        options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
    }

    @Override
    public int getItemViewType(int position) {
        if (position== productManagerList.size())
            return 1;
        else
            return 0;
    }

    private class ProductManagerViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout goodLL;
        private CheckBox allManagerCB;
        private ImageView goodIV;
        private TextView product_nameTV;
        private TextView present_priceTV;
        private TextView purchaseTV;
        private TextView repertoryTV;
        private TextView already_downTV;

        public ProductManagerViewHolder(View itemView) {
            super(itemView);
            goodLL= (LinearLayout) itemView.findViewById(R.id.goodLL);
            allManagerCB= (CheckBox) itemView.findViewById(R.id.allManagerCB);
            goodIV= (ImageView) itemView.findViewById(R.id.goodIV);
            product_nameTV= (TextView) itemView.findViewById(R.id.product_nameTV);
            present_priceTV= (TextView) itemView.findViewById(R.id.present_priceTV);
            purchaseTV= (TextView) itemView.findViewById(R.id.purchaseTV);
            repertoryTV= (TextView) itemView.findViewById(R.id.repertoryTV);
            already_downTV= (TextView) itemView.findViewById(R.id.already_downTV);
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

        View view = LayoutInflater.from(context).inflate(R.layout.all_manager_item, parent,false);
         ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         view.setLayoutParams(layoutParams);
        return new ProductManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LoadMoreViewHolder) {
            final LoadMoreViewHolder loadMoreViewHolder= (LoadMoreViewHolder) holder;
            if(!(AllManagerActivity.p<AllManagerActivity.total_page)){
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

        ProductManagerViewHolder productManagerViewHolder= (ProductManagerViewHolder) holder;
        imageLoader.displayImage(CommonValue.serverBasePath+productManagerList.get(position).getThumb(),productManagerViewHolder.goodIV,options);
        productManagerViewHolder.product_nameTV.setText(productManagerList.get(position).getProduct_name());
        productManagerViewHolder.present_priceTV.setText("¥ "+productManagerList.get(position).getPresent_price());
        productManagerViewHolder.purchaseTV.setText("销量 "+productManagerList.get(position).getPurchase());
        productManagerViewHolder.repertoryTV.setText("库存 "+productManagerList.get(position).getRepertory());

        if(status==1){//对已下架的进行处理
            productManagerViewHolder.already_downTV.setVisibility(View.VISIBLE);
        }

        productManagerViewHolder.allManagerCB.setOnCheckedChangeListener(null);//必须先取消其监听，否则出现不同item误监听的情况
        if(positionCheckList.get(position)==1)
            productManagerViewHolder.allManagerCB.setChecked(true);
        else
            productManagerViewHolder.allManagerCB.setChecked(false);

        productManagerViewHolder.allManagerCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    positionCheckList.set(position,1);//代表选中
                }else {
                    positionCheckList.set(position,0);//代表未选中
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productManagerList.size()+1;
    }

    //暴露一个方法供外部调用
    public void loadAgain(){

    }
}
