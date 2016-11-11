package com.jionglemo.jionglemo_b.Chat;

import android.content.Context;
import android.util.Log;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Created by Mike on 2016/7/21.
 */
public class NotificationReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        Log.i("guodingyuan","进来1");
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
      /*  Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri.Builder builder = Uri.parse("rong://" + context.getPackageName()).buildUpon();

        builder.appendPath("conversation")
               .appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
               .appendQueryParameter("targetId", "")//直接传入空的，会自己匹配的
               .appendQueryParameter("title", "");//直接传入空的，会自己匹配的
        Uri uri = builder.build();
        Log.i("guodingyuan",uri.toString());
        intent.setData(uri);
        context.startActivity(intent);*/
        //上述步骤不用重复了
        Log.i("guodingyuan","进来2");
        return false;
    }
}
