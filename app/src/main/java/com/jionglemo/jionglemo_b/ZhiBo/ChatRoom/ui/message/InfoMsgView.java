package com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.ui.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.R;

import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;

public class InfoMsgView extends BaseMsgView {

    private TextView infoText;

    public InfoMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_info_view, this);
        infoText = (TextView) view.findViewById(R.id.info_text);
    }

    @Override
    public void setContent(MessageContent msgContent) {
        InformationNotificationMessage msg = (InformationNotificationMessage) msgContent;
        String userName=msg.getUserInfo().getName();
        String content=msg.getMessage().substring(userName.length()+1, msg.getMessage().length());
        if("#￥%&+-*#￥%&+-*".equals(content)){
            content="结束了直播";
            Intent mIntent = new Intent("直播结束");
            //发送广播
            getContext().sendBroadcast(mIntent);
        }
        SpannableString ss = new SpannableString(userName+ " "+content);
        ss.setSpan(new ForegroundColorSpan(Color.rgb(239,156,0)), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        infoText.setText(ss);
    }
}
