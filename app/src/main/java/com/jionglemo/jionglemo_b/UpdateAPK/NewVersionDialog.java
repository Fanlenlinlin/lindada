package com.jionglemo.jionglemo_b.UpdateAPK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jionglemo.jionglemo_b.CommonTool.Date_transform;
import com.jionglemo.jionglemo_b.CommonTool.GetNetData;
import com.jionglemo.jionglemo_b.CommonTool.Version;
import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;




/**
 * Created by guodingyuan on 2016/2/17.
 */
public class NewVersionDialog {

    //自动检查更新方法
    public static void checkUpdateAPK(final Context context){

        //加上这两行代码后可在主线程中访问网络
        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        //StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        final int versionCode= Version.getVersionCode(context);//获取当前APK版本号
        GetNetData getNetData=new GetNetData(context){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                ApkInfo apkInfo= JSON.parseObject(response.toString(),ApkInfo.class);
                if(apkInfo.getVersion_code()>versionCode)
                    showNewVersionDialog(apkInfo,context);
            }
        };
        RequestParams params=new RequestParams();
        params.put("type",2);//类型：1用户版；2商家版
        getNetData.getData(params, CommonValue.getNewApkInfo);
    }

    //手动检查更新方法
    public static void checkUpdateAPKBySelf(final Context context){
        final ProgressDialog progressDialog=ProgressDialog.show(context, "检查更新", "正在检查更新中···");
        progressDialog.setCancelable(true);
        final int versionCode= Version.getVersionCode(context);//获取当前APK版本号
        GetNetData getNetData=new GetNetData(context){
            @Override
            public void getDataSuccess(JSONObject response) {
                super.getDataSuccess(response);
                progressDialog.dismiss();
                ApkInfo apkInfo= JSON.parseObject(response.toString(),ApkInfo.class);
                if(apkInfo.getVersion_code()>versionCode)
                    showNewVersionDialog(apkInfo,context);
                else {
                    new AlertDialog.Builder(context)
                            .setTitle("通知")
                            .setMessage("暂未发现新版本")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }

            @Override
            public void getDataFailure() {
                super.getDataFailure();
                progressDialog.dismiss();
            }
        };
        RequestParams params=new RequestParams();
        params.put("type",2);//类型：1用户版；2商家版
        getNetData.getData(params, CommonValue.getNewApkInfo);
    }

    //发现新版本的对话框
    public static void showNewVersionDialog(final ApkInfo apkInfo, final Context context){
        final AlertDialog dialog=new AlertDialog.Builder(context, R.style.NobackDialog).create();//设置对话框风格为完全透明
        dialog.setCancelable(false);//设置其点击其它地方无法关闭
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.updateapk_dialog);
        TextView versionTV= (TextView) window.findViewById(R.id.versionTV);
        TextView riqiTV= (TextView) window.findViewById(R.id.riqiTV);
        TextView contentTV= (TextView)window.findViewById(R.id.contentTV);
        TextView updateTV= (TextView)window.findViewById(R.id.updateTV);
        Button tuichuBT= (Button)window.findViewById(R.id.tuichuBT);
        versionTV.setText("V" + apkInfo.getVersion_name() );
        riqiTV.setText(Date_transform.getTimesOne(apkInfo.getCreate_time()).substring(0, 10));
        contentTV.setText(apkInfo.getContent().replace('\\','\n'));
        updateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartDownLown(apkInfo.getSrc(), apkInfo.getVersion_code(), context);
                dialog.dismiss();
            }
        });
        tuichuBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //下载APK方法
    private static void StartDownLown(String url, int versionCode, Context context){
        Intent intent=new Intent(context,DownLownService.class);
        intent.putExtra("url",url);
        intent.putExtra("versionCode", versionCode);
        context.startService(intent);
    }

}
