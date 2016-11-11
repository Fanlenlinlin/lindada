package com.jionglemo.jionglemo_b.UpdateAPK;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jionglemo.jionglemo_b.DB.MySQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guodingyuan on 2016/1/28.
 * 用于APK版本更新
 */
public class DownInfoDB {

    //数据库名
    public static final String DB_NAME_DOWNINFO = "down_info";
    //数据库版本
    public static final int VERSION = 1;
    private static DownInfoDB downInfoDB;
    private SQLiteDatabase db;
    private MySQLiteOpenHelper dbHelper;

    //将构造方法私有化
    private DownInfoDB(Context context) {
        dbHelper = new MySQLiteOpenHelper(context, DB_NAME_DOWNINFO, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取ApkInfoDB的实例。
     */
    public synchronized static DownInfoDB getInstance(Context context) {
        if (downInfoDB == null) {
            downInfoDB = new DownInfoDB(context);
        }
        return downInfoDB;
    }

    /**
     * 查看数据库中是否有数据
     */
    public boolean isHasInfors(String urlstr,int versionCode) {

        Cursor cursor = db.query("DownInfo", null, "url = ?", new String[]{urlstr}, null, null, null);
        if(cursor.moveToFirst()){
           if(cursor.getInt(cursor.getColumnIndex("versionCode"))==versionCode){
               cursor.close();
               return false;
           }else {//说明现数据库遗留的为以前版本的数据，必须清空，同时，返回true（代表第一次下载）
               delete(urlstr);
               return true;
           }
        } else {
            cursor.close();
            return true;
        }
    }

    /**
     * 保存下载的具体信息
     */
    public void saveInfos(List<DownInfo> downInfos) {

        if(downInfos !=null){
            for (DownInfo info : downInfos) {
                ContentValues values = new ContentValues();
                values.put("threadId", info.getThreadId());
                values.put("startPos", info.getStartPos());
                values.put("endPos", info.getEndPos());
                values.put("compeleteSize", info.getCompeleteSize());
                values.put("url", info.getUrl());
                values.put("versionCode", info.getVersionCode());
                db.insert("DownInfo", null, values);
            }
        }
    }

    /**
     * 得到下载具体信息
     */
    public List<DownInfo> getInfos(String urlstr) {

        List<DownInfo> list = new ArrayList<>();
        Cursor cursor = db.query("DownInfo", null, "url = ?", new String[] {urlstr}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                DownInfo downInfo = new DownInfo();
                downInfo.setThreadId(cursor.getString(cursor.getColumnIndex("threadId")));
                downInfo.setStartPos(cursor.getInt(cursor.getColumnIndex("startPos")));
                downInfo.setEndPos(cursor.getInt(cursor.getColumnIndex("endPos")));
                downInfo.setCompeleteSize(cursor.getInt(cursor.getColumnIndex("compeleteSize")));
                downInfo.setUrl(urlstr);
                downInfo.setVersionCode(cursor.getInt(cursor.getColumnIndex("versionCode")));
                list.add(downInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 更新数据库中的下载信息
     */
    public void updataInfos(String threadId, int compeleteSize, String urlstr) {

        ContentValues values = new ContentValues();
        values.put("compeleteSize", compeleteSize);
        db.update("DownInfo", values, "threadId = ? and url = ?", new String[]{threadId, urlstr});
    }

    /**
     * 下载完成后删除数据库中的数据
     */
    public void delete(String url) {
        db.delete("DownInfo", "url=?", new String[]{url});
    }
}
