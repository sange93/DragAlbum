package com.sange.album

import android.view.View

/**
 * 拖动相册的item点击监听
 * Created by ssq on 2018/2/26.
 */
interface IOnItemClickListener {
    fun onItemClick(view: View, position: Int, isPhoto: Boolean)
}