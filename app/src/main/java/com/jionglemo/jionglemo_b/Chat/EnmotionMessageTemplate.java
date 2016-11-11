package com.jionglemo.jionglemo_b.Chat;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jionglemo.jionglemo_b.R;

import java.io.IOException;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


@ProviderTag(messageContent = EnmotionMessage.class)
public class EnmotionMessageTemplate extends IContainerItemProvider.MessageProvider<EnmotionMessage> {
    private class ViewHolder {
        GifImageView gifIV;
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.enmotion_message, null);
        ViewHolder holder = new ViewHolder();
        holder.gifIV = (GifImageView) view.findViewById(R.id.gifIV);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, int i, EnmotionMessage enmotionMessage, UIMessage uiMessage) {

        Message msg = uiMessage.getMessage();
        EnmotionMessage msgContent = (EnmotionMessage) msg.getContent();
        ViewHolder holder = (ViewHolder) view.getTag();
        try {
            GifDrawable gifFromAssets = new GifDrawable(view.getContext().getResources(), EnmojiGifAdapter.enmotionGif[msgContent.getPosition()]);
            holder.gifIV.setImageDrawable(gifFromAssets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Spannable getContentSummary(EnmotionMessage enmotionMessage) {
        return new SpannableString("囧了么专用表情");
    }

    @Override
    public void onItemClick(View view, int i, EnmotionMessage enmotionMessage, UIMessage uiMessage) {

    }

    @Override
    public void onItemLongClick(View view, int i, EnmotionMessage enmotionMessage, UIMessage uiMessage) {

    }

}
