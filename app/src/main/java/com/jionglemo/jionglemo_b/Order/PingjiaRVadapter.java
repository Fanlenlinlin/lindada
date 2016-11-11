package com.jionglemo.jionglemo_b.Order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.CommonTool.Date_transform;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.CommonView.StarView;
import com.jionglemo.jionglemo_b.ProductManager.ProductShowActivity;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mike on 2016/6/22.
 */
public class PingjiaRVadapter extends RecyclerView.Adapter {

    private Context context;
    private List<Pingjia> pingjiaList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public  boolean loadError=false;//是否加载失败
    private InputMethodManager imm;
    private String tag;
    private Map<Integer,String>  replyContentMap;//用一个Map临时存储用户的回复内容

    public PingjiaRVadapter(Context mContext, List<Pingjia> mPingjiaList,String mTag){
        context=mContext;
        pingjiaList=mPingjiaList;
        tag=mTag;
        imageLoader = ImageLoaderArgument.getInstance(context);//全局初始化此配置
        options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        replyContentMap=new HashMap<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position== pingjiaList.size())
            return 1;
        else
            return 0;
    }

    private class PingjiaViewHolder extends RecyclerView.ViewHolder{

        private ImageView goodIV;
        private StarView starView;
        private TextView user_nameTV;
        private TextView timeTV;
        private TextView contentTV;
        private TextView replyTV;
        private EditText replyET;
        private TextView confirmTV;
        private TextView replyContentTV;
        private TextView replyTimeTV;
        private LinearLayout goodLL;

        public PingjiaViewHolder(View itemView) {
            super(itemView);
            goodIV= (ImageView) itemView.findViewById(R.id.goodIV);
            starView= (StarView) itemView.findViewById(R.id.starView);
            user_nameTV= (TextView) itemView.findViewById(R.id.user_nameTV);
            timeTV= (TextView) itemView.findViewById(R.id.timeTV);
            contentTV= (TextView) itemView.findViewById(R.id.contentTV);
            replyTV= (TextView) itemView.findViewById(R.id.replyTV);
            replyET= (EditText) itemView.findViewById(R.id.replyET);
            confirmTV= (TextView) itemView.findViewById(R.id.confirmTV);
            replyContentTV= (TextView) itemView.findViewById(R.id.replyContentTV);
            replyTimeTV= (TextView) itemView.findViewById(R.id.replyTimeTV);
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

        View view = LayoutInflater.from(context).inflate(R.layout.pingjia_item, parent,false);
         ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         view.setLayoutParams(layoutParams);
        return new PingjiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LoadMoreViewHolder) {
            final LoadMoreViewHolder loadMoreViewHolder= (LoadMoreViewHolder) holder;
            boolean more=false;
            switch (tag){
                case "评价":
                    more=CheckPingjiaActivity.p<CheckPingjiaActivity.total_page;
                    break;
                case "搜索评价":
                    more=SearchPingjiaActivity.p<SearchPingjiaActivity.total_page;
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

        final PingjiaViewHolder pingjiaViewHolder = (PingjiaViewHolder) holder;
        imageLoader.displayImage(CommonValue.serverBasePath+pingjiaList.get(position).getThumb(), pingjiaViewHolder.goodIV,options);
        pingjiaViewHolder.starView.setStarNum(pingjiaList.get(position).getStar());
        pingjiaViewHolder.user_nameTV.setText(pingjiaList.get(position).getUser_name());
        pingjiaViewHolder.timeTV.setText(Date_transform.getTimesOne(pingjiaList.get(position).getCreate_time()));
        pingjiaViewHolder.contentTV.setText(pingjiaList.get(position).getContent());

        //当编辑框失去焦点时，让其隐藏
        pingjiaViewHolder.replyET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    imm.showSoftInput(pingjiaViewHolder.replyET, 0);
                else{
                    pingjiaViewHolder.replyET.setVisibility(View.GONE);
                    pingjiaViewHolder.confirmTV.setVisibility(View.GONE);
                    imm.hideSoftInputFromWindow(pingjiaViewHolder.replyET.getWindowToken(), 0);
                    //失去焦点后，如果edittext中有内容，则用Map进行临时存储
                    if(pingjiaViewHolder.replyET.getText().toString().trim().length()>0){
                        replyContentMap.put(position,pingjiaViewHolder.replyET.getText().toString().trim());
                    }
                }
            }
        });

        pingjiaViewHolder.replyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pingjiaViewHolder.replyET.setVisibility(View.VISIBLE);
                pingjiaViewHolder.confirmTV.setVisibility(View.VISIBLE);
                pingjiaViewHolder.replyET.setText("");//为避免复用导致的错位，先统一进行清空
                //请求焦点，显示键盘
                pingjiaViewHolder.replyET.requestFocus();
                //如果Map中存在临时存放的内容，则进行恢复，同时，将Map该键去掉
                if(replyContentMap.containsKey(position)) {
                    pingjiaViewHolder.replyET.setText(replyContentMap.get(position));//恢复内容
                    replyContentMap.remove(position);
                }
            }
        });

        pingjiaViewHolder.confirmTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String replyString=pingjiaViewHolder.replyET.getText().toString().trim();
                if(replyString.length()==0)
                    Toast.makeText(context,"请输入回复的内容",Toast.LENGTH_SHORT).show();
                else {
                    GetNetData getNetData=new GetNetData(context){
                        @Override
                        public void getDataSuccess(JSONObject response) {
                            super.getDataSuccess(response);
                            try {
                                Pingjia pingjia=pingjiaList.get(position);
                                pingjia.setReply(replyString);
                                pingjia.setReply_time(response.getJSONObject("message").getString("reply_time"));
                                pingjiaList.set(position,pingjia);
                                notifyItemChanged(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    RequestParams params=new RequestParams();
                    params.put("evaluate_id",pingjiaList.get(position).getId());
                    params.put("reply_content",replyString);
                    getNetData.getData(params,CommonValue.replyUserEvaluate);
                }
            }
        });

        if(pingjiaList.get(position).getReply()==null){
            pingjiaViewHolder.replyTV.setClickable(true);
            pingjiaViewHolder.replyTV.setText("回复");
            pingjiaViewHolder.replyTV.setTextColor(Color.rgb(255,0,153));
            pingjiaViewHolder.replyContentTV.setVisibility(View.GONE);
            pingjiaViewHolder.replyTimeTV.setVisibility(View.GONE);
        }else {
            pingjiaViewHolder.replyTV.setClickable(false);
            pingjiaViewHolder.replyTV.setText("已回复");
            pingjiaViewHolder.replyTV.setTextColor(Color.rgb(102,102,102));
            pingjiaViewHolder.replyContentTV.setVisibility(View.VISIBLE);
            pingjiaViewHolder.replyContentTV.setText(pingjiaList.get(position).getReply());
            pingjiaViewHolder.replyTimeTV.setVisibility(View.VISIBLE);
            pingjiaViewHolder.replyTimeTV.setText("回复时间："+Date_transform.getTimesOne(pingjiaList.get(position).getReply_time()));
        }

        pingjiaViewHolder.goodLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ProductShowActivity.class);
                intent.putExtra("product_id",pingjiaList.get(position).getProduct_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pingjiaList.size()+1;
    }

    //暴露一个方法供外部调用
    public void loadAgain(){

    }
}
