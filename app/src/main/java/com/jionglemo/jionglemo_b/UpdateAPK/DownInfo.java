package com.jionglemo.jionglemo_b.UpdateAPK;

/**
 * Created by guodingyuan on 2016/1/28.
 */
public class DownInfo {
    private String threadId;// 下载器id
    private int startPos;// 开始点
    private int endPos;// 结束点
    private int compeleteSize;// 完成度
    private String url;// 下载器网络标识
    private int versionCode;//版本号

    public int getCompeleteSize() {
        return compeleteSize;
    }

    public void setCompeleteSize(int compeleteSize) {
        this.compeleteSize = compeleteSize;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
