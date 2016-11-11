package com.jionglemo.jionglemo_b.ZhiBo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mike on 2016/6/25.
 */
public class ZhiboPersonRVadapter extends RecyclerView.Adapter {

    private Context context;
    private List<ZhiboPenson> zhiboPensonList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int[] portrait={R.drawable.a1,R.drawable.a2,R.drawable.a3,R.drawable.a4,R.drawable.a5,R.drawable.a6,R.drawable.a7,R.drawable.a8,R.drawable.a9,R.drawable.a10,R.drawable.a11,R.drawable.a12,
            R.drawable.a13,R.drawable.a14,R.drawable.a15,R.drawable.a16,R.drawable.a17,R.drawable.a18,R.drawable.a19,R.drawable.a20,R.drawable.a21,R.drawable.a22,R.drawable.a23,R.drawable.a24,
            R.drawable.a25,R.drawable.a26,R.drawable.a27,R.drawable.a28,R.drawable.a29,R.drawable.a30,R.drawable.a31,R.drawable.a32,R.drawable.a33,R.drawable.a34,R.drawable.a35,R.drawable.a36,
            R.drawable.a37,R.drawable.a38,R.drawable.a39,R.drawable.a40,R.drawable.a41,R.drawable.a42,R.drawable.a43,R.drawable.a44,R.drawable.a45,R.drawable.a46,R.drawable.a47,R.drawable.a48,
            R.drawable.a49,R.drawable.a50,R.drawable.a51,R.drawable.a52,R.drawable.a53,R.drawable.a54,R.drawable.a55,R.drawable.a56,R.drawable.a57,R.drawable.a58,R.drawable.a59,R.drawable.a60,
            R.drawable.a61,R.drawable.a62,R.drawable.a63,R.drawable.a64,};
    public  boolean loadError=false;//是否加载失败

    public ZhiboPersonRVadapter(Context mContext, List<ZhiboPenson> mZhiboPensonList){
        context=mContext;
        zhiboPensonList=mZhiboPensonList;
        imageLoader = ImageLoaderArgument.getInstance(context);//全局初始化此配置
        options = ImageLoaderArgument.getDisplayImageOptions(R.drawable.person);
    }

    @Override
    public int getItemViewType(int position) {
        if (position== zhiboPensonList.size()){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
            View view= LayoutInflater.from(context).inflate(R.layout.daogoustar,parent,false);
            ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);
            return new MyViewHolder(view);
        }
        View view= LayoutInflater.from(context).inflate(R.layout.loadmore_progressbar,parent,false);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        return new LoadMoreViewHolder(view);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView photoIV;

        public MyViewHolder(View itemView) {
            super(itemView);
            photoIV= (CircleImageView) itemView.findViewById(R.id.photoIV);
        }
    }

    private class LoadMoreViewHolder extends RecyclerView.ViewHolder{

        private ProgressBar progressBar;
        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            progressBar= (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof LoadMoreViewHolder){
            LoadMoreViewHolder loadMoreViewHolder= (LoadMoreViewHolder) holder;
            if(!(ZhiboActivity.zhiboPersonPage <ZhiboActivity.zhiboPersonTotalPage)){
                loadMoreViewHolder.progressBar.setVisibility(View.GONE);
            }else if(loadError){
                loadMoreViewHolder.progressBar.setVisibility(View.GONE);
            }else {
                loadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
            }
            return;
        }
        MyViewHolder myViewHolder= (MyViewHolder) holder;
        if("".equals(zhiboPensonList.get(position).getPortrait())){
            int index= (int) (Math.random()*64);//产生0~63的随机数
            myViewHolder.photoIV.setImageResource(portrait[index]);
        }
        else
            imageLoader.displayImage(CommonValue.serverBasePath+zhiboPensonList.get(position).getPortrait(),myViewHolder.photoIV,options);
    }

    @Override
    public int getItemCount() {
        return zhiboPensonList.size()+1;
    }
}
