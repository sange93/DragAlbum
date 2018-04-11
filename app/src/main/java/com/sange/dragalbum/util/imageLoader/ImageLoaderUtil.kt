package com.sange.dragalbum.util.imageLoader

import android.graphics.Bitmap
import android.widget.ImageView
import java.io.File

/**
 * 图片加载 工具类
 * Created by ssq on 2018/3/7.
 */
class ImageLoaderUtil {

    companion object {
        // 更换第三方框架时需要修改实现类
        private val mImageLoader: ILoader by lazy { GlideLoaderImpl() }

        fun with(any: Any): ImageLoaderUtil {
            mImageLoader.with(any)
            return ImageLoaderUtil()
        }
    }

    /**
     * 加载图片（相对路径）
     * 先分析本地是否有资源
     */
//    fun displayImageByRelativePath(relativePath: String, imageView: ImageView){
//        val url = FileUtil.getInstance().isFileDownloaded(relativePath)
//        displayImage(url,imageView)
//    }

    /**
     * 加载图片(绝对路径)
     */
    fun displayImage(url: String, imageView: ImageView) {
        mImageLoader.displayImage(url, imageView)
    }

    /**
     * 加载圆形图片
     */
    fun displayCircleImage(url: String, imageView: ImageView) {
        mImageLoader.displayCircleImage(url, imageView)
    }

    /**
     * 获取图片 bitmap (线程同步)
     */
    fun getBitmapSync(url: Any) : Bitmap {
        return mImageLoader.getBitmapSync(url)
    }

    /**
     * 获取图片 bitmap (线程同步)
     */
    fun getBitmapSync(url: Any, width: Int, height: Int) : Bitmap {
        return mImageLoader.getBitmapSync(url, width, height)
    }

    /**
     * 获取图片 bitmap
     */
    fun getBitmap(url: Any, callBack: ILoaderCallBack<Bitmap>) {
        mImageLoader.getBitmap(url, callBack)
    }

    /**
     * 获取图片 bitmap
     */
    fun getBitmap(url: Any, callBack: ILoaderCallBack<Bitmap>, width: Int, height: Int) {
        mImageLoader.getBitmap(url, callBack,width, height)
    }

    /**
     * 获取图片 file
     */
    fun getFile(url: Any, callBack: ILoaderCallBack<File>) {
        mImageLoader.getFile(url, callBack)
    }
}