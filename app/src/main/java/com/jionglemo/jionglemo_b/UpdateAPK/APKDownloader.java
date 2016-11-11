package com.jionglemo.jionglemo_b.UpdateAPK;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guodingyuan on 2016/1/28.
 */
public class APKDownloader {
    private String urlstr;// 下载的地址
    private String localfile;// 保存路径
    private int threadcount;// 线程数
    private int versionCode;
    private DownLownService.Mymessage mymessage;// 消息处理器
    private DownInfoDB downInfoDB;// 工具类
    private int fileSize;// 所要下载的文件的大小
    private List<DownInfo> infos;// 存放下载信息类的集合
    private static final int INIT = 1;//定义三种下载的状态：初始化状态，正在下载状态，暂停状态
    private static final int DOWNLOADING = 2;
    private static final int PAUSE = 3;
    private int state = INIT;
    private LoadInfo loadInfo;

    public APKDownloader(String urlstr, String localfile, int threadcount,int versions,
                         Context context, DownLownService.Mymessage mymessage) {
        this.urlstr = urlstr;
        this.localfile = localfile;
        this.threadcount = threadcount;
        this.versionCode=versions;
        this.mymessage = mymessage;
        downInfoDB = DownInfoDB.getInstance(context);
    }
    /**
     *判断是否正在下载
     */
    public boolean isdownloading() {
        return state == DOWNLOADING;
    }
    /**
     * 得到downloader里的信息
     * 首先进行判断是否是第一次下载，如果是第一次就要进行初始化，并将下载器的信息保存到数据库中
     * 如果不是第一次下载，那就要从数据库中读出之前下载的信息（起始位置，结束位置，文件大小等），并将下载信息返回给下载器
     */
    public LoadInfo getDownloaderInfors() {

        if (isFirst(urlstr,versionCode)) {
            Log.i("guodingyuan", "第一次");
            init();
            int range = fileSize / threadcount;
            infos = new ArrayList<>();
            for (int i = 0; i < threadcount - 1; i++) {
                DownInfo downInfo = new DownInfo();
                downInfo.setThreadId(String.valueOf(i));
                downInfo.setStartPos(i * range);
                downInfo.setEndPos((i + 1) * range - 1);
                downInfo.setCompeleteSize(0);
                downInfo.setUrl(urlstr);
                downInfo.setVersionCode(versionCode);
                infos.add(downInfo);
            }
            DownInfo downInfo = new DownInfo();
            downInfo.setThreadId(String.valueOf(threadcount - 1));
            downInfo.setStartPos((threadcount - 1) * range);
            downInfo.setEndPos(fileSize - 1);
            downInfo.setCompeleteSize(0);
            downInfo.setUrl(urlstr);
            downInfo.setVersionCode(versionCode);
            infos.add(downInfo);
            //保存infos中的数据到数据库
            downInfoDB.saveInfos(infos);
            //创建一个LoadInfo对象记载下载器的具体信息
            loadInfo = new LoadInfo(fileSize, 0, urlstr);
            return loadInfo;
        } else {
            //得到数据库中已有的urlstr的下载器的具体信息
            infos = downInfoDB.getInfos(urlstr);

            int size = 0;
            int compeleteSize = 0;
            for (DownInfo info : infos) {
                compeleteSize += info.getCompeleteSize();
                size += info.getEndPos() - info.getStartPos() + 1;
            }
            loadInfo =new LoadInfo(size, compeleteSize, urlstr);
            return loadInfo;
        }
    }

    /**
     * 初始化
     */
    private void init() {
        try {
            URL url = new URL(urlstr);
            Log.i("guo",urlstr+"urlstr");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            fileSize = connection.getContentLength();
            Log.i("guo",fileSize+"fileSize");

            File file = new File(localfile);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 本地访问文件
            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
            accessFile.setLength(fileSize);
            accessFile.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是第一次 下载
     */
    private boolean isFirst(String urlstr,int versionCode) {
        return downInfoDB.isHasInfors(urlstr,versionCode);
    }

    /**
     * 利用线程开始下载数据
     */
    public void download() {
        Log.i("guo", "哈哈1");

        if (infos != null) {
            Log.i("guo", "哈哈2");

            if (state == DOWNLOADING)
                return;
            state = DOWNLOADING;
            for (DownInfo info : infos) {
                new MyThread(info.getThreadId(), info.getStartPos(),
                        info.getEndPos(), info.getCompeleteSize(),
                        info.getUrl()).start();
            }
        }
    }

    public class MyThread extends Thread {
        private String threadId;
        private int startPos;
        private int endPos;
        private int compeleteSize;
        private String urlstr;

        public MyThread(String threadId, int startPos, int endPos,
                        int compeleteSize, String urlstr) {
            this.threadId = threadId;
            this.startPos = startPos;
            this.endPos = endPos;
            this.compeleteSize = compeleteSize;
            this.urlstr = urlstr;
        }
        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile randomAccessFile = null;
            InputStream is = null;
            try {

                URL url = new URL(urlstr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                // 设置范围，格式为Range：bytes x-y;
                connection.setRequestProperty("Range", "bytes="+(startPos + compeleteSize) + "-" + endPos);

                randomAccessFile = new RandomAccessFile(localfile, "rwd");
                randomAccessFile.seek(startPos + compeleteSize);
                Log.i("RG", "connection--->>>"+connection);
                // 将要下载的文件写到保存在保存路径下的文件中
                is = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int length = -1;
                int Length=0;
                int i=0;
                while ((length = is.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, length);
                    compeleteSize += length;
                    // 更新数据库中的下载信息
                    downInfoDB.updataInfos(threadId, compeleteSize, urlstr);
                    // 用消息将下载信息传给进度条，对进度条进行更新
                    i++;
                    Length=Length+length;
                    if(i==(endPos-startPos)/length/20){//要求每个线程只允许更新20次UI
                        mymessage.setCompeleteSize(loadInfo.getComplete());
                        mymessage.setLength(Length);
                        mymessage.setUrl(urlstr);
                        mymessage.updateUI();
                        i=0;
                        Length=0;
                    }

                    if ((endPos-startPos) <= (compeleteSize+1)) {
                        mymessage.setCompeleteSize(loadInfo.getComplete());
                        mymessage.setLength(Length);
                        mymessage.setUrl(urlstr);
                        mymessage.setComplete(true);
                        mymessage.updateUI();
                        Log.i("guodingyuan","下载完成");
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(is!=null)
                        is.close();
                    if(randomAccessFile!=null)
                        randomAccessFile.close();
                    if(connection!=null)
                        connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //删除数据库中urlstr对应的下载器信息
    public void delete(String urlstr) {
        downInfoDB.delete(urlstr);
    }
    //设置暂停
    public void pause() {
        state = PAUSE;
    }
    //重置下载状态
    public void reset() {
        state = INIT;
    }
}