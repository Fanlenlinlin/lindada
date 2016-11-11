package com.jionglemo.jionglemo_b.ZhiBo;

/**
 * Created by Mike on 2016/9/29.
 * 礼物
 */
public class ZhiboGift {

    private int id;//送礼物的用户id
    private String name;//送礼物的用户名称
    private String portrait;//送礼物的用户头像
    private String giftName;//所送礼物的名字


    public ZhiboGift(int id, String name, String portrait, String giftName) {
        this.id = id;
        this.name = name;
        this.portrait = portrait;
        this.giftName = giftName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }
}
