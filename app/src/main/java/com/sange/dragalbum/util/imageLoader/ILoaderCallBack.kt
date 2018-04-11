package com.sange.dragalbum.util.imageLoader

/**
 * 图片加载 回调
 * Created by ssq on 2018/3/7.
 */
interface ILoaderCallBack<T> {

    /**
     * 加载成功
     * @param resource 结果对象
     */
    fun onSuccess(resource: T)

    /**
     * 加载失败
     */
    fun onFail(msg: String)
}