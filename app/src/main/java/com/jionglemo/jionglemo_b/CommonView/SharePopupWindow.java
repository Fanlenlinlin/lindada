package com.jionglemo.jionglemo_b.CommonView;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.R;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Mike on 2016/9/2.
 */
public class SharePopupWindow {

    private Context context;
    private PopupWindow popupWindow;
    private LinearLayout wechatLL;
    private LinearLayout wechatmomentsLL;
    private LinearLayout qqLL;
    private LinearLayout qqzoneLL;
    private LinearLayout sinaweiboLL;
    private TextView cancelShareTV;
    private String appLoadPath="http://a.app.qq.com/o/simple.jsp?pkgname=com.jionglemo.jionglemo";
    private String logoPath="https://mmbiz.qlogo.cn/mmbiz/o4eXCkwib6SIxzibSC05J4h6UtzVU2VG0icCa5XqdB4NOZuFoxuuuxciaLblGeC6EFPSiaP8ErAwvHbUgw8kvpXhMDQ/0?wx_fmt=png";

    public SharePopupWindow(Context context) {
        this.context=context;
    }

    public void showSharePopupWindow(View view){
        if (popupWindow == null) {
            ShareSDK.initSDK(context);//进行初始化
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View shareView = layoutInflater.inflate(R.layout.share_popupwindow, null);
            wechatLL = (LinearLayout) shareView.findViewById(R.id.wechatLL);
            wechatmomentsLL = (LinearLayout) shareView.findViewById(R.id.wechatmomentsLL);
            qqLL = (LinearLayout) shareView.findViewById(R.id.qqLL);
            qqzoneLL = (LinearLayout) shareView.findViewById(R.id.qqzoneLL);
            sinaweiboLL = (LinearLayout) shareView.findViewById(R.id.sinaweiboLL);
            cancelShareTV = (TextView) shareView.findViewById(R.id.cancelShareTV);
            // 创建一个PopuWidow对象
            popupWindow = new PopupWindow(shareView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        }
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置popWindow的显示和消失动画
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);

        //微信分享
        wechatLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wechat.ShareParams sp = new Wechat.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);//必须指定分享的类型，否则无法完成分享
                sp.setTitle("囧了么");
                sp.setText("囧了么APP下载");
                sp.setUrl(appLoadPath);//分享链接
                sp.setImagePath(logoPath);
                Platform wechat = ShareSDK.getPlatform (Wechat.NAME);
                wechat.setPlatformActionListener (new MyPlatformActionListener()); // 设置分享事件回调
                wechat.share(sp);
                popupWindow.dismiss();
            }
        });

        //微信朋友圈分享
        wechatmomentsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
                sp.setShareType(Platform.SHARE_WEBPAGE);//必须指定分享的类型，否则无法完成分享
                sp.setTitle("囧了么");
                sp.setText("囧了么APP下载");
                sp.setUrl(appLoadPath);//分享链接
                sp.setImageUrl(logoPath);//此处必须使用setImageUrl，使用setImagePath显示不出页面
                Platform wechatMoments = ShareSDK.getPlatform (WechatMoments.NAME);
                wechatMoments.setPlatformActionListener (new MyPlatformActionListener()); // 设置分享事件回调
                wechatMoments.share(sp);
                popupWindow.dismiss();
            }
        });

        //QQ分享
        qqLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QQ.ShareParams sp = new QQ.ShareParams();
                sp.setTitle("囧了么APP下载");
                sp.setTitleUrl(appLoadPath);
                sp.setText(appLoadPath);//分享链接
                sp.setImageUrl(logoPath);
                Platform qq = ShareSDK.getPlatform (QQ.NAME);
                qq. setPlatformActionListener (new MyPlatformActionListener()); // 设置分享事件回调
                qq.share(sp);
                popupWindow.dismiss();
            }
        });

        //QQ空间分享
        qqzoneLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZone.ShareParams sp = new QZone.ShareParams();
                sp.setTitle("囧了么APP下载");
                sp.setTitleUrl(appLoadPath); // 标题的超链接
                sp.setImageUrl(logoPath);
                sp.setSite("囧了么APP下载");
                sp.setSiteUrl(appLoadPath);
                sp.setText(appLoadPath);
                Platform qzone = ShareSDK.getPlatform (QZone.NAME);
                qzone. setPlatformActionListener (new MyPlatformActionListener()); // 设置分享事件回调
                qzone.share(sp);
                popupWindow.dismiss();
            }
        });

        //微博分享
        sinaweiboLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
                sp.setText("囧了么APP下载"+appLoadPath);//分享链接
                sp.setImageUrl(logoPath);
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                weibo.setPlatformActionListener(new MyPlatformActionListener());// 设置分享事件回调
                weibo.share(sp);//执行分享
                popupWindow.dismiss();
            }
        });

        //取消分享
        cancelShareTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    private class MyPlatformActionListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Toast.makeText(context,"已分享",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Toast.makeText(context,"分享出错",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Toast.makeText(context,"用户取消分享",Toast.LENGTH_SHORT).show();
        }
    }
}
