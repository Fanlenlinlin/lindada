package com.jionglemo.jionglemo_b.ZhiBo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mike on 2016/9/13.
 */
public class JiongBiTopRVadapter extends RecyclerView.Adapter{

    private Context context;
    private List<JiongBiTop> jiongBiTopList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public JiongBiTopRVadapter(Context context,  List<JiongBiTop> jiongBiTopList){
        this.context=context;
        this.jiongBiTopList=jiongBiTopList;
        imageLoader = ImageLoaderArgument.getInstance(context);//全局初始化此配置
        options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
    }

    private class JiongBiTopViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView photoIV;
        private TextView user_nameTV;
        private TextView priceTV;
        private TextView numberTV;
        public JiongBiTopViewHolder(View itemView) {
            super(itemView);
            photoIV= (CircleImageView) itemView.findViewById(R.id.photoIV);
            user_nameTV= (TextView) itemView.findViewById(R.id.user_nameTV);
            priceTV= (TextView) itemView.findViewById(R.id.priceTV);
            numberTV= (TextView) itemView.findViewById(R.id.numberTV);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.jiongbitop_item, parent,false);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new JiongBiTopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        JiongBiTopViewHolder jiongBiRecordViewHolder= (JiongBiTopViewHolder) holder;
        imageLoader.displayImage(CommonValue.serverBasePath+jiongBiTopList.get(position).getPortrait(),jiongBiRecordViewHolder.photoIV,options);
        jiongBiRecordViewHolder.user_nameTV.setText(jiongBiTopList.get(position).getUser_name());
        jiongBiRecordViewHolder.priceTV.setText("贡献"+jiongBiTopList.get(position).getPrice()+"囧币");
        jiongBiRecordViewHolder.numberTV.setText("NO."+(position+2));
    }

    @Override
    public int getItemCount() {
        return jiongBiTopList.size();
    }
}
