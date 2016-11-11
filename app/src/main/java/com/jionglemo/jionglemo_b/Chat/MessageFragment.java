package com.jionglemo.jionglemo_b.Chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jionglemo.jionglemo_b.BasePacket.MyApplication;
import com.jionglemo.jionglemo_b.R;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MessageFragment extends Fragment {

    private View rootView;//作为内部变量，用于与ViewPager结合优化内存

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_message, container, false);
            isReconnect();
        }
        return rootView;
    }

    /**
     *  NETWORK_UNAVAILABLE(-1, "Network is unavailable."),
     *  CONNECTED(0, "Connect Success."),
     *  CONNECTING(1, "Connecting"),
     *  DISCONNECTED(2, "Disconnected"),
     *  KICKED_OFFLINE_BY_OTHER_CLIENT(3, "Login on the other device, and be kicked offline."),
     *  TOKEN_INCORRECT(4, "Token incorrect."),
     *  SERVER_INVALID(5, "Server invalid.");
     */
  /*  @Override

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            Log.i("guodingyuan","可见"+RongIMClient.getInstance().getCurrentConnectionStatus());
            if(RongIMClient.getInstance().getCurrentConnectionStatus()==RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED){
                //如果状态为未连接时，进行重新连接
                reconnect(CommonValue.getRongToken(getActivity()));
            }
        }
    }
*/

    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect() {

        Intent intent = getActivity().getIntent();
        String token = null;

        if (ChatContext.getInstance() != null) {
            token = ChatContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "default");
        }
        //push，通知或新消息过来
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
                    enterFragment();
                }
            }
        }else {
            enterFragment();
        }
    }

    /**
     * 重连
     *
     * @param token
     */
    private void reconnect(String token) {

        if (getActivity().getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getActivity().getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {

                    enterFragment();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    /**
     * 加载 会话列表 ConversationListFragment
     */
    private void enterFragment() {
        ConversationListFragment fragment = (ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();

        fragment.setUri(uri);
    }
}
