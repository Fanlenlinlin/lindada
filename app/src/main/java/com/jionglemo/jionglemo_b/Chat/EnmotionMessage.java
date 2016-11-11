package com.jionglemo.jionglemo_b.Chat;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

@MessageTag(value = "CustomMessage", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class EnmotionMessage extends MessageContent {

    private int position;

    public EnmotionMessage(int position){
        this.position=position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public EnmotionMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("position"))
                position = jsonObj.optInt("position");
            if(jsonObj.has("user"))
                setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("position", position);
            if(getJSONUserInfo() != null) {
                jsonObj.putOpt("user", getJSONUserInfo());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        ParcelUtils.writeToParcel(dest,getUserInfo());
    }

    protected EnmotionMessage(Parcel in) {
        position = in.readInt();
        setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
    }

    public static final Creator<EnmotionMessage> CREATOR = new Creator<EnmotionMessage>() {
        @Override
        public EnmotionMessage createFromParcel(Parcel source) {
            return new EnmotionMessage(source);
        }

        @Override
        public EnmotionMessage[] newArray(int size) {
            return new EnmotionMessage[size];
        }
    };
}