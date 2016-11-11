package com.jionglemo.jionglemo_b.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jionglemo.jionglemo_b.R;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by Mike on 2016/7/25.
 */
public class EnmojiGifAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private String targetID;
    private Conversation.ConversationType type;
    public final static int [] enmotionGif={R.drawable.jiong0, R.drawable.jiong1,R.drawable.jiong2,R.drawable.jiong3,R.drawable.jiong4,
                                            R.drawable.jiong5,R.drawable.jiong6,R.drawable.jiong7};

    public EnmojiGifAdapter(Context context, String targetID, Conversation.ConversationType type) {
        this.mContext = context;
        this.targetID=targetID;
        this.type=type;
    }

    private class GifViewHolder extends RecyclerView.ViewHolder{

        private ImageView enmotionIV;
        public GifViewHolder(View itemView) {
            super(itemView);
            enmotionIV= (ImageView) itemView.findViewById(R.id.enmotionIV);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.enmotion_item, parent,false);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new GifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        GifViewHolder gifViewHolder= (GifViewHolder) holder;
        gifViewHolder.enmotionIV.setImageResource(enmotionGif[position]);
        gifViewHolder.enmotionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = Message.obtain(targetID,type, new EnmotionMessage(position));
                RongIMClient.SendMessageCallback callback = new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {
                    }

                    @Override
                    public void onError(Integer messageId, RongIMClient.ErrorCode e) {

                    }
                };
                RongIMClient.ResultCallback<Message> result = new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                };
                RongIM.getInstance().getRongIMClient().sendMessage(message, null, null, callback, result);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return enmotionGif.length;
    }
}

