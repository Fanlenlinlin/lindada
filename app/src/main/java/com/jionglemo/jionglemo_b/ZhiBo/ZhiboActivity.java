package com.jionglemo.jionglemo_b.ZhiBo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.ImageLoaderArgument;
import com.jionglemo.jionglemo_b.CommonTool.PictureTool;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.CommonView.SharePopupWindow;
import com.jionglemo.jionglemo_b.R;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.LiveKit;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.controller.ChatListAdapter;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.ui.message.GiftMessage;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.ui.widget.ChatListView;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.ui.widget.InputPanel;
import com.ksy.recordlib.service.core.KSYStreamer;
import com.ksy.recordlib.service.core.KSYStreamerConfig;
import com.ksy.recordlib.service.stats.OnLogEventListener;
import com.ksy.recordlib.service.stats.StreamStatusEventHandler;
import com.ksy.recordlib.service.streamer.OnStatusListener;
import com.ksy.recordlib.service.streamer.RecorderConstants;
import com.ksy.recordlib.service.util.audio.OnAudioRawDataListener;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ZhiboActivity extends AppCompatActivity implements Handler.Callback {

    private String TAG="mike";

    private TextView attentionNumTV;
    private LinearLayout bottomBarLL;
    private InputPanel input_panel;
    private ChatListView chatListView;
    private ChatListAdapter chatListAdapter;
    private Handler handler = new Handler(this);

    private GLSurfaceView mCameraPreview;
    private KSYStreamer mStreamer;
    private volatile boolean mAcitivityResumed = false;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String mUrl;
    private Handler mHandler;
    private Chronometer chronometer;
    private ToggleButton bellToggleButton;
    private LinearLayout prepareZhiboLL1;
    private LinearLayout prepareZhiboLL2;
    private LinearLayout zhibo_topLL;
    private LinearLayout zhibo_bottomLL;
    private TextView countSecondTV;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private TextView store_nameTV0;
    private TextView sales_nameTV0;
    private CircleImageView photoIV;
    private TextView sales_nameTV;
    private TextView store_nameTV;
    private TextView onLinePeopleTV;
    private RecyclerView zhiboPeopleRV;

    private ConnectionChangeReceiver connectionChangeReceiver;
    private boolean zhiboAction=false;//是否开始直播
    private long bellfirstTime;

    public static int zhiboPersonPage;//直播头像当前页数
    public static int zhiboPersonTotalPage;//直播头像总页数
    private LinearLayoutManager daogouStarLM;
    private ZhiboPersonRVadapter zhiboPersonRVadapter;
    private List<ZhiboPenson> zhiboPersonList;
    private int zhiboPersonlastPosition;
    private boolean zhiboPersonlock=true;//为避免多次快速滑动到底部而进行重复加载，用一个锁锁住

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zhibo);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕保持常亮

        store_nameTV0 = (TextView) findViewById(R.id.store_nameTV0);
        sales_nameTV0 = (TextView) findViewById(R.id.sales_nameTV0);

        photoIV = (CircleImageView) findViewById(R.id.photoIV);
        sales_nameTV = (TextView) findViewById(R.id.sales_nameTV);
        store_nameTV = (TextView) findViewById(R.id.store_nameTV);
        imageLoader = ImageLoaderArgument.getInstance(this);
        options = ImageLoaderArgument.getDisplayImageOptions(R.color.grey);
        attentionNumTV = (TextView) findViewById(R.id.attentionNumTV);
        onLinePeopleTV = (TextView) findViewById(R.id.onLinePeopleTV);

        zhiboPeopleRV = (RecyclerView) findViewById(R.id.zhiboPeopleRV);

        LiveKit.setOnReceiveMessageListener();//设置消息接收的监听
        UserInfo user=new UserInfo(String.valueOf(CommonValue.getJid(ZhiboActivity.this)),CommonValue.getUserName(ZhiboActivity.this), Uri.parse(CommonValue.serverBasePath+CommonValue.getPortraitPath(ZhiboActivity.this)));
        LiveKit.setCurrentUser(user);
        LiveKit.addEventHandler(handler);
        //加入聊天室
        LiveKit.joinChatRoom(String.valueOf(CommonValue.getJid(this)), -1, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                InformationNotificationMessage content = InformationNotificationMessage.obtain(CommonValue.getUserName(ZhiboActivity.this)+" 进入直播间");
                LiveKit.sendMessage(content);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                //在加入聊天室失败后，进行一次重新连接
                RongIM.connect(CommonValue.getRongToken(ZhiboActivity.this), new RongIMClient.ConnectCallback() {

                    @Override
                    public void onSuccess(String s) {
                        LiveKit.joinChatRoom(String.valueOf(CommonValue.getJid(ZhiboActivity.this)),-1, new RongIMClient.OperationCallback(){

                            @Override
                            public void onSuccess() {
                                InformationNotificationMessage content = InformationNotificationMessage.obtain(CommonValue.getUserName(ZhiboActivity.this)+" 进入直播间");
                                LiveKit.sendMessage(content);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Toast.makeText(ZhiboActivity.this, "聊天室加入失败!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onTokenIncorrect() {

                    }
                });
            }
        });

        bottomBarLL = (LinearLayout) findViewById(R.id.bottomBarLL);
        input_panel = (InputPanel) findViewById(R.id.input_panel);
        chatListView = (ChatListView) findViewById(R.id.chat_listview);
        chatListAdapter = new ChatListAdapter();
        chatListView.setAdapter(chatListAdapter);
        input_panel.setPanelListener(new InputPanel.InputPanelListener() {
            @Override
            public void onSendClick(String text) {
                final TextMessage content = TextMessage.obtain(text);
                LiveKit.sendMessage(content);
            }
        });

        chatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    cancleInputPanel();
                return false;
            }
        });


        /**
         * 直播的相关逻辑
         */
        prepareZhiboLL1 = (LinearLayout) findViewById(R.id.prepareZhiboLL1);
        prepareZhiboLL2 = (LinearLayout) findViewById(R.id.prepareZhiboLL2);
        zhibo_topLL = (LinearLayout) findViewById(R.id.zhibo_topLL);
        zhibo_bottomLL = (LinearLayout) findViewById(R.id.zhibo_bottomLL);
        countSecondTV = (TextView) findViewById(R.id.countSecondTV);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        bellToggleButton = (ToggleButton) findViewById(R.id.bellToggleButton);

        bellToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    Toast.makeText(ZhiboActivity.this,"提示音已开启",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ZhiboActivity.this,"提示音已关闭",Toast.LENGTH_SHORT).show();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null && msg.obj != null) {
                    String content = msg.obj.toString();
                    switch (msg.what) {
                        case RecorderConstants.KSYVIDEO_ENCODED_FRAMES_FAILED:
                        case RecorderConstants.KSYVIDEO_CODEC_OPEN_FAILED:
                        case RecorderConstants.KSYVIDEO_CONNECT_FAILED:
                        case RecorderConstants.KSYVIDEO_DNS_PARSE_FAILED:
                        case RecorderConstants.KSYVIDEO_RTMP_PUBLISH_FAILED:
                        case RecorderConstants.KSYVIDEO_CONNECT_BREAK:
                        case RecorderConstants.KSYVIDEO_AUDIO_INIT_FAILED:
                        case RecorderConstants.KSYVIDEO_OPEN_CAMERA_FAIL://调取照相机失败
                            Log.i(TAG,"调取照相机失败");
                            break;
                        case RecorderConstants.KSYVIDEO_CAMERA_PARAMS_ERROR:
                        case RecorderConstants.KSYVIDEO_AUDIO_START_FAILED:
                            //Toast.makeText(ZhiboActivity.this, content, Toast.LENGTH_LONG).show();
                            chronometer.stop();
                            break;
                        case RecorderConstants.KSYVIDEO_OPEN_STREAM_SUCC:
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // 开始计时
                            chronometer.start();
                            break;
                        case RecorderConstants.KSYVIDEO_INIT_DONE:
                            //Toast.makeText(getApplicationContext(), "初始化完成", Toast.LENGTH_SHORT).show();
                            //checkPermission();
                            //mStreamer.startStream();初始化完成后执行这一步将自动直播
                            break;
                        default:
                            //Toast.makeText(ZhiboActivity.this, content, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        };


        FrameLayout zongFL= (FrameLayout) findViewById(R.id.zongFL);
        zongFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleInputPanel();
            }
        });

        mCameraPreview = (GLSurfaceView) findViewById(R.id.camera_preview);
        //可以在这里做权限检查,若没有audio和camera权限,进一步引导用户做权限设置
        checkPermission();
        mStreamer = new KSYStreamer(ZhiboActivity.this);
        KSYStreamerConfig.Builder builder = new KSYStreamerConfig.Builder();
        builder.setmUrl(mUrl);
        builder.setFrameRate(15);//15,20
        //int videoBitrate=800;
        //设置最高码率，即目标码率
        builder.setMaxAverageVideoBitrate(800);//1500
        //设置最低码率
        builder.setMinAverageVideoBitrate(200);//100,400
        //builder.setMinAverageVideoBitrate(videoBitrate * 2 / 8);
        //设置初始码率
        builder.setInitAverageVideoBitrate(500);//800
        //builder.setInitAverageVideoBitrate(videoBitrate * 5 / 8);
        builder.setAudioBitrate(48);//32
        builder.setVideoResolution(RecorderConstants.VIDEO_RESOLUTION_480P);//360P
        builder.setEncodeMethod(KSYStreamerConfig.ENCODE_METHOD.SOFTWARE);
        builder.setSampleAudioRateInHz(44100);
        //builder.setEnableStreamStatModule(true);
        builder.setDefaultLandscape(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        builder.setFrontCameraMirror(false);
        builder.setManualFocus(false);
        builder.setIsSlightBeauty(false);
        mStreamer.setConfig(builder.build());
        mStreamer.setDisplayPreview(mCameraPreview);

        //老的状态回调机制
        mStreamer.setOnStatusListener(mOnErrorListener);

        //新的状态回调机制
        StreamStatusEventHandler.getInstance().addOnStatusErrorListener(mOnStatusErrorListener);
        StreamStatusEventHandler.getInstance().addOnStatusInfoListener(mOnStatusInfoListener);

        mStreamer.setOnLogListener(mOnLogListener);
        mStreamer.setOnAudioRawDataListener(mOnAudioRawDataListener);
        mStreamer.enableDebugLog(true);
        mStreamer.setMuteAudio(false);//设置静音
        mStreamer.setEnableEarMirror(false);//设置耳反
        mStreamer.setBeautyFilter(RecorderConstants.FILTER_BEAUTY_DENOISE);//设置美颜
        getZhiBoUrl();//获取直播地址
        getZhiboNumAndList(); //获取直播在线人数和直播用户列表

        giftHandler.post(checkGiftListRunnable);//启动检查礼物列表的线程

        //注册监听，用于监听网络状态
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        connectionChangeReceiver = new ConnectionChangeReceiver();
        registerReceiver(connectionChangeReceiver, filter);

        /**
         * 设置连接状态变化的监听器.
         */
        RongIMClient.setConnectionStatusListener(new MyConnectionStatusListener());
    }


    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {

        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus){
                case CONNECTED://连接成功。
                    break;
                case DISCONNECTED://断开连接。
                    break;
                case CONNECTING://连接中。
                    break;
                case NETWORK_UNAVAILABLE://网络不可用。
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                    Log.i("guodingyuan","被踢掉线");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(ZhiboActivity.this)
                                    .setCancelable(false)
                                    .setTitle("注意")
                                    .setMessage("        您当前账号已经在其他设备登录，必须退出当前直播后，重新开启直播，否则您将无法接收到直播间的任何用户消息！")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            finishZhibo();//结束直播调用接口
                                            mStreamer.stopStream(true);
                                            chronometer.stop();
                                            finish();
                                            //重新连接融云，通知大家直播已结束
                                            RongIM.connect(CommonValue.getRongToken(ZhiboActivity.this), new RongIMClient.ConnectCallback() {
                                                @Override
                                                public void onSuccess(String s) {
                                                    LiveKit.joinChatRoom(String.valueOf(CommonValue.getJid(ZhiboActivity.this)),-1, new RongIMClient.OperationCallback(){

                                                        @Override
                                                        public void onSuccess() {
                                                            //发出一条消息，通知大家说直播已结束
                                                            InformationNotificationMessage content = InformationNotificationMessage.obtain(CommonValue.getUserName(ZhiboActivity.this)+" #￥%&+-*#￥%&+-*");
                                                            LiveKit.sendMessage(content);
                                                        }

                                                        @Override
                                                        public void onError(RongIMClient.ErrorCode errorCode) {
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(RongIMClient.ErrorCode errorCode) {

                                                }

                                                @Override
                                                public void onTokenIncorrect() {

                                                }
                                            });
                                        }
                                    }).show();
                        }
                    });
                    break;
            }
        }
    }

    private AlertDialog mobNetDialog;
    private AlertDialog noneNetDialog;

    //使用一个监听来监听网络状态的变化
    private class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!wifiNetInfo.isConnected()) {//如果WIFI不可用
                if(mobNetInfo.isConnected()){//如果使用流量连接
                    if(mobNetDialog==null) {
                        mobNetDialog = new AlertDialog.Builder(ZhiboActivity.this)
                                .setCancelable(false)
                                .setTitle("注意")
                                .setMessage("您目前正处于非WIFI环境，是否继续观看直播？")
                                .setNegativeButton("退出", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        finishZhibo();//结束直播调用接口
                                        //发出一条消息，通知大家说直播已结束
                                        InformationNotificationMessage content = InformationNotificationMessage.obtain(CommonValue.getUserName(ZhiboActivity.this)+" #￥%&+-*#￥%&+-*");
                                        LiveKit.sendMessage(content);
                                        finish();
                                    }
                                })
                                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        if (zhiboAction) {
                                            mStreamer.onResume();
                                        }
                                    }
                                }).show();
                    }else {
                        if(!mobNetDialog.isShowing())
                            mobNetDialog.show();
                    }
                    if(noneNetDialog!=null&&noneNetDialog.isShowing())
                        noneNetDialog.dismiss();
                }else{//如果WIFI和流量都不可用
                    if(zhiboAction){
                        mStreamer.onPause();
                        if(noneNetDialog==null){
                            noneNetDialog=new AlertDialog.Builder(ZhiboActivity.this)
                                    .setCancelable(false)
                                    .setTitle("注意")
                                    .setMessage("当前无可用网络！")
                                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            finish();
                                        }
                                    })
                                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                            startActivity(intent);
                                        }
                                    }).show();
                        }else {
                            if(!noneNetDialog.isShowing())
                                noneNetDialog.show();
                        }
                    }else {
                        finish();
                        Toast.makeText(ZhiboActivity.this,"无可用网络，请设置后重连",Toast.LENGTH_LONG).show();
                    }
                    if(mobNetDialog!=null&&mobNetDialog.isShowing())
                        mobNetDialog.dismiss();
                }
            }else {//如果WIFI可用
                if(zhiboAction){
                    mStreamer.onResume();
                    Log.i("guodingyuan","WIFI——OK");
                }
                if(noneNetDialog!=null&&noneNetDialog.isShowing())
                    noneNetDialog.dismiss();
                if(mobNetDialog!=null&&mobNetDialog.isShowing())
                    mobNetDialog.dismiss();
            }
        }
    }

    //获取直播在线人数和直播用户列表
    private void getZhiboNumAndList(){
        onLinePeopleTV.post(onLinePeopleNumRunnable);
        GetNetData getNetData=new GetNetData(this){

            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    zhiboPersonList =JSON.parseArray(response.getJSONArray("data_list").toString(),ZhiboPenson.class);
                    daogouStarLM=new LinearLayoutManager(ZhiboActivity.this);
                    daogouStarLM.setOrientation(LinearLayoutManager.HORIZONTAL);
                    zhiboPeopleRV.setLayoutManager(daogouStarLM);
                    zhiboPersonRVadapter =new ZhiboPersonRVadapter(ZhiboActivity.this,zhiboPersonList);
                    zhiboPeopleRV.setAdapter(zhiboPersonRVadapter);

                    JSONObject pageJSON=response.getJSONObject("page");
                    zhiboPersonPage = pageJSON.getInt("p");
                    zhiboPersonTotalPage = pageJSON.getInt("total_page");

                    zhiboPeopleRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            //滑到最后一条再向下滑动时重新获取数据，加载完毕后就不进行加载
                            if (newState == RecyclerView.SCROLL_STATE_IDLE && zhiboPersonlastPosition == zhiboPersonList.size() && zhiboPersonPage<zhiboPersonTotalPage) {
                                if(zhiboPersonlock){
                                    zhiboPersonlock=false;//锁住
                                    getMoreZhiboPerson();
                                }
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            zhiboPersonlastPosition = daogouStarLM.findLastVisibleItemPosition();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        getNetData.getData(null,CommonValue.getUserList);
    }

    //获取更多直播头像
    private void getMoreZhiboPerson(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    List<ZhiboPenson> list= JSON.parseArray(response.getJSONArray("data_list").toString(),ZhiboPenson.class);
                    zhiboPersonList.addAll(list);
                    zhiboPersonRVadapter.notifyDataSetChanged();

                    JSONObject pageJSON=response.getJSONObject("page");
                    zhiboPersonPage = pageJSON.getInt("p");
                    zhiboPersonTotalPage = pageJSON.getInt("total_page");

                    zhiboPersonlock=true;//解锁
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getDataFailure() {
                super.getDataFailure();
                if(zhiboPersonRVadapter !=null){
                    zhiboPersonRVadapter.loadError = true;
                    zhiboPersonRVadapter.notifyItemChanged(zhiboPersonRVadapter.getItemCount() - 1);//更新最后一条数据的状态
                    zhiboPersonlock=true;//解锁
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("p", zhiboPersonPage +1);
        getNetData.getData(params, CommonValue.getUserList);
    }

    //用于每30秒获取一次在线人数
    private Runnable onLinePeopleNumRunnable=new Runnable() {
        @Override
        public void run() {
            GetNetData getNetData=new GetNetData(ZhiboActivity.this){
                @Override
                public void getDataSuccess(JSONObject response) {
                    super.getDataSuccess(response);
                    try {
                        onLinePeopleTV.setText(response.getInt("message")+"人");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestParams params=new RequestParams();
            params.put("jid",CommonValue.getJid(ZhiboActivity.this));
            getNetData.getData(params,CommonValue.liveNum);
            onLinePeopleTV.postDelayed(this,30000);//每30秒获取一次在线人数
        }
    };

    //获取直播地址
    private void getZhiBoUrl(){
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    mUrl=response.getString("live_url");
                    mStreamer.updateUrl(mUrl);//因为异步请求，需在获取到直播地址后更新URL
                    imageLoader.displayImage(CommonValue.serverBasePath+response.getString("portrait"),photoIV,options);
                    store_nameTV.setText(response.getString("store_name"));
                    attentionNumTV.setText(response.getString("notice"));
                    store_nameTV0.setText(response.getString("store_name"));
                    if(CommonValue.getStype(ZhiboActivity.this)==2){
                        sales_nameTV.setText(response.getString("sales_name"));
                        sales_nameTV0.setText(response.getString("sales_name"));
                    }else {
                        sales_nameTV.setText(response.getString("room_name"));
                        sales_nameTV0.setText(response.getString("room_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("room_name",CommonValue.getJid(this));
        getNetData.getData(params,CommonValue.getLivePath);
    }

    @Override
    protected void onDestroy() {
        onLinePeopleTV.removeCallbacks(onLinePeopleNumRunnable);//销毁更新在线人数的线程
        giftHandler.removeCallbacks(checkGiftListRunnable);//销毁检查礼物列表的线程
        unregisterReceiver(connectionChangeReceiver);//销毁网络状态的监听
        LiveKit.removeEventHandler(handler);
        LiveKit.quitChatRoom(new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
        super.onDestroy();

        //新的状态回调机制
        StreamStatusEventHandler.getInstance().removeStatusErrorListener(mOnStatusErrorListener);
        StreamStatusEventHandler.getInstance().removeStatusInfoListener(mOnStatusInfoListener);

        mStreamer.onDestroy();
        executorService.shutdownNow();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }


    @Override
    public boolean handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case LiveKit.MESSAGE_ARRIVED: {
                MessageContent content = (MessageContent) msg.obj;
                chatListAdapter.addMessage(content);
                //有用户进入后响起铃声
                if(content instanceof InformationNotificationMessage){
                    if(bellToggleButton.isChecked()){
                        //如果间隔超过2秒的话响铃
                        if(System.currentTimeMillis()-bellfirstTime>2000){
                            bellfirstTime=System.currentTimeMillis();
                            SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
                            soundPool.load(ZhiboActivity.this,R.raw.bell,1);
                            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                                @Override
                                public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                                    soundPool.play(1,  //声音id
                                            2, //左声道
                                            2, //右声道
                                            1, //优先级
                                            0, // 0表示不循环，-1表示循环播放
                                            1);//播放比率，0.5~2，一般为1
                                }
                            });
                        }
                    }
                }
                if(content instanceof GiftMessage)
                    try {
                        GiftMessage giftMessage= (GiftMessage) content;
                        String giftArray[]=giftMessage.getContent().split("囧币");
                        String giftName=giftArray[1];
                        ZhiboGift zhiboGift=new ZhiboGift(content.getJSONUserInfo().getInt("id"),content.getJSONUserInfo().getString("name"),content.getJSONUserInfo().getString("portrait"),giftName);
                        switch (giftName){
                            case "爱心":
                                showHeartGiftAnim(zhiboGift);
                                break;
                            case "烟花":
                                showYanHuaGiftAnim(zhiboGift);
                                break;
                            default:
                                showGiftAnim(zhiboGift);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;
            }
            case LiveKit.MESSAGE_SENT: {
                MessageContent content = (MessageContent) msg.obj;
                chatListAdapter.addMessage(content);
                if(content instanceof GiftMessage)
                    try {
                        GiftMessage giftMessage= (GiftMessage) content;
                        String giftArray[]=giftMessage.getContent().split("囧币");
                        String giftName=giftArray[1];
                        ZhiboGift zhiboGift=new ZhiboGift(content.getJSONUserInfo().getInt("id"),content.getJSONUserInfo().getString("name"),content.getJSONUserInfo().getString("portrait"),giftName);
                        switch (giftName){
                            case "爱心":
                                showHeartGiftAnim(zhiboGift);
                                break;
                            case "烟花":
                                showYanHuaGiftAnim(zhiboGift);
                                break;
                            default:
                                showGiftAnim(zhiboGift);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;
            }
            case LiveKit.MESSAGE_SEND_ERROR: {
                break;
            }
            default:
        }
        chatListAdapter.notifyDataSetChanged();
        return false;
    }

    //展示礼物爱心的动画
    private GifImageView heartGifIV;
    private View bigGiftLayOut1;
    private CircleImageView bigGiftPhotoCIV1;
    private TextView bigGiftUserNameTV1;
    private TextView bigGiftTV1;
    private Animation bigGiftShowIn1;
    private int[] heartGiftPicture={R.drawable.heart_0,R.drawable.heart_1,R.drawable.heart_2,R.drawable.heart_3,R.drawable.heart_4,R.drawable.heart_5,R.drawable.heart_6,R.drawable.heart_7,
            R.drawable.heart_8,R.drawable.heart_9,R.drawable.heart_10,R.drawable.heart_11,R.drawable.heart_12,R.drawable.heart_13, R.drawable.heart_14,
            R.drawable.heart_15,R.drawable.heart_16,R.drawable.heart_17,R.drawable.heart_18,R.drawable.heart_19,R.drawable.heart_20, R.drawable.heart_21};
    private int heartPosition=0;
    private List<ZhiboGift> heartGiftList=new ArrayList<>();//使用一个列表临时存储未展现的爱心礼物
    private void showHeartGiftAnim(ZhiboGift zhiboGift){
        if(heartGifIV==null){
            heartGifIV = (GifImageView) findViewById(R.id.heartGifIV);
            //飘出来是送礼物用户
            bigGiftLayOut1=findViewById(R.id.bigGiftLayOut1);
            bigGiftPhotoCIV1= (CircleImageView) bigGiftLayOut1.findViewById(R.id.bigGiftPhotoCIV);
            bigGiftUserNameTV1= (TextView) bigGiftLayOut1.findViewById(R.id.bigGiftUserNameTV);
            bigGiftTV1= (TextView) bigGiftLayOut1.findViewById(R.id.bigGiftTV);
            bigGiftTV1.setText("献爱心");
            bigGiftShowIn1=AnimationUtils.loadAnimation(this,R.anim.show_left_to_right_all_anim);
            bigGiftLayOut1.setVisibility(View.VISIBLE);
        }
        if(heartPosition==0){//说明目前有空位
            imageLoader.displayImage(zhiboGift.getPortrait(),bigGiftPhotoCIV1,options);
            bigGiftUserNameTV1.setText(zhiboGift.getName());
            bigGiftLayOut1.startAnimation(bigGiftShowIn1);
            heartGifIV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(heartPosition<20){
                            heartGifIV.setImageBitmap(PictureTool.readBitMap(ZhiboActivity.this,heartGiftPicture[heartPosition]));
                            heartPosition++;
                            heartGifIV.postDelayed(this,80);
                        }else if(heartPosition==20){
                            heartGifIV.setImageBitmap(PictureTool.readBitMap(ZhiboActivity.this,heartGiftPicture[heartPosition]));
                            heartPosition++;
                            heartGifIV.postDelayed(this,500);
                        }else if(heartPosition==21){
                            GifDrawable gifFromAssets = new GifDrawable(getResources(), heartGiftPicture[heartPosition]);
                            heartGifIV.setImageDrawable(gifFromAssets);
                            heartPosition++;
                            heartGifIV.postDelayed(this,2000);
                        }else {
                            heartGifIV.removeCallbacks(this);
                            heartGifIV.setImageDrawable(null);
                            heartPosition=0;//重新归零
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },80);
        }else {
            heartGiftList.add(zhiboGift);
        }
    }

    //展示礼物烟花的动画
    private ImageView yanhuaIV;
    private ImageView yanhuaIVyellow;
    private ImageView yanhuaIVpurple;
    private ImageView yanhuaIVred;
    private ImageView yanhuaIVgreen;
    private int yanhuaPosition=0;
    private int[] yanhuaGiftPicture={R.drawable.yanhua0,R.drawable.yanhua1,R.drawable.yanhua2,R.drawable.yanhua3,R.drawable.yanhua4,R.drawable.yanhua5,R.drawable.yanhua6,
            R.drawable.yanhua7, R.drawable.yanhua8,R.drawable.yanhua9,R.drawable.yanhua10,R.drawable.yanhua11,R.drawable.yanhua12,R.drawable.yanhua13};
    private List<ZhiboGift> yanhuaGiftList=new ArrayList<>();//使用一个列表临时存储未展现的烟花礼物
    private Animation yanhuaAnimShow1;
    private Animation yanhuaAnimShow2;
    private Animation yanhuaAnimShow3;
    private Animation yanhuaAnimShow4;
    private Animation yanhuaAnimHide;
    private View bigGiftLayOut2;
    private CircleImageView bigGiftPhotoCIV2;
    private TextView bigGiftUserNameTV2;
    private TextView bigGiftTV2;
    private Animation bigGiftShowIn2;
    private void showYanHuaGiftAnim(ZhiboGift zhiboGift){
        if(yanhuaIV==null) {
            yanhuaIV = (ImageView) findViewById(R.id.yanhuaIV);
            yanhuaIVyellow = (ImageView) findViewById(R.id.yanhuaIVyellow);
            yanhuaIVpurple = (ImageView) findViewById(R.id.yanhuaIVpurple);
            yanhuaIVred = (ImageView) findViewById(R.id.yanhuaIVred);
            yanhuaIVgreen = (ImageView) findViewById(R.id.yanhuaIVgreen);
            yanhuaAnimShow1 = AnimationUtils.loadAnimation(this, R.anim.yanhua_show);
            yanhuaAnimShow2 = AnimationUtils.loadAnimation(this, R.anim.yanhua_show);
            yanhuaAnimShow3 = AnimationUtils.loadAnimation(this, R.anim.yanhua_show);
            yanhuaAnimShow4 = AnimationUtils.loadAnimation(this, R.anim.yanhua_show);
            yanhuaAnimHide = AnimationUtils.loadAnimation(this, R.anim.yanhua_hide);
            //飘出来是送礼物用户
            bigGiftLayOut2=findViewById(R.id.bigGiftLayOut2);
            bigGiftPhotoCIV2= (CircleImageView) bigGiftLayOut2.findViewById(R.id.bigGiftPhotoCIV);
            bigGiftUserNameTV2= (TextView) bigGiftLayOut2.findViewById(R.id.bigGiftUserNameTV);
            bigGiftTV2= (TextView) bigGiftLayOut2.findViewById(R.id.bigGiftTV);
            bigGiftTV2.setText("烟花浪漫");
            bigGiftShowIn2=AnimationUtils.loadAnimation(this,R.anim.show_left_to_right_all_anim);
            bigGiftLayOut2.setVisibility(View.VISIBLE);
        }
        if(yanhuaPosition==0){//说明目前有空位
            imageLoader.displayImage(zhiboGift.getPortrait(),bigGiftPhotoCIV2,options);
            bigGiftUserNameTV2.setText(zhiboGift.getName());
            bigGiftLayOut2.startAnimation(bigGiftShowIn2);
            yanhuaIV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(yanhuaPosition<10){
                        yanhuaIV.setImageBitmap(PictureTool.readBitMap(ZhiboActivity.this,yanhuaGiftPicture[yanhuaPosition]));
                        if(yanhuaPosition==6){
                            yanhuaIVyellow.setImageBitmap(PictureTool.readBitMap(ZhiboActivity.this,yanhuaGiftPicture[10]));
                            yanhuaIVyellow.startAnimation(yanhuaAnimShow1);
                            yanhuaPosition++;
                            yanhuaIV.postDelayed(this,120);
                        }else if(yanhuaPosition==7){
                            yanhuaIVpurple.setImageBitmap(PictureTool.readBitMap(ZhiboActivity.this,yanhuaGiftPicture[11]));
                            yanhuaIVpurple.startAnimation(yanhuaAnimShow2);
                            yanhuaPosition++;
                            yanhuaIV.postDelayed(this,600);
                        }else if(yanhuaPosition==8){
                            yanhuaIVred.setImageBitmap(PictureTool.readBitMap(ZhiboActivity.this,yanhuaGiftPicture[12]));
                            yanhuaIVred.startAnimation(yanhuaAnimShow3);
                            yanhuaPosition++;
                            yanhuaIV.postDelayed(this,600);
                        }else if(yanhuaPosition==9){
                            yanhuaIVgreen.setImageBitmap(PictureTool.readBitMap(ZhiboActivity.this,yanhuaGiftPicture[13]));
                            yanhuaIVgreen.startAnimation(yanhuaAnimShow4);
                            yanhuaPosition++;
                            yanhuaIV.postDelayed(this,1200);
                        }else {
                            yanhuaPosition++;
                            yanhuaIV.postDelayed(this,120);
                        }
                    }else if(yanhuaPosition==10){
                        //退出动画
                        yanhuaIV.setImageBitmap(null);
                        yanhuaIVyellow.startAnimation(yanhuaAnimHide);
                        yanhuaIVpurple.startAnimation(yanhuaAnimHide);
                        //退出动画
                        yanhuaIVred.startAnimation(yanhuaAnimHide);
                        yanhuaIVgreen.startAnimation(yanhuaAnimHide);
                        yanhuaPosition++;
                        yanhuaIV.postDelayed(this,1200);
                    }else {
                        yanhuaIV.removeCallbacks(this);
                        yanhuaIVyellow.setImageBitmap(null);
                        yanhuaIVpurple.setImageBitmap(null);
                        yanhuaIVred.setImageBitmap(null);
                        yanhuaIVgreen.setImageBitmap(null);
                        yanhuaPosition=0;//重新归零
                    }
                }
            },120);
        }else {
            yanhuaGiftList.add(zhiboGift);
        }
    }


    //展示礼物的动画
    private View giftLayOut1;
    private CircleImageView photoCIV1;
    private TextView userNameTV1;
    private TextView giftNameTV1;
    private ImageView giftIV1;
    private TextView numTV1;
    private int userIDTag1=0;//用于标记第一个位是否有空，0为有空，没空则显示当前的user_id
    private String giftNameTag1=null;
    private Animation animationIn1;
    private Animation animationOut1;
    private Animation animationScaleNum1;
    private View giftLayOut2;
    private CircleImageView photoCIV2;
    private TextView userNameTV2;
    private TextView giftNameTV2;
    private ImageView giftIV2;
    private TextView numTV2;
    private int userIDTag2=0;//用于标记第二个位是否有空，0为有空，没空则显示当前的user_id
    private String giftNameTag2=null;
    private Animation animationIn2;
    private Animation animationOut2;
    private Animation animationScaleNum2;
    private List<ZhiboGift> zhiboGiftList=new ArrayList<>();//使用一个列表临时存储未展现的礼物
    private Handler giftHandler=new Handler(this);
    private void showGiftAnim(ZhiboGift zhiboGift){
        if(userIDTag2==zhiboGift.getId() && giftNameTag2.equals(zhiboGift.getGiftName())){//属于第二个位的“连”
            giftLayOut2.removeCallbacks(myAnimationRunnable2);
            numTV2.setText("x"+(Integer.valueOf(numTV2.getText().toString().replace("x",""))+1));
            numTV2.startAnimation(animationScaleNum2);
            giftLayOut2.postDelayed(myAnimationRunnable2,3000);
        }else if(userIDTag1==zhiboGift.getId() && giftNameTag1.equals(zhiboGift.getGiftName())){//属于第一个位的“连”
            giftLayOut1.removeCallbacks(myAnimationRunnable1);
            numTV1.setText("x"+(Integer.valueOf(numTV1.getText().toString().replace("x",""))+1));
            numTV1.startAnimation(animationScaleNum1);
            giftLayOut1.postDelayed(myAnimationRunnable1,3000);
        }else if(userIDTag2==0){//说明第二个位有空，则优先第二个位显示
            if (giftLayOut2 == null) {
                giftLayOut2=findViewById(R.id.giftLayOut2);
                photoCIV2= (CircleImageView)giftLayOut2.findViewById(R.id.photoCIV);
                userNameTV2 = (TextView) giftLayOut2.findViewById(R.id.userNameTV);
                giftNameTV2 = (TextView) giftLayOut2.findViewById(R.id.giftNameTV);
                giftIV2 = (ImageView)giftLayOut2.findViewById(R.id.giftIV);
                numTV2 = (TextView)giftLayOut2.findViewById(R.id.numTV);
                animationIn2 = AnimationUtils.loadAnimation(this, R.anim.show_left_to_right_anim);
                animationOut2 = AnimationUtils.loadAnimation(this, R.anim.hidden_scale_top_down_anim);
                animationScaleNum2=AnimationUtils.loadAnimation(this,R.anim.scale_gift_number);
                giftLayOut2.setVisibility(View.VISIBLE);
            }
            imageLoader.displayImage(zhiboGift.getPortrait(),photoCIV2,options);
            userNameTV2.setText(zhiboGift.getName());
            numTV2.setText("x1");
            giftNameTV2.setText("送一个"+zhiboGift.getGiftName());
            switch (zhiboGift.getGiftName()){
                case "套套":
                    giftIV2.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_taotao));
                    break;
                case "皮鞭":
                    giftIV2.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_pibian));
                    break;
                case "跳蛋":
                    giftIV2.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_tiaodan));
                    break;
                case "囧囧":
                    giftIV2.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_jiongbi));
                    break;
                case "飞吻":
                    giftIV2.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_feiwen));
                    break;
                case "真棒":
                    giftIV2.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_zhenbang));
                    break;
            }
            giftLayOut2.startAnimation(animationIn2);
            giftLayOut2.postDelayed(myAnimationRunnable2,3000);
            userIDTag2=zhiboGift.getId();
            giftNameTag2=zhiboGift.getGiftName();
        }else if(userIDTag1==0){//说明第一个位有空，则在第一个位显示
            if (giftLayOut1 == null) {
                giftLayOut1=findViewById(R.id.giftLayOut1);
                photoCIV1= (CircleImageView)giftLayOut1.findViewById(R.id.photoCIV);
                userNameTV1 = (TextView) giftLayOut1.findViewById(R.id.userNameTV);
                giftNameTV1 = (TextView) giftLayOut1.findViewById(R.id.giftNameTV);
                giftIV1 = (ImageView)giftLayOut1.findViewById(R.id.giftIV);
                numTV1 = (TextView)giftLayOut1.findViewById(R.id.numTV);
                giftLayOut1.setVisibility(View.VISIBLE);
                animationIn1 = AnimationUtils.loadAnimation(this, R.anim.show_left_to_right_anim);
                animationOut1 = AnimationUtils.loadAnimation(this, R.anim.hidden_scale_top_down_anim);
                animationScaleNum1=AnimationUtils.loadAnimation(this,R.anim.scale_gift_number);
            }
            imageLoader.displayImage(zhiboGift.getPortrait(),photoCIV1,options);
            userNameTV1.setText(zhiboGift.getName());
            numTV1.setText("x1");
            giftNameTV1.setText("送一个"+zhiboGift.getGiftName());
            switch (zhiboGift.getGiftName()){
                case "套套":
                    giftIV1.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_taotao));
                    break;
                case "皮鞭":
                    giftIV1.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_pibian));
                    break;
                case "跳蛋":
                    giftIV1.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_tiaodan));
                    break;
                case "囧囧":
                    giftIV1.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_jiongbi));
                    break;
                case "飞吻":
                    giftIV1.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_feiwen));
                    break;
                case "真棒":
                    giftIV1.setImageBitmap(PictureTool.readBitMap(this,R.drawable.gift_zhenbang));
                    break;
            }
            giftLayOut1.startAnimation(animationIn1);
            giftLayOut1.postDelayed(myAnimationRunnable1,3000);
            userIDTag1=zhiboGift.getId();
            giftNameTag1=zhiboGift.getGiftName();
        }else {//说明第一、二位都没空，先使用列表进行存储
            zhiboGiftList.add(zhiboGift);
        }
    }
    //第一个位的动画处理
    private Runnable myAnimationRunnable1=new Runnable() {
        @Override
        public void run() {
            //结束动画
            giftLayOut1.startAnimation(animationOut1);
            userIDTag1=0;
            giftNameTag1=null;
        }
    };
    //第二个位的动画处理
    private Runnable myAnimationRunnable2=new Runnable() {
        @Override
        public void run() {
            //结束动画
            giftLayOut2.startAnimation(animationOut2);
            userIDTag2=0;
            giftNameTag2=null;
        }
    };

    //检查一下礼物等待列表中是否还有，有的话进行礼物展示
    private Runnable checkGiftListRunnable=new Runnable() {
        @Override
        public void run() {
            if(zhiboGiftList.size()!=0){
                if(userIDTag2==0||userIDTag1==0){//只有在某个动画空位有空时，才进行礼物等待列表展示
                    showGiftAnim(zhiboGiftList.get(0));
                    zhiboGiftList.remove(0);//同时进行列表的清除
                }
            }
            if(heartGiftList.size()!=0){
                if(heartPosition==0){//说明心形礼物有空位
                    showHeartGiftAnim(heartGiftList.get(0));
                    heartGiftList.remove(0);//同时进行列表的清除
                }
            }
            if(yanhuaGiftList.size()!=0){
                if(yanhuaPosition==0){//说明烟花礼物有空位
                    showYanHuaGiftAnim(yanhuaGiftList.get(0));
                    yanhuaGiftList.remove(0);//同时进行列表的清除
                }
            }
            giftHandler.postDelayed(this,1000);//进行线程循环
        }
    };

    //开始直播
    public void gotoZhiBo(View view){
        if(mUrl!=null){
            countSecondTV.setVisibility(View.VISIBLE);
            //启动数秒缩放动作
            final Animation scaleAnimation= AnimationUtils.loadAnimation(this, R.anim.scale_second);
            countSecondTV.startAnimation(scaleAnimation);
            countSecondTV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    countSecondTV.setText("2");
                    countSecondTV.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countSecondTV.setText("1");
                            countSecondTV.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    prepareZhiboLL1.setVisibility(View.GONE);
                                    prepareZhiboLL2.setVisibility(View.GONE);
                                    zhibo_topLL.setVisibility(View.VISIBLE);
                                    zhibo_bottomLL.setVisibility(View.VISIBLE);

                                    checkPermission();
                                    if (mStreamer.startStream()) {
                                        // 混响
                                        mStreamer.setEnableReverb(false);//关闭混响，否则有沙沙沙声音
                                        mStreamer.setReverbLevel(4);//悠扬（level:1），空灵（level:2），KTV效果（level:3,4）
                                        zhiboAction=true;//代表直播开始
                                        Log.e(TAG, "进来");
                                    } else {
                                        Log.e(TAG, "操作太频繁");
                                    }
                                }
                            },1000);
                        }
                    },1000);
                }
            },1000);
        }else {
            Toast.makeText(this,"网络连接出错，请检查您的网络状态",Toast.LENGTH_SHORT).show();
            getZhiBoUrl();//再次获取直播地址
        }
    }

    //关闭底部聊天框
    private void cancleInputPanel(){
        if(input_panel.getVisibility()==View.VISIBLE){
            input_panel.setVisibility(View.GONE);
            input_panel.hideKeyboard();//隐藏键盘
            input_panel.closeEmojiBoard();//关闭表情板
            bottomBarLL.setVisibility(View.VISIBLE);
        }
    }

    //聊天
    public void chat(View view){
        bottomBarLL.setVisibility(View.GONE);
        input_panel.setVisibility(View.VISIBLE);
        input_panel.showKeyboard();//显示键盘
    }

    //进入囧币页
    public void jiongbi(View view){
        Toast.makeText(this,"此功能暂未开放",Toast.LENGTH_SHORT).show();
        /*Intent intent=new Intent(this,JiongBiRecordActivity.class);
        startActivity(intent);*/
    }

    //更换照相机镜头
    private long lastClickTime = 0;
    public void changeCamera(View view){
        long curTime = System.currentTimeMillis();
        if (curTime - lastClickTime < 1000) {
            return;
        }
        lastClickTime = curTime;
        mStreamer.switchCamera();
    }

    //分享
    public void share(View view){
        SharePopupWindow sharePopupWindow=new SharePopupWindow(this);
        sharePopupWindow.showSharePopupWindow(view);
    }

    //立刻关闭
    public void cancleAtOnce(View view){
        finishZhibo();//结束直播调用接口
        finish();
    }

    //关闭
    public void cancle(View view){
        showExitDialog();
    }

    //结束直播调用接口
    private void finishZhibo(){
        //导购结束直播
        GetNetData getNetData=new GetNetData(ZhiboActivity.this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
            }
        };
        RequestParams params=new RequestParams();
        getNetData.getData(params,CommonValue.closeLive);
    }


    /**
     * 以下为直播的相关方法
     */

    @Override
    public void onResume() {
        super.onResume();
        //可以在这里做权限检查,若没有audio和camera权限,进一步引导用户做权限设置
      //  checkPermission();
        mStreamer.onResume();
        mAcitivityResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mStreamer.onPause();
        mAcitivityResumed = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(zhibo_topLL.getVisibility()==View.VISIBLE)
                    showExitDialog();
                else{
                    finishZhibo();//结束直播调用接口
                    finish();
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //展示结束直播的对话框
    private void showExitDialog(){
        new AlertDialog.Builder(ZhiboActivity.this).setCancelable(true)
                .setTitle("结束直播？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finishZhibo();//结束直播调用接口
                        //发出一条消息，通知大家说直播已结束
                        InformationNotificationMessage content = InformationNotificationMessage.obtain(CommonValue.getUserName(ZhiboActivity.this)+" #￥%&+-*#￥%&+-*");
                        LiveKit.sendMessage(content);

                        mStreamer.stopStream(true);
                        chronometer.stop();
                        finish();
                    }
                }).show();
    }

    public OnStatusListener mOnErrorListener = new OnStatusListener() {
        @Override
        public void onStatus(int what, int arg1, int arg2, String msg) {
            // msg may be null
            switch (what) {
                case RecorderConstants.KSYVIDEO_OPEN_STREAM_SUCC:
                    // 推流成功
                    Log.d("TAG", "KSYVIDEO_OPEN_STREAM_SUCC");
                    mHandler.obtainMessage(what, "start stream succ")
                            .sendToTarget();
                    break;
                case RecorderConstants.KSYVIDEO_ENCODED_FRAMES_FAILED:
                    //编码失败
                    Log.e(TAG, "---------KSYVIDEO_ENCODED_FRAMES_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_WLD_UPLOAD:
                    break;
                case RecorderConstants.KSYVIDEO_FRAME_DATA_SEND_SLOW:
                    //网络状况不佳
                    if (mHandler != null) {
                        mHandler.obtainMessage(what, "network not good").sendToTarget();
                    }
                    break;
                case RecorderConstants.KSYVIDEO_EST_BW_DROP:
                    //编码码率下降状态通知
                    break;
                case RecorderConstants.KSYVIDEO_EST_BW_RAISE:
                    //编码码率上升状态通知
                    break;
                case RecorderConstants.KSYVIDEO_AUDIO_INIT_FAILED:
                    Log.e("CameraActivity", "init audio failed");
                    //音频录制初始化失败回调
                    break;
                case RecorderConstants.KSYVIDEO_INIT_DONE:
                    mHandler.obtainMessage(what, "init done")
                            .sendToTarget();
                    break;
                case RecorderConstants.KSYVIDEO_PIP_EXCEPTION:
                    mHandler.obtainMessage(what, "pip exception")
                            .sendToTarget();
                    break;
                case RecorderConstants.KSYVIDEO_RENDER_EXCEPTION:
                    mHandler.obtainMessage(what, "renderer exception")
                            .sendToTarget();
                    break;
                case RecorderConstants.KSYVIDEO_AUDIO_START_FAILED:
                    Log.e("CameraActivity", "-------audio start failed");
                    break;
                case RecorderConstants.KSYVIDEO_CAMERA_PARAMS_ERROR:
                    Log.e("CameraActivity", "-------camera param is null");
                    break;
                case RecorderConstants.KSYVIDEO_OPEN_CAMERA_FAIL:
                    Log.e("CameraActivity", "-------open camera failed");
                    break;
                default:
                    if (msg != null) {
                        // 可以在这里处理断网重连的逻辑
                        if (TextUtils.isEmpty(mUrl)) {
                            mStreamer.updateUrl("rtmp://192.168.0.115/jlm/androidtest");
                        } else {
                            mStreamer.updateUrl(mUrl);
                        }
                        if (!executorService.isShutdown()) {
                            executorService.submit(new Runnable() {

                                @Override
                                public void run() {
                                    boolean needReconnect = true;
                                    try {
                                        while (needReconnect) {
                                            Thread.sleep(3000);
                                            //只在Activity对用户可见时重连
                                            if (mAcitivityResumed) {
                                                if (mStreamer.startStream()) {
                                                    needReconnect = false;
                                                }
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                        }
                    }
                    if (mHandler != null) {
                        mHandler.obtainMessage(what, msg).sendToTarget();
                    }
            }
        }

    };


    //推流失败,需要退出重新启动推流,建议做log输出,方便快速定位问题
    public StreamStatusEventHandler.OnStatusErrorListener mOnStatusErrorListener = new StreamStatusEventHandler.OnStatusErrorListener() {
        @Override
        public void onError(int what, int arg1, int arg2, String msg) {
            switch (what) {
                case RecorderConstants.KSYVIDEO_ENCODED_FRAMES_FAILED:
                    //视频编码失败,发生在软解,会停止推流
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_ENCODED_FRAMES_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_CODEC_OPEN_FAILED:
                    //编码器初始化失败,发生在推流开始时
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_CODEC_OPEN_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_CONNECT_FAILED:
                    //打开编码器的输入输出文件失败,发生在推流开始时,会停止推流
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_CONNECT_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_DNS_PARSE_FAILED:
                    //打开编码器的输入输出文件失败,发生在推流开始时,会停止推流
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_CONNECT_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_RTMP_PUBLISH_FAILED:
                    //打开编码器的输入输出文件失败,发生在推流开始时,会停止推流
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_CONNECT_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_CONNECT_BREAK:
                    //写入一个音视频文件失败
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_CONNECT_BREAK");
                    break;
                case RecorderConstants.KSYVIDEO_AUDIO_INIT_FAILED:
                    //软编,音频初始化失败
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_AUDIO_INIT_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_AUDIO_COVERT_FAILED:
                    //软编,音频转码失败
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_AUDIO_COVERT_FAILED");
                    break;
                case RecorderConstants.KSYVIDEO_OPEN_CAMERA_FAIL:
                    //openCamera失败
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_OPEN_CAMERA_FAIL");
                    break;
                case RecorderConstants.KSYVIDEO_CAMERA_PARAMS_ERROR:
                    //获取不到camera参数,android 6.0 以下没有camera权限时可能发生
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_CAMERA_PARAMS_ERROR");
                    break;
                case RecorderConstants.KSYVIDEO_AUDIO_START_FAILED:
                    //audio startRecord 失败
                    Log.e(TAG, "the streaming stopped because KSYVIDEO_AUDIO_START_FAILED");
                    break;
                default:
                    break;
            }

//            if (mHandler != null) {
//                mHandler.obtainMessage(what, msg).sendToTarget();
//            }
        }
    };

    public StreamStatusEventHandler.OnStatusInfoListener mOnStatusInfoListener = new StreamStatusEventHandler.OnStatusInfoListener() {
        @Override
        public void onInfo(int what, int arg1, int arg2, String msg) {
            switch (what) {
                case RecorderConstants.KSYVIDEO_OPEN_STREAM_SUCC:
                    //推流成功
                    Log.d("TAG", "KSYVIDEO_OPEN_STREAM_SUCC");
//                    mHandler.obtainMessage(what, "start stream succ")
//                            .sendToTarget();
                    break;
                case RecorderConstants.KSYVIDEO_FRAME_DATA_SEND_SLOW:
                    //网络状况不佳
//                    if (mHandler != null) {
//                        mHandler.obtainMessage(what, "network not good").sendToTarget();
//                    }
                    break;
                case RecorderConstants.KSYVIDEO_EST_BW_RAISE:
                    //码率上调
                    Log.d("TAG", "KSYVIDEO_EST_BW_RAISE");
                    break;
                case RecorderConstants.KSYVIDEO_EST_BW_DROP:
                    //码率下调
                    Log.d("TAG", "KSYVIDEO_EST_BW_DROP");
                    break;
                case RecorderConstants.KSYVIDEO_INIT_DONE:
                    //video preview init done
                    Log.d("TAG", "KSYVIDEO_INIT_DONE");
                    break;
                case RecorderConstants.KSYVIDEO_PIP_EXCEPTION:
                    Log.d("TAG", "KSYVIDEO_PIP_EXCEPTION");
//                    mHandler.obtainMessage(what, "pip exception")
//                            .sendToTarget();
                    break;
                case RecorderConstants.KSYVIDEO_RENDER_EXCEPTION:
                    Log.d("TAG", "KSYVIDEO_RENDER_EXCEPTION");
//                    mHandler.obtainMessage(what, "renderer exception")
//                            .sendToTarget();
                    break;
                default:
                    break;

            }
        }
    };


    private OnLogEventListener mOnLogListener = new OnLogEventListener() {
        @Override
        public void onLogEvent(StringBuffer singleLogContent) {
            Log.d(TAG, "***onLogEvent : " + singleLogContent.toString());
        }
    };

    private OnAudioRawDataListener mOnAudioRawDataListener = new OnAudioRawDataListener() {
        @Override
        public short[] OnAudioRawData(short[] data, int count) {
            //audio pcm data
            return data;
        }
    };

    private boolean checkPermission() {
        try {
            int pRecordAudio = PermissionChecker.checkCallingOrSelfPermission(this, "android.permission.RECORD_AUDIO");
            int pCamera = PermissionChecker.checkCallingOrSelfPermission(this, "android.permission.CAMERA");
            //录音未获取权限，照相已获取权限
            if( pRecordAudio != PackageManager.PERMISSION_GRANTED && pCamera == PackageManager.PERMISSION_GRANTED){
                getPermissionsAboutAudioAndCamera(new String[]{Manifest.permission.RECORD_AUDIO});
                Log.e(TAG, "do not have AudioRecord permission, please check");
                return false;
            }
            //录音已获取权限，照相未获取权限
            if( pRecordAudio == PackageManager.PERMISSION_GRANTED && pCamera != PackageManager.PERMISSION_GRANTED){
                getPermissionsAboutAudioAndCamera(new String[]{Manifest.permission.CAMERA});
                Log.e(TAG, "do not have CAMERA permission, please check");
                return false;
            }
            //录音和照相都未获取权限
            if(pRecordAudio != PackageManager.PERMISSION_GRANTED && pCamera != PackageManager.PERMISSION_GRANTED ){
                getPermissionsAboutAudioAndCamera(new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA});
                Log.e(TAG, "do not have AudioRecord AND CAMERA permission, please check");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //关于Android6.0运行时权限的一些设置
    private void getPermissionsAboutAudioAndCamera(String[] permissions){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//判断是否Android6.0及以上
            requestPermissions(permissions, 1);
        }
    }


    //获取权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length==1){
            if( grantResults[0] != PackageManager.PERMISSION_GRANTED)
                showPermissionsResultDialog(permissions);
        }
        if(grantResults.length == 2){
            if( grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                showPermissionsResultDialog(new String[]{permissions[0]});
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED)
                showPermissionsResultDialog(new String[]{permissions[1]});
            if( grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED)
                showPermissionsResultDialog(permissions);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showPermissionsResultDialog(final String[] permissions){
        AlertDialog dialog;
        if(!shouldShowRequestPermissionRationale(permissions[0])||( permissions.length>1 &&!shouldShowRequestPermissionRationale(permissions[1]))){//通过这一步可判断“不再询问”是否被勾上
            dialog = new AlertDialog.Builder(this)
                    .setTitle("请允许使用摄像头和录音")
                    .setMessage("由于囧了么无法获取摄像头和录音权限，不能正常允许，请开启权限后再使用。\n" +
                            "\n" + "设置路径：设置->应用->囧了么商家->权限")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /**
                             * 以下方法为调用系统设置中关于本APP的应用详情页
                             */
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", "com.jionglemo.jionglemo_b", null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
        }else {
            dialog = new AlertDialog.Builder(this)
                    .setTitle("请允许使用摄像头和录音")
                    .setMessage("为了正常直播，我们需要获取摄像头和录音权限，否则您将无法正常使用囧了么")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(permissions, 1);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
        }
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {  //屏蔽back键
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        dialog.show();
    }
}
