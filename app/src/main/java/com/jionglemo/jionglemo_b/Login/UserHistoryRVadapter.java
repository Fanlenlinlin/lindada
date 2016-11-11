package com.jionglemo.jionglemo_b.Login;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.DB.UserHistoryDB;
import com.jionglemo.jionglemo_b.R;

import java.util.List;

/**
 * Created by Mike on 2016/6/25.
 */
public class UserHistoryRVadapter extends RecyclerView.Adapter {

    private Context context;
    private List<String> userPhoneList;
    private UserHistoryDB userHistoryDB;

    public UserHistoryRVadapter(Context mContext, List<String> mUserPhoneList, UserHistoryDB mUserHistoryDB){
        context=mContext;
        userPhoneList=mUserPhoneList;
        userHistoryDB=mUserHistoryDB;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_history,parent,false);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new MyViewHolder(view);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView contentTV;
        private ImageView deleteIV;
        public MyViewHolder(View itemView) {
            super(itemView);
            contentTV= (TextView) itemView.findViewById(R.id.contentTV);
            deleteIV= (ImageView) itemView.findViewById(R.id.deleteIV);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder= (MyViewHolder) holder;
        myViewHolder.contentTV.setText(userPhoneList.get(position));
        myViewHolder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userHistoryDB.deleteUserHistoryByContent(userPhoneList.get(position));
                notifyItemRemoved(position);
                userPhoneList.remove(position);
                notifyItemRangeChanged(position, getItemCount());//通知position之后的数据刷新
                if(userPhoneList.size()==0){
                    userPhoneList.add("暂无任何登录记录");
                }
            }
        });

        myViewHolder.contentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"暂无任何登录记录".equals(userPhoneList.get(position)))
                     checkUser(userPhoneList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userPhoneList.size();
    }

    //定义一个方法供外部调用
    public void checkUser(String phone){

    }
}
