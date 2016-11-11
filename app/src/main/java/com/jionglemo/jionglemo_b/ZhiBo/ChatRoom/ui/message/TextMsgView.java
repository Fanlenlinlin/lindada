package com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.ui.message;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.LiveKit;
import com.jionglemo.jionglemo_b.ZhiBo.ChatRoom.controller.EmojiManager;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

public class TextMsgView extends BaseMsgView {

    private TextView msgText;
    private String userName;
    private Context context;

    public TextMsgView(Context context) {
        super(context);
        this.context=context;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_text_view, this);
        msgText = (TextView) view.findViewById(R.id.msg_text);
    }

    @Override
    public void setContent(MessageContent msgContent) {
        final TextMessage msg = (TextMessage) msgContent;
        userName=msg.getUserInfo().getName();
        SpannableString ss = new SpannableString(userName+ " "+EmojiManager.parse(msg.getContent(), msgText.getTextSize()));
        ss.setSpan(new ForegroundColorSpan(Color.rgb(255,0,135)), 0, msg.getUserInfo().getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msgText.setText(ss);
        msgText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.show();
                Window window = dialog.getWindow();
                window.setContentView(R.layout.shutup_dialog);
                TextView user_nameTV= (TextView) window.findViewById(R.id.user_nameTV);
                final RadioGroup timeRG= (RadioGroup) window.findViewById(R.id.timeRG);
                TextView cancelTV= (TextView) window.findViewById(R.id.cancelTV);
                TextView confirmTV= (TextView) window.findViewById(R.id.confirmTV);
                user_nameTV.setText(" "+userName+" ");
                cancelTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                confirmTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (timeRG.getCheckedRadioButtonId()){
                            case R.id.timeRB1:
                                shutup(msg.getUserInfo().getUserId(),"10",dialog,msg.getUserInfo().getName());
                                break;
                            case R.id.timeRB2:
                                shutup(msg.getUserInfo().getUserId(),"60",dialog,msg.getUserInfo().getName());
                                break;
                            case R.id.timeRB3:
                                shutup(msg.getUserInfo().getUserId(),"720",dialog,msg.getUserInfo().getName());
                                break;
                            case R.id.timeRB4:
                                shutup(msg.getUserInfo().getUserId(),"1440",dialog,msg.getUserInfo().getName());
                                break;
                            default:
                                Toast.makeText(getContext(),"请选择禁言时间",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    //调用禁言的方法
    private void shutup(String userId, final String minute, final AlertDialog dialog, final String userName){
        GetNetData getNetData=new GetNetData(getContext()){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                try {
                    if(response.getInt("status")==1){
                        String time = "";
                        switch (minute){
                            case "10":
                                time="10分钟";
                                break;
                            case "60":
                                time="1小时";
                                break;
                            case "720":
                                time="12小时";
                                break;
                            case "1440":
                                time="24小时";
                                break;
                        }
                        InformationNotificationMessage content = InformationNotificationMessage.obtain(CommonValue.getUserName(context)+" 将“"+userName+"”禁言"+time);
                        LiveKit.sendMessage(content);
                    }else {
                        Toast.makeText(getContext(),"禁言失败，请稍后再试！",Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("jid",userId);
        params.put("room_id",String.valueOf(CommonValue.getJid(getContext())));
        params.put("minute",minute);
        getNetData.getData(params,CommonValue.groupUserGag);
    }
}
