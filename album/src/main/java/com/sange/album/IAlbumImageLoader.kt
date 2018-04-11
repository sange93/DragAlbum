package com.sange.album

import android.widget.ImageView

/**
 * 图片加载接口（必须实现）
 * Created by ssq on 2018/4/11.
 */
interface IAlbumImageLoader {
    /**
     * 加载图片 可选用第三方图片加载框架 来实现
     */
    fun displayImage(url: String, imageView: ImageView)
}