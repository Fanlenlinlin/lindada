package com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.ui.message;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.jionglemo.jionglemo_b.R;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.controller.EmojiManager;

import io.rong.imlib.model.MessageContent;

public class GiftMsgView extends BaseMsgView {

    private TextView msg_gift;

    public GiftMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_gift_view, this);
        msg_gift = (TextView) view.findViewById(R.id.msg_gift);
    }

    @Override
    public void setContent(MessageContent msgContent) {
        GiftMessage msg = (GiftMessage) msgContent;
        SpannableString ss = new SpannableString(msg.getUserInfo().getName() + " 送给"+ EmojiManager.parse(msg.getType(),msg_gift.getTextSize())+EmojiManager.parse(msg.getContent(), msg_gift.getTextSize()));
        ss.setSpan(new ForegroundColorSpan(Color.rgb(255,0,135)), 0, msg.getUserInfo().getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.rgb(239,156,0)), msg.getUserInfo().getName().length()+3, msg.getUserInfo().getName().length()+3+EmojiManager.parse(msg.getType(),msg_gift.getTextSize()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msg_gift.setText(ss);
    }
}
