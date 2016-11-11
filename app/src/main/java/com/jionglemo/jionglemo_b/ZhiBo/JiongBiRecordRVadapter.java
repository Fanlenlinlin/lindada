package com.jionglemo.jionglemo_b.ZhiBo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.CommonTool.Date_transform;
import com.jionglemo.jionglemo_b.R;

import java.util.List;

/**
 * Created by Mike on 2016/9/13.
 */
public class JiongBiRecordRVadapter extends RecyclerView.Adapter{

    private Context context;
    private List<JiongBiRecord> jiongBiRecordList;
    public  boolean loadError=false;//是否加载失败

    public JiongBiRecordRVadapter(Context context,List<JiongBiRecord> jiongBiRecordList){
        this.context=context;
        this.jiongBiRecordList=jiongBiRecordList;
    }

    private class JiongBiRecordViewHolder extends RecyclerView.ViewHolder{

        private TextView priceTV;
        private TextView contentTV;
        private TextView timeTV;
        public JiongBiRecordViewHolder(View itemView) {
            super(itemView);
            priceTV= (TextView) itemView.findViewById(R.id.priceTV);
            contentTV= (TextView) itemView.findViewById(R.id.contentTV);
            timeTV= (TextView) itemView.findViewById(R.id.timeTV);
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
    public int getItemViewType(int position) {
        if (position== jiongBiRecordList.size())
            return 1;
        else
            return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.loadmore, parent,false);
            ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            return new LoadMoreViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.jiongbirecord_item, parent,false);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new JiongBiRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreViewHolder) {
            final LoadMoreViewHolder loadMoreViewHolder= (LoadMoreViewHolder) holder;
            if(!(JiongBiRecordActivity.p<JiongBiRecordActivity.total_page)){
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
        JiongBiRecordViewHolder jiongBiRecordViewHolder= (JiongBiRecordViewHolder) holder;
        jiongBiRecordViewHolder.priceTV.setText("+"+jiongBiRecordList.get(position).getPrice());
        jiongBiRecordViewHolder.contentTV.setText(jiongBiRecordList.get(position).getSend_name()+"赠送"+jiongBiRecordList.get(position).getGift_name());
        jiongBiRecordViewHolder.timeTV.setText(Date_transform.getTime(jiongBiRecordList.get(position).getSend_time()));
    }

    @Override
    public int getItemCount() {
        return jiongBiRecordList.size()+1;
    }

    //暴露一个方法供外部调用
    public void loadAgain(){

    }
}
