package com.jionglemo.jionglemo_b.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jionglemo.jionglemo_b.UpdateAPK.DownInfoDB;

/**
 * Created by Mike on 2016/7/7.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {


    /**
     * 登录用户历史表建表语句
     */
    public static final String CREATE_USERHISTORY = "create table UserHistory ("
            + "id integer primary key autoincrement, "
            + "user_phone text)";

    /**
     * APK文件下载信息表语句
     */
    public static final String CREATE_DOWNINFO="create table DownInfo ("
            + "id integer primary key autoincrement, "
            + "threadId text, "
            + "startPos integer, "
            + "endPos integer, "
            + "compeleteSize integer, "
            + "url text, "
            + "versionCode integer)";

    private String DB_NAME;
    private Context context;

    public MySQLiteOpenHelper(Context mcontext, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(mcontext, name, factory, version);
        DB_NAME=name;
        context=mcontext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        switch (DB_NAME){
            case UserHistoryDB.DB_NAME_USERHISTORY:
                db.execSQL(CREATE_USERHISTORY);//创建登录用户历史表
                break;
            case DownInfoDB.DB_NAME_DOWNINFO:
                db.execSQL(CREATE_DOWNINFO);//创建DownInfo表
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
