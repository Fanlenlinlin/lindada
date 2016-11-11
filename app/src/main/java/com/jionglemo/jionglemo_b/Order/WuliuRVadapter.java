package com.jionglemo.jionglemo_b.Order;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.jionglemo.jionglemo_b.R;

import java.util.List;

/**
 * Created by Mike on 2016/7/1.
 */
public class WuliuRVadapter extends RecyclerView.Adapter {

    private Context context;
    private List<WuliuMessage> wuLiuMessageList;
    private float scale;

    public WuliuRVadapter(Context context, List<WuliuMessage> wuLiuMessageList){
        this.context=context;
        this.wuLiuMessageList=wuLiuMessageList;
        scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.wuliumessage,parent,false);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new MyViewHolder(view);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView yuandianIV;
        private TextView lineTV;
        private TextView timeTV;
        private TextView contentTV;

        public MyViewHolder(View itemView) {
            super(itemView);
            yuandianIV= (ImageView) itemView.findViewById(R.id.yuandianIV);
            lineTV= (TextView) itemView.findViewById(R.id.lineTV);
            timeTV= (TextView) itemView.findViewById(R.id.timeTV);
            contentTV= (TextView) itemView.findViewById(R.id.contentTV);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
         MyViewHolder myViewHolder= (MyViewHolder) holder;
         myViewHolder.timeTV.setText(wuLiuMessageList.get(position).getTime());
         myViewHolder.contentTV.setText(wuLiuMessageList.get(position).getContext());
         if(position==0){
             myViewHolder.yuandianIV.setImageResource(R.drawable.yuanshapegreen);
             FrameLayout.LayoutParams yuandianLP=new FrameLayout.LayoutParams((int) (10*scale+0.5),(int) (10*scale+0.5));
             yuandianLP.setMargins(0,(int)(5*scale+0.5f), 0,0);
             myViewHolder.yuandianIV.setLayoutParams(yuandianLP);

             FrameLayout.LayoutParams lineLP=new FrameLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
             lineLP.setMargins((int)(5*scale+0.5f),(int)(5*scale+0.5f),(int)(16*scale+0.5f),0);
             myViewHolder.lineTV.setLayoutParams(lineLP);

             myViewHolder.timeTV.setTextColor(Color.rgb(10,143,8));
             myViewHolder.contentTV.setTextColor(Color.rgb(10,143,8));
         }else {
             myViewHolder.yuandianIV.setImageResource(R.drawable.yuanshapegrey_999999);
             FrameLayout.LayoutParams yuandianLP=new FrameLayout.LayoutParams((int) (8*scale+0.5),(int) (8*scale+0.5));
             yuandianLP.setMargins((int)(1*scale+0.5f),(int)(5*scale+0.5f), 0,0);
             myViewHolder.yuandianIV.setLayoutParams(yuandianLP);

             FrameLayout.LayoutParams lineLP=new FrameLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
             lineLP.setMargins((int)(5*scale+0.5f),0,(int)(16*scale+0.5f),0);
             myViewHolder.lineTV.setLayoutParams(lineLP);

             myViewHolder.timeTV.setTextColor(Color.rgb(153,153,153));
             myViewHolder.contentTV.setTextColor(Color.rgb(153,153,153));
         }
    }

    @Override
    public int getItemCount() {
        return wuLiuMessageList.size();
    }
}
