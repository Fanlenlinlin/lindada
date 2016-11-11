package com.jionglemo.jionglemo_b.UpdateAPK;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jionglemo.jionglemo_b.CommonView.CommonValue;
import com.jionglemo.jionglemo_b.R;

import java.io.File;

public class DownLownService extends Service {

    private NotificationManager nm;
    private LoadInfo loadInfo;
    private APKDownloader apkDownloader;
    private int progress;
    private RemoteViews remoteView;
    private Notification.Builder builder;
    private int threadcount = 4;//设置下载线程数为4，这里是我为了方便随便固定的
    private boolean firstTime=true;

    public DownLownService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(firstTime){//防止进行多次下载
            firstTime=false;
            String url=intent.getStringExtra("url");
            int versionCode=intent.getIntExtra("versionCode",0);
            Log.i("guodingyuan",url+"哈哈哈"+versionCode);
            DownInfoDB downInfoDB = DownInfoDB.getInstance(this);
            if(downInfoDB.isHasInfors(url, versionCode) && checkAPKInfo(versionCode)) {//若数据库中无上次遗留的数据（即上次已下载完毕），且手机上存在的apk文件版本号与现在要升级的版本号一致，则直接进行安装，否则进行下载更新
                update();//安装APK
                stopSelf();
            }else {
                nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                builder = new Notification.Builder(this);
                builder.setSmallIcon(R.drawable.logo_b);
                final Notification notification= builder.getNotification();//获取一个Notification
                //notification.flags=Notification.FLAG_NO_CLEAR; //设置无法进行清除
                notification.flags=Notification.FLAG_AUTO_CANCEL;//设置可清除
                notification.defaults =Notification.DEFAULT_SOUND;//设置为默认的声音
                //使用notification.xml文件作VIEW
                remoteView= new RemoteViews(getPackageName(), R.layout.update_apk_dialog);

              /*
                Intent notificationIntent = new Intent(this,main.class);
                PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
                notification.contentIntent = contentIntent;*/

                //建立自已项目文件夹
                File temp = new File(CommonValue.photoPath);
                if (!temp.exists()) {
                    temp.mkdir();
                }
                String localfile = CommonValue.photoPath +"囧了么.apk";
                Mymessage mymessage=new Mymessage();
                // 初始化一个downloader下载器
                apkDownloader = new APKDownloader(url, localfile, threadcount,versionCode,this, mymessage);
                // 得到下载信息类的个数组成集合
                // 因必须访问网络，故需开线程，通过异步进行处理
                final Handler handler=new Handler();
                Runnable downloaderRunnable=new Runnable() {
                    @Override
                    public void run() {
                        loadInfo = apkDownloader.getDownloaderInfors();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("guodingyuan","loadInfo.getFileSize()"+loadInfo.getFileSize());
                                remoteView.setProgressBar(R.id.progressBar, loadInfo.getFileSize(), loadInfo.getComplete(), false);
                                //设置进度条，最大值为100,当前值为0，最后一个参数为true时显示条纹
                                //（就是在Android Market下载软件，点击下载但还没获取到目标大小时的状态）/remoteView.setTextViewText(R.id.baifenbiTV, (int) ((float)loadInfo.getComplete()/(float)loadInfo.getFileSize())+"%");
                                remoteView.setTextViewText(R.id.baifenbiTV, (int) ((float) loadInfo.getComplete() / (float) loadInfo.getFileSize()*100) + "%");
                                notification.contentView=remoteView;
                                nm.notify(1, notification);
                                apkDownloader.download();
                            }
                        });
                    }
                };
                new Thread(downloaderRunnable).start();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class Mymessage{

        private String url;
        private Handler mHandler = new Handler();
        private boolean first=true;
        private boolean complete=false;
        private int num=0;

        public void setUrl(String url) {
            this.url = url;
        }

        public void setCompeleteSize(int compeleteSize) {
            if(first){
                progress=compeleteSize;
                first=false;
            }
        }

        public void setLength(int length) {
            progress=progress+length;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }

        public void updateUI(){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Notification notification = builder.getNotification();//获取一个Notification
                    notification.flags = Notification.FLAG_AUTO_CANCEL;//设置可清除
                    remoteView.setProgressBar(R.id.progressBar, loadInfo.getFileSize(), progress, false);
                    remoteView.setTextViewText(R.id.baifenbiTV, (int) ((float) progress / (float) loadInfo.getFileSize() * 100) + "%");
                    if (complete) {
                        num++;
                        if (num == threadcount) {//只有当所有线程完成下载时才进行安装和Toast
                            Log.i("guo", "完成");
                            remoteView.setTextViewText(R.id.loadstateTV, "下载完成");
                            Toast.makeText(DownLownService.this, "下载完成！", Toast.LENGTH_SHORT).show();
                            // 下载完成后将数据清空
                            apkDownloader.delete(url);
                            apkDownloader.reset();
                            update();//安装APK
                            stopSelf();
                        }
                    }
                    //关键部分，如果你不重新更新通知，进度条是不会更新的
                    notification.contentView = remoteView;
                    nm.notify(1, notification);
                }
            });
        }
    }

    //用于判定文件夹中是否存在apk文件，若存在则继续判定apk的版本是否与要安装的版本相同
    private boolean checkAPKInfo(int versionCode){
        File apk_file=new File(CommonValue.photoPath, "囧了么.apk");
        if(apk_file.exists()){
            String apk_path=apk_file.getPath();
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
            /** apk的版本号码 int */
            int VersionCode = packageInfo.versionCode;
            if(VersionCode==versionCode){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    //安装文件，一般固定写法
    private void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(CommonValue.photoPath, "囧了么.apk")),"application/vnd.android.package-archive");
        getApplication().startActivity(intent);
    }
}
