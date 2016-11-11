package com.jionglemo.jionglemo_b.HomePage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.ProductManager.ProductShowActivity;
import com.jionglemo.jionglemo_b.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Mike on 2016/7/15.
 */
public class VRstoreProductRVadapter extends RecyclerView.Adapter{

    private Context context;
    private List<Product> productList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public  boolean loadError=false;//是否加载失败

    public VRstoreProductRVadapter(Context mContext, List<Product> mProductList){
        context=mContext;
        productList=mProductList;
        imageLoader = ImageLoaderArgument.getInstance(context);//全局初始化此配置
        options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
    }

    private class VRstoreProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView productIV;
        private TextView productNameTV;
        private TextView priceNowTV;
        private TextView priceOldTV;
        private LinearLayout productLL;

        public VRstoreProductViewHolder(View itemView) {
            super(itemView);
            productIV= (ImageView) itemView.findViewById(R.id.productIV);
            productNameTV= (TextView) itemView.findViewById(R.id.productNameTV);
            priceNowTV= (TextView) itemView.findViewById(R.id.priceNowTV);
            priceOldTV= (TextView) itemView.findViewById(R.id.priceOldTV);
            productLL= (LinearLayout) itemView.findViewById(R.id.productLL);
        }
    }

    //加载更多
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
    public int getItemViewType(int position) {
        if(position==productList.size())
            return 0;
        else
            return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0){
            View view = LayoutInflater.from(context).inflate(R.layout.loadmore, parent,false);
            ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            return new LoadMoreViewHolder(view);
        }

        View view= LayoutInflater.from(context).inflate(R.layout.vrstore_product_item,parent,false);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new VRstoreProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof LoadMoreViewHolder) {
            final LoadMoreViewHolder loadMoreViewHolder= (LoadMoreViewHolder) holder;
            if(!(VRActivity.p<VRActivity.total_page)){
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

        VRstoreProductViewHolder vRstoreProductViewHolder= (VRstoreProductViewHolder) holder;
        imageLoader.displayImage(CommonValue.serverBasePath+productList.get(position).getThumb(),vRstoreProductViewHolder.productIV,options);
        vRstoreProductViewHolder.productNameTV.setText(productList.get(position).getProduct_name());
        vRstoreProductViewHolder.priceNowTV.setText("¥："+productList.get(position).getPresent_price());
        vRstoreProductViewHolder.priceOldTV.setText("原价¥ "+productList.get(position).getOriginal_price());

        vRstoreProductViewHolder.productLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ProductShowActivity.class);
                intent.putExtra("product_id",productList.get(position).getProduct_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size()+1;
    }

    //暴露一个方法供外部调用
    public void loadAgain(){

    }
}
