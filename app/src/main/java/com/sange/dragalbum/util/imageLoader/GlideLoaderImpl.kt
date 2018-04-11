package com.sange.dragalbum.util.imageLoader

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File

/**
 * glide 框架加载 实现类
 * Created by ssq on 2018/3/7.
 */
class GlideLoaderImpl : ILoader {

    private var mRequestManager: RequestManager? = null

    // 头像 配置
    private var mHeadOptions = RequestOptions()
            .centerCrop()
            .transform(CircleCrop())
//            .placeholder(R.drawable.icon_user_head)
//            .error(R.drawable.icon_user_head)
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
    // 一般图片配置
    private var mOptions = RequestOptions()
            .fitCenter()
//            .placeholder(R.drawable.logo_gray)
//            .error(R.drawable.logo_gray)
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    override fun with(any: Any) {
        when (any) {
            is Context -> mRequestManager = Glide.with(any)
            is Activity -> mRequestManager = Glide.with(any)
            is FragmentActivity -> mRequestManager = Glide.with(any)
            is View -> mRequestManager = Glide.with(any)
            is Fragment -> mRequestManager = Glide.with(any)
            is android.support.v4.app.Fragment -> mRequestManager = Glide.with(any)
        }
    }

    override fun displayImage(url: Any, imageView: ImageView) {
        mRequestManager!!.load(url).thumbnail(0.3f).apply(mOptions).into(imageView)
    }

    override fun displayCircleImage(url: Any, imageView: ImageView) {
        mRequestManager!!.load(url).apply(mHeadOptions).into(imageView)
    }

    override fun getBitmapSync(url: Any) : Bitmap {
        return mRequestManager!!.asBitmap().load(url).submit().get()
    }

    override fun getBitmapSync(url: Any, width: Int, height: Int) : Bitmap {
        return mRequestManager!!.asBitmap().load(url).submit(width,height).get()
    }

    override fun getBitmap(url: Any, callBack: ILoaderCallBack<Bitmap>) {
        mRequestManager!!.asBitmap().load(url).into(MyBitmapSimpleTarget(callBack))
    }

    override fun getBitmap(url: Any, callBack: ILoaderCallBack<Bitmap>, width: Int, height: Int) {
        mRequestManager!!.asBitmap().load(url).into(MyBitmapSimpleTarget(callBack, width, height))
    }

    override fun getFile(url: Any, callBack: ILoaderCallBack<File>) {
        mRequestManager!!.asFile().load(url).into(MyFileSimpleTarget(callBack))
    }

    /**
     * Bitmap 回调
     */
    inner class MyBitmapSimpleTarget : SimpleTarget<Bitmap> {
        private var mCallBack: ILoaderCallBack<Bitmap>

        constructor(callBack: ILoaderCallBack<Bitmap>) : super() {
            mCallBack = callBack
        }

        constructor(callBack: ILoaderCallBack<Bitmap>, width: Int, height: Int) : super(width, height) {
            mCallBack = callBack
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            mCallBack.onSuccess(resource)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            mCallBack.onFail("加载图片失败")
        }
    }

    /**
     * File 回调
     */
    inner class MyFileSimpleTarget(callBack: ILoaderCallBack<File>) : SimpleTarget<File>() {
        private val mCallBack = callBack

        override fun onResourceReady(resource: File, transition: Transition<in File>?) {
            mCallBack.onSuccess(resource)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            mCallBack.onFail("加载图片失败")
        }
    }
}