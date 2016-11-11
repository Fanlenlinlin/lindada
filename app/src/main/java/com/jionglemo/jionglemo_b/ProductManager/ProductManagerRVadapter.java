package com.jionglemo.jionglemo_b.ProductManager;

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
import android.widget.Toast;

import com.jionglemo.jionglemo_b.BasePacket.MainActivity;
import com.jionglemo.jionglemo_b.CommonTool.Date_transform;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonTool.PictureTool;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.CommonView.DeleteDialogFragment;
import com.jionglemo.jionglemo_b.CommonView.SharePopupWindow;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mike on 2016/6/22.
 */
public class ProductManagerRVadapter extends RecyclerView.Adapter {

    private Context context;
    private List<ProductManager> productManagerList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public  boolean loadError=false;//是否加载失败
    private int status;//产品状态：0上架；1下架；-1表示从产品分类中过来的

    public ProductManagerRVadapter(Context mContext, List<ProductManager> mProductManagerList,int mStatus){
        context=mContext;
        productManagerList=mProductManagerList;
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
        private ImageView goodIV;
        private TextView product_nameTV;
        private TextView present_priceTV;
        private TextView purchaseTV;
        private TextView concern_numberTV;
        private TextView repertoryTV;
        private TextView create_timeTV;
        private LinearLayout showLL;
        private LinearLayout shareLL;
        private LinearLayout downLL;
        private ImageView statusIV;
        private TextView statusTV;
        private TextView already_downTV;

        public ProductManagerViewHolder(View itemView) {
            super(itemView);
            goodLL= (LinearLayout) itemView.findViewById(R.id.goodLL);
            goodIV= (ImageView) itemView.findViewById(R.id.goodIV);
            product_nameTV= (TextView) itemView.findViewById(R.id.product_nameTV);
            present_priceTV= (TextView) itemView.findViewById(R.id.present_priceTV);
            purchaseTV= (TextView) itemView.findViewById(R.id.purchaseTV);
            concern_numberTV= (TextView) itemView.findViewById(R.id.concern_numberTV);
            repertoryTV= (TextView) itemView.findViewById(R.id.repertoryTV);
            create_timeTV= (TextView) itemView.findViewById(R.id.create_timeTV);
            showLL= (LinearLayout) itemView.findViewById(R.id.showLL);
            shareLL= (LinearLayout) itemView.findViewById(R.id.shareLL);
            downLL= (LinearLayout) itemView.findViewById(R.id.downLL);
            statusIV= (ImageView) itemView.findViewById(R.id.statusIV);
            statusTV= (TextView) itemView.findViewById(R.id.statusTV);
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

        View view = LayoutInflater.from(context).inflate(R.layout.product_manager_item, parent,false);
         ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         view.setLayoutParams(layoutParams);
        return new ProductManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LoadMoreViewHolder) {
            final LoadMoreViewHolder loadMoreViewHolder= (LoadMoreViewHolder) holder;
            boolean more;
            if(status==-1)
                more=ProductClassifyActivity.p<ProductClassifyActivity.total_page;
            else
                more=ProductManagerFragment.p<ProductManagerFragment.total_page;
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

        ProductManagerViewHolder productManagerViewHolder= (ProductManagerViewHolder) holder;
        imageLoader.displayImage(CommonValue.serverBasePath+productManagerList.get(position).getThumb(),productManagerViewHolder.goodIV,options);
        productManagerViewHolder.product_nameTV.setText(productManagerList.get(position).getProduct_name());
        productManagerViewHolder.present_priceTV.setText("¥ "+productManagerList.get(position).getPresent_price());
        productManagerViewHolder.purchaseTV.setText("销量 "+productManagerList.get(position).getPurchase());
        productManagerViewHolder.concern_numberTV.setText("收藏 "+productManagerList.get(position).getConcern_number());
        productManagerViewHolder.repertoryTV.setText("库存 "+productManagerList.get(position).getRepertory());
        productManagerViewHolder.create_timeTV.setText("添加 "+ Date_transform.getTimeSimple(productManagerList.get(position).getCreate_time()));

        if(status==1){//对已下架的进行处理
            productManagerViewHolder.statusIV.setImageBitmap(PictureTool.readBitMap(context,R.drawable.product_manager_up));
            productManagerViewHolder.shareLL.setVisibility(View.GONE);
            productManagerViewHolder.already_downTV.setVisibility(View.VISIBLE);
            productManagerViewHolder.statusTV.setText("上架");
        }

        if(status==-1){//产品分类
            productManagerViewHolder.downLL.setVisibility(View.GONE);
        }

        productManagerViewHolder.goodLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productShow(position);
            }
        });

        //商品预览
        productManagerViewHolder.showLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productShow(position);
            }
        });

        //商品分享
        productManagerViewHolder.shareLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePopupWindow sharePopupWindow=new SharePopupWindow(context);
                sharePopupWindow.showSharePopupWindow(v);
            }
        });

        //商品上下架
        productManagerViewHolder.downLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==0){
                    showUpAndDown("商品下架","您确定要将商品下架吗？","商品已下架",position,1);
                }else {
                    showUpAndDown("商品上架","您确定要将商品上架吗？","商品已上架",position,0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productManagerList.size()+1;
    }
    //商品预览
    private void productShow(int position){
        Intent intent=new Intent(context,ProductShowActivity.class);
        intent.putExtra("product_id",productManagerList.get(position).getProduct_id());
        context.startActivity(intent);
    }

    //上下架处理
    private void showUpAndDown(String dialogTitle, String dialogMessage, final String toastMessage, final int position, final int toStatus){
        DeleteDialogFragment deleteDialogFragment=DeleteDialogFragment.newInstance(dialogTitle,dialogMessage,"确定");
        deleteDialogFragment.setOnDeleteListener(new DeleteDialogFragment.DeleteListener() {
            @Override
            public void onDelete() {
                GetNetData getNetData=new GetNetData(context){
                    @Override
                    public void getDataSuccess(JSONObject response) {
                        super.getDataSuccess(response);
                        notifyItemRemoved(position);//刷新列表，使用该方法可避免闪烁
                        productManagerList.remove(position);//从绑定的列表中删除
                        notifyItemRangeChanged(position, getItemCount());//通知position之后的数据刷新
                        Toast.makeText(context,toastMessage,Toast.LENGTH_SHORT).show();
                        if(productManagerList.size()==0)//如果没有数据了，展示空页面
                            showNone();
                    }
                };
                RequestParams params=new RequestParams();
                params.put("status",toStatus);
                params.put("product_id",productManagerList.get(position).getProduct_id());
                getNetData.getData(params,CommonValue.updateProduct);
            }
        });
        MainActivity mainActivity= (MainActivity) context;
        deleteDialogFragment.show(mainActivity.getSupportFragmentManager(),"deleteDialog");
    }

    //暴露一个方法供外部调用，加载更多
    public void loadAgain(){

    }

    //暴露一个方法供外部调用，显示无数据
    public void showNone(){

    }
}
