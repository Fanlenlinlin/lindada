package com.jionglemo.jionglemo_b.Chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Bob on 15/8/21.
 */
public class ChatContext {

    private static ChatContext mChatContext;
    public Context mContext;
    private SharedPreferences mPreferences;

    public static ChatContext getInstance() {

        if (mChatContext == null) {
            mChatContext = new ChatContext();
        }
        return mChatContext;
    }

    private ChatContext() {
    }

    private ChatContext(Context context) {
        mContext = context;
        mChatContext = this;
        //http初始化 用于登录、注册使用
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }


    public static void init(Context context) {
        mChatContext = new ChatContext(context);
    }

    public SharedPreferences getSharedPreferences() {
        return mPreferences;
    }

}
