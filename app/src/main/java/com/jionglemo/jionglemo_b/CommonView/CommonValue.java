package com.jionglemo.jionglemo_b.CommonView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

/**
 * Created by Mike on 2016/6/23.
 */
public class CommonValue {

    //服务器基本地址
    public static String serverBasePath="http://static.cdn.jlemo.com/";

    //用户头像本地存放路径
    public static String photoPath=Environment.getExternalStorageDirectory().getPath()+"/JiongLeMo/";

    //用户相关标志记录的文件名
    public static String Tag="Tag";

    //用户相关信息存储的文件名
    public static String UserMessage="UserMessage";

    //获取是否为第一次安装
    public static boolean getFirstSetup(Context context){
        SharedPreferences pref = context.getSharedPreferences(Tag, 0);
        return pref.getBoolean("firstSetup",false);
    }

    //获取用户是否为登录状态
    public static boolean getLoadState(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getBoolean("loadState",false);
    }

    //获取用户ID
    public static int getUser_id(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getInt("user_id",0);
    }

    //获取jid
    public static int getJid(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getInt("jid",0);
    }

    //获取用户名
    public static String getUserName(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("userName", "囧了么001号");
    }

    //获取用户头像路径
    public static String getPortraitPath(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("portrait", null);
    }

    //获取用户融云Token
    public static String getRongToken(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("rong_token", null);
    }

    //获取导购id
    public static int getSalesId(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getInt("sales_id", 0);
    }

    //获取导购所在店铺的id
    public static int getStoreId(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getInt("store_id", 0);
    }

    //获取用户性别
    public static String getUserSex(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("sex", "0");
    }

    //获取用户性取向
    public static String getUserSexOrientation(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("sexOrientation", "0");
    }

    //获取用户出生年份
    public static String getUserYear(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("year", "1986");
    }

    //获取用户出生年份
    public static String getUserMonth(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("month", "1");
    }

    //获取用户出生年份
    public static String getUserDay(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("day", "1");
    }

    //获取用户所在城市
    public static String getUserAdress(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("city", "广州");
    }

    //获取导购现有囧币
    public static int getCurrencyMoney(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getInt("CurrencyMoney", 0);
    }

    //获取导购所得全部囧币
    public static int getTotalMoney(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getInt("TotalMoney", 0);
    }

    //获取用户积分
    public static int getUserIntegral(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getInt("integral", 0);
    }

    //获取用户隐私密码
    public static String getProtectPassword(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("protectPassword", "");
    }

    //获取用户请求Key
    public static String getAuthKey(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("authKey", "");
    }

    //获取用户登录账号
    public static String getLoadPhone(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("loadPhone", "");
    }

    //获取用户登录密码
    public static String getLoadPassword(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getString("loadPassword", "");
    }

    //获取用户身份
    public static int getStype(Context context){
        SharedPreferences pref = context.getSharedPreferences(UserMessage, 0);
        return pref.getInt("stype", 1);//stype：1店铺登录；2导购登录
    }


    //public static String testPath = "http://api.store.jlemo.com/";
    public static String testPath = "http://test.api.store.jionglemo.com/";
    public static String testAppPath = "http://test.api.app.jionglemo.com/";
    //public static String testaAppPath = "http://api.app.jlemo.com/";

    public static String login= testPath + "Main/login";//登录
    public static String findPassword= testPath + "Main/updateUserInfo";//找回密码
    public static String storeHomepage= testPath + "Index/index";//店铺首页
    public static String getChatUserInfo = testPath + "Live/getUserInfo";//聊天用户信息
    public static String getChatToken= testPath + "StoreSalesChat/index";//聊天获取token
    public static String getLivePath= testPath + "Live/index";//导购直播
    public static String closeLive= testPath + "Live/closeLive";//导购结束直播
    public static String salesMessage= testPath + "StoreSales/index";//导购信息
    public static String orderCount= testPath + "Order/orderCount";//订单数量
    public static String orderList= testPath + "Order/index";//店铺订单列表
    public static String updateOrderInfo= testPath + "Order/updateOrderInfo";//更新订单价格
    public static String deliverGoods= testPath + "Order/deliverGoods";//发货
    public static String delOrder= testPath + "Order/delOrder";//删除订单
    public static String storeGiftList= testPath + "StoreGift/index";//送礼物列表
    public static String storeGiftRankingList= testPath + "StoreGift/rankingList";//送礼物排行榜
    public static String getEvaluateList= testPath + "Order/getEvaluateList";//评价列表
    public static String getEvaluateCount= testPath + "Order/getEvaluateCount";//评价数量
    public static String replyUserEvaluate= testPath + "Order/replyUserEvaluate";//回复用户评价
    public static String productList= testPath + "Product/index";//商品列表
    public static String getProductClassify= testPath + "Product/getProductClassify";//分类列表
    public static String updateProduct= testPath + "Product/updateProduct";//商品上下架
    public static String classifyProduct= testPath + "Product/productList";//分类获取商品
    public static String getProductDetails= testAppPath +"ProductDetails/getProductDetails";//网页产品详情（与用户版共用）
    public static String liveNum= testPath + "Live/userNumber";//直播在线人数
    public static String getUserList= testPath + "Live/getUserList";//直播用户列表
    public static String queryExpress= testAppPath +"Express/queryExpress";//查询快递（与用户版共用）
    public static String storeUrl= testAppPath +"VirtualStore/storeUrl";//虚拟商店（与用户版共用）
    public static String shelfProductList= testPath + "Product/shelfProductList";//虚拟商店商品列表
    public static String fensiList= testPath + "StoreFans/index";//粉丝列表
    public static String getStoreSalesInfo= testPath + "StoreSales/getStoreSalesInfo";//导购登录获取导购信息
    public static String getNewApkInfo= testAppPath +"App/index";//安卓版本更新
    public static String groupUserGag= testPath + "StoreSalesChat/groupUserGag";//直播聊天室禁言
    public static String getStoreInfo= testPath + "Store/getStoreInfo";//店铺登录获取店铺信息
    public static String logout= testPath + "Store/Logout";//退出登录
    public static String serviceJid= testAppPath +"app/serviceJid";//囧了么客服（与用户版共用）
    public static String orderDetail= testPath + "Order/orderDetails";//订单详情
}