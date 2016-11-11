package com.jionglemo.jionglemo_b.HomePage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.BasePacket.MainActivity;
import com.jionglemo.jionglemo_b.CommonTool.DataCleanManager;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.Version;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.Login.LoadActivity2;
import com.jionglemo.jionglemo_b.R;
import com.jionglemo.jionglemo_b.UpdateAPK.ApkInfo;
import com.jionglemo.jionglemo_b.UpdateAPK.NewVersionDialog;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;

import io.rong.imkit.RongIM;

public class MySettingActivity extends AppCompatActivity {

    private File cacheFile;
    private TextView cacheSizeTV;
    private boolean needClean=true;//是否需要进行清除缓存
    private TextView versionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        cacheSizeTV = (TextView) findViewById(R.id.cacheSizeTV);
        //得到/data/data/com.xxx.xxx/cache文件
        cacheFile = getCacheDir();
        try {
            cacheSizeTV.setText(DataCleanManager.getCacheSize(cacheFile));
            if(DataCleanManager.getCacheSize(cacheFile).equals("0KB"))
                needClean=false;//如果缓存为0K的话，不需要清除
        } catch (Exception e) {
            e.printStackTrace();
        }

        versionTV = (TextView) findViewById(R.id.versionTV);
        versionTV.setText("当前版本V"+ Version.getVersionName(this));
        //检查更新
        final int versionCode= Version.getVersionCode(this);//获取当前APK版本号
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                ApkInfo apkInfo= JSON.parseObject(response.toString(),ApkInfo.class);
                if(apkInfo.getVersion_code()>versionCode){
                    versionTV.setText("NEW");
                    versionTV.setTextColor(Color.WHITE);
                    versionTV.setBackgroundResource(R.drawable.juxing_ff0099);
                }
            }
        };
        RequestParams params=new RequestParams();
        params.put("type",2);//类型：1用户版；2商家版
        getNetData.getData(params, CommonValue.getNewApkInfo);
    }

    public void back(View view){
        finish();
    }

    public void cleanCache(View view){
        if(needClean){
            //这个方法传入“false”表示/data/data/com.xxx.xxx/cache下面所有的文件及文件夹全部删除，但不删除cache这个文件夹
            DataCleanManager.deleteFolderFile(cacheFile.getPath(), false);
            cacheSizeTV.setText("0KB");
            Toast.makeText(this,"缓存清理成功",Toast.LENGTH_SHORT).show();
            needClean=false;
        }else {
            Toast.makeText(this,"暂时不需要清理缓存",Toast.LENGTH_SHORT).show();
        }
    }

    public void aboutUs(View view){
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.about_us);
    }

    public void userAgreement(View view){
        Intent intent = new Intent(MySettingActivity.this,UserAgreementActivity.class);
        startActivity(intent);
    }

    public void quitLoad(View view){
        //退出登录接口，异步完成
        GetNetData getNetData=new GetNetData(this){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
            }
        };

        getNetData.getData(null,CommonValue.logout);
        SharedPreferences.Editor editor = getSharedPreferences(CommonValue.UserMessage,0).edit();
        editor.clear();//清空SharedPreferences
        editor.commit();

        //退出登录不收push消息
        RongIM.getInstance().logout();

        Intent intent=new Intent(MySettingActivity.this, LoadActivity2.class);
        startActivity(intent);
        finish();
        MainActivity.mainActivity.finish();
    }

    //检查更新
    public void checkVersion(View view){
        NewVersionDialog.checkUpdateAPKBySelf(this);
    }
}
