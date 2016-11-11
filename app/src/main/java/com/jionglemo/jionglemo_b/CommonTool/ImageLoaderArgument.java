package com.jionglemo.jionglemo_b.CommonTool;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * Created by 郭锭源 on 2016/4/14.
 * 用于配置ImageLoader的相关参数
 */
public class ImageLoaderArgument {

    private static ImageLoader imageLoader;

    /**
     * 获取ImageLoader的实例。
     */
    public synchronized static ImageLoader getInstance(Context context) {
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(getImageLoaderConfiguration(context));//全局初始化此配置
        }
        return imageLoader;
    }

    private static ImageLoaderConfiguration getImageLoaderConfiguration(Context context){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                        //  .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(5)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)//降低线程的优先级保证主UI线程不受太大影响
                .denyCacheImageMultipleSizesInMemory()
               // .memoryCache(new UsingFreqLimitedMemoryCache(10 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(10 * 1024 * 1024)//建议内存设在5-10M,可以有比较好的表现
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(400) //缓存的文件数量
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                        // .writeDebugLogs() // Remove for release app
                .build();//开始构建
        return config;
    }

    public static DisplayImageOptions getDisplayImageOptions(int Resource){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(Resource)
                .showImageForEmptyUri(Resource)
                .showImageOnFail(Resource)
                .cacheOnDisc()//允许缓存至SDcard，可提高滑顺度
                .cacheInMemory()//允许使用内存缓存，能有效解决图片加载闪烁的问题
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型，设置为RGB565比起默认的ARGB_8888要节省大量的内存
                        // .delayBeforeLoading(100)//载入图片前稍做延时可以提高整体滑动的流畅度

                .resetViewBeforeLoading(false)//优化内存
                .build();
        return options;
    }
}
