package com.jionglemo.jionglemo_b.Chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.BasePacket.MyApplication;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by Bob on 15/8/18.
 * 会话页面
 */
public class ConversationActivity extends FragmentActivity {

    private TextView titleTV;

    private String mTargetId;

    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        titleTV= (TextView) findViewById(R.id.titleTV);
        Intent intent = getIntent();
        getIntentDate(intent);
        isReconnect(intent);
    }

    //返回
    public void back(View view){
        finish();
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {

        mTargetId = intent.getData().getQueryParameter("targetId");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        enterFragment(mConversationType, mTargetId);
        setActionBarTitle(mTargetId);
    }


    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }


    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect(Intent intent) {

        String token = null;

        if (ChatContext.getInstance() != null) {

            token = ChatContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "default");
        }

        //push或通知过来
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push").equals("true")) {

                reconnect(token);
            } else {
                //程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {

                    reconnect(token);
                } else {
                    enterFragment(mConversationType, mTargetId);
                }
            }
        }
    }



    /**
     * 设置 actionbar title
     */
    private void setActionBarTitle(String targetid) {
        GetNetData getCartUserInfo=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    titleTV.setText(response.getString("user_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("jid",targetid);
        getCartUserInfo.getData(params, CommonValue.getChatUserInfo);
    }

    /**
     * 重连
     *
     * @param token
     */
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {

                    enterFragment(mConversationType, mTargetId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }
}
