package com.sange.dragalbum.util.imageLoader

import android.graphics.Bitmap
import android.widget.ImageView
import java.io.File

/**
 *  图片加载接口（第三方框架需要实现此接口）
 * Created by ssq on 2018/3/7.
 */
interface ILoader {
    /**
     * 实例化第三方框架
     * @param any 上下文或者其他
     */
    fun with(any: Any)

    /**
     * 加载图片
     */
    fun displayImage(url: Any, imageView: ImageView)

    /**
     * 加载圆形图片
     */
    fun displayCircleImage(url: Any, imageView: ImageView)

    /**
     * 获取图片 bitmap (线程同步)
     */
    fun getBitmapSync(url: Any) : Bitmap

    /**
     * 获取图片 bitmap (线程同步)
     */
    fun getBitmapSync(url: Any, width: Int, height: Int) : Bitmap

    /**
     * 获取图片 bitmap
     */
    fun getBitmap(url: Any, callBack: ILoaderCallBack<Bitmap>)

    /**
     * 获取图片 bitmap
     */
    fun getBitmap(url: Any, callBack: ILoaderCallBack<Bitmap>, width: Int, height: Int)

    /**
     * 获取图片 file
     */
    fun getFile(url: Any, callBack: ILoaderCallBack<File>)
}