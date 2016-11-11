package com.jionglemo.jionglemo_b.BasePacket;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jionglemo.jionglemo_b.Chat.ChatContext;
import com.jionglemo.jionglemo_b.Chat.ContactsProvider;
import com.jionglemo.jionglemo_b.Chat.EnmotionGifProvider;
import com.jionglemo.jionglemo_b.Chat.EnmotionMessage;
import com.jionglemo.jionglemo_b.Chat.EnmotionMessageTemplate;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.LiveKit;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.LocationInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.message.LocationMessage;

/**
 * Created by Mike on 2016/6/16.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {

        super.onCreate();

        /**
         *
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
            if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
                ChatContext.init(this);
                RongIM.registerMessageType(EnmotionMessage.class);//注册自定义消息
                RongIM.getInstance().registerMessageTemplate(new EnmotionMessageTemplate());//注册消息模板
            }
        }

        //直播聊天室
        context = this;
        LiveKit.init(this,"0vnjpoadndptz");//这一步需放在此处，否则无法收到消息
        connectRongYun();
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    //融云连接和设置
    private void connectRongYun(){
        String token= CommonValue.getRongToken(this);
        if(token!=null)//如果本地token存在，则进行连接
            connect(token);

        /**
         * 设置用户信息的提供者，供 RongIM 调用获取用户名称和头像信息。
         *
         * @param userInfoProvider 用户信息提供者。
         * @param isCacheUserInfo  设置是否由 IMKit 来缓存用户信息。<br>
         *                         如果 App 提供的 UserInfoProvider
         *                         每次都需要通过网络请求用户数据，而不是将用户数据缓存到本地内存，会影响用户信息的加载速度；<br>
         *                         此时最好将本参数设置为 true，由 IMKit 将用户信息缓存到本地内存中。
         * @see UserInfoProvider
         */

        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String userId) {
                if(userId.equals(CommonValue.getJid(context)+""))
                    return new UserInfo(String.valueOf(CommonValue.getJid(context)),CommonValue.getUserName(context), Uri.parse(CommonValue.serverBasePath+CommonValue.getPortraitPath(context)));
                else
                    return findUserById(userId);//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
            }
        }, true);


        //扩展功能自定义
        InputProvider.ExtendProvider[] provider = {
                new ImageInputProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
                new LocationInputProvider(RongContext.getInstance()),//地理位置
                //RongContext.getInstance().getVoIPInputProvider(),//语音通话
                new ContactsProvider(RongContext.getInstance()),//自定义通讯录
                new EnmotionGifProvider(RongContext.getInstance())//自定义动态表情
        };
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);


        /**
         * 设置位置信息的提供者。
         *
         * @param locationProvider 位置信息提供者。
         */
        RongIM.setLocationProvider(new RongIM.LocationProvider() {

            @Override
            public void onStartLocation(Context context, LocationCallback locationCallback) {
                //在这里打开你的地图页面,保存 locationCallback 对象。
                Uri uri = Uri.parse("http://api.map.baidu.com/staticimage?center=116.403874,39.914889&width=400&height=300&zoom=11&markers=116.383177,39.923743&markerStyles=m,A");
                LocationMessage locationMessage = LocationMessage.obtain(116.403874, 39.914888, "中国北京", uri);
                //如果地图地位成功，那么调用
                locationCallback.onSuccess(locationMessage);
                //如果地图地位失败，那么调用
                locationCallback.onFailure("定位失败!");
            }
        });
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建与服务的连接
             */

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {

                    Log.d("guodingyuan", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("guodingyuan", "--onSuccess" + userid);
                }
                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.d("guodingyuan", "--onError" + errorCode);
                }
            });
        }
    }

    private UserInfo findUserById(final String userId){
        GetNetData getCartUserInfo=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    UserInfo userInfo=new UserInfo(userId, response.getString("user_name"),  Uri.parse(CommonValue.serverBasePath+response.getString("portrait")));
                    /**
                     * 刷新用户缓存数据。
                     *
                     * @param userInfo 需要更新的用户缓存数据。
                     */
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("jid",userId);
        getCartUserInfo.getData(params,CommonValue.getChatUserInfo);
        return null;
    }
}
