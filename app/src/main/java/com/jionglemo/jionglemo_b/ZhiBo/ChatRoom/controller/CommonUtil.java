package com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.controller;


import com.jionglemo.jionglemo_b.BasePacket.MyApplication;

public class CommonUtil {

    public static int dip2px(float dpValue) {
        float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
