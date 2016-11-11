package com.jionglemo.jionglemo_b.ZhiBo.ChatRoom;

/**
 * Created by Mike on 2016/8/27.
 */
public class Gift {
    private int photoResource;
    private String giftName;
    private String giftMoney;

    public Gift(int photoResource, String giftName, String giftMoney) {
        this.photoResource = photoResource;
        this.giftName = giftName;
        this.giftMoney = giftMoney;
    }

    public int getPhotoResource() {
        return photoResource;
    }

    public void setPhotoResource(int photoResource) {
        this.photoResource = photoResource;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftMoney() {
        return giftMoney;
    }

    public void setGiftMoney(String giftMoney) {
        this.giftMoney = giftMoney;
    }
}
