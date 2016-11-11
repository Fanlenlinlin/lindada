package com.jionglemo.jionglemo_b.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guodingyuan on 2015/9/28.
 */
public class UserHistoryDB {
    //数据库名
    public static final String DB_NAME_USERHISTORY = "user_history";
    //数据库版本
    public static final int VERSION = 1;
    private static UserHistoryDB userHistoryDB;
    private SQLiteDatabase db;

    //将构造方法私有化
    private UserHistoryDB(Context context) {
        MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(context, DB_NAME_USERHISTORY, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取UserHistoryDB的实例。
     */
    public synchronized static UserHistoryDB getInstance(Context context) {
        if (userHistoryDB == null) {
            userHistoryDB = new UserHistoryDB(context);
        }
        return userHistoryDB;
    }

    /**
     * 将登录用户存储到数据库。
     */
    public void saveUserHistory(String phone) {
        checkUserPhone(phone);//检查是否存在，如果之前已经存在，则删除，这样才能保证最后登录的排在最前
        Cursor cursor = db.query("UserHistory", null, null, null, null,null,null);
        int count=cursor.getCount();
        if(count>=8){//最多只允许存储8条数据
            cursor.moveToFirst();
            deleteUserHistoryByContent(cursor.getString(cursor.getColumnIndex("user_phone")));//如果记录已有8条，则删除第一条记录
        }
        cursor.close();
        if (phone != null) {
            ContentValues values = new ContentValues();
            values.put("user_phone",phone);
            db.insert("UserHistory", null, values);
        }
    }

    /**
     * 查询数据库中是否有某个用户
     */
    public void checkUserPhone(String phone){
        Cursor cursor = db.query("UserHistory", null, "user_phone = ?", new String[]{ phone }, null,null,null);
        if(cursor.moveToFirst()){//如果之前已经存在，则删除，这样才能保证最后登录的排在最前
            cursor.close();
            deleteUserHistoryByContent(phone);
        }
    }

    /**
     * 从数据库读取用户历史的所有信息。
     */
    public List<String> loadUserHistory() {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query("UserHistory", null, null, null, null,null,"id desc");//倒序查询
        if (cursor.moveToFirst()) {
            do {
                String phone=cursor.getString(cursor.getColumnIndex("user_phone"));
                list.add(phone);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 删除用户历史的某条数据
     */
    public void deleteUserHistoryByContent(String phone) {
        db.delete("UserHistory","user_phone = ?",new String[]{ phone });
    }
}
